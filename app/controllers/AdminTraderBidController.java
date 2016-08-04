package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import controllers.security.AdminSecured;

import models.TraderBid;
import models.Batch;
import models.HandlerSeller;

import models.BidResponseResult;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import services.TraderBidService;
import services.TraderService;
import services.impl.EbeanTraderService;
import services.HandlerSellerService;
import services.BatchService;
import services.messaging.bid.BatchSMSMessageService;
import services.messaging.bid.BatchSendGridMessageService;
import services.bid_management.TraderFCFSService;
import services.parsers.TraderBidJsonParser;
import services.parsers.TraderBidJsonParser.TraderManagementTypeInfo;

import utils.JsonMsgUtils;

import play.Logger;

import services.bid_management.WaterfallService;

public class AdminTraderBidController extends Controller {

  private final TraderBidService traderBidService;
  private final BatchSMSMessageService batchSMSMessageService;
  private final BatchSendGridMessageService batchSendGridMessageService;
  private final BatchService batchService;
  private final HandlerSellerService handlerSellerService;

  private final ObjectMapper jsonMapper;

  @Inject
  public AdminTraderBidController(TraderBidService traderBidService,
          BatchSMSMessageService batchSMSMessageService,
          BatchSendGridMessageService batchSendGridMessageService,
          BatchService batchService,
          HandlerSellerService handlerSellerService) {
    this.traderBidService = traderBidService;
    this.batchSMSMessageService = batchSMSMessageService;
    this.batchSendGridMessageService = batchSendGridMessageService;
    this.batchService = batchService;
    this.handlerSellerService = handlerSellerService;

    this.jsonMapper = new ObjectMapper();
  }

  // TODO Not Secured. Implement non-admin means of responding to bid.
  public Result acceptBid(long bidId, long handlerSellerId) {
    TraderBid traderBid = traderBidService.getById(bidId);
    if (traderBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }

    BidResponseResult success =
      traderBid.handlerSellerAcceptBid(handlerSellerId, traderBid.getAlmondPounds());
    
    return success.isValid() ? ok(JsonMsgUtils.successfullAccept())
        : internalServerError(JsonMsgUtils.bidNotAccepted(success.getInvalidResponseMessage()));
  }

  // TODO Not Secured. Implement non-admin means of responding to bid.
  public Result rejectBid(long bidId, long handlerSellerId) {
    TraderBid traderBid = traderBidService.getById(bidId);
    if (traderBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }

    BidResponseResult success = traderBid.handlerSellerRejectBid(handlerSellerId);

    return success.isValid() ? ok(JsonMsgUtils.successfullReject())
        : internalServerError(JsonMsgUtils.bidNotRejected(success.getInvalidResponseMessage()));
  }

  // TODO Not Secured. Implement non-admin means of responding to bid.
  public Result requestCall(long bidId, long handlerSellerId) {
    TraderBid traderBid = traderBidService.getById(bidId);
    if (traderBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(handlerSellerId));
    }

    BidResponseResult success = traderBid.handlerSellerRequestCall(handlerSellerId);

    return success.isValid() ? ok(JsonMsgUtils.successfullCallRequest())
        : internalServerError(JsonMsgUtils.callNotRequested(success.getInvalidResponseMessage()));
  }


  @Security.Authenticated(AdminSecured.class)
  public Result sendBatch(long id) {
    Batch batch = batchService.getById(id);
    return sendBatch(batch);
  }

  @Security.Authenticated(AdminSecured.class)
  private Result sendBatch(Batch batch) {
    boolean emailSuccess 
      = batchSendGridMessageService.send(batch) && batchSMSMessageService.send(batch);

    return emailSuccess
        ? ok(JsonMsgUtils.successfullEmail())
        : internalServerError(JsonMsgUtils.messagesNotSent());
  }

  @Security.Authenticated(AdminSecured.class)
  @BodyParser.Of(BodyParser.Json.class)
  public Result createBatch(long traderId) {
    JsonNode data = request().body().asJson();

    if (data == null) {
      return badRequest(JsonMsgUtils.expectingData());
    }
    if(!data.isArray()) {
      return badRequest(JsonMsgUtils.expectingArray());
    }

    List<TraderBid> processedTraderBids = new ArrayList<>();
    for(JsonNode singleBidNode: data) {
      TraderBidJsonParser parser = new TraderBidJsonParser(singleBidNode);
      
      if (!parser.isValid()) {
        return badRequest(JsonMsgUtils.caughtException(parser.getErrorMessage()));
      }

      TraderBid traderBid = parser.formBid();
      traderBid.save();

      TraderManagementTypeInfo managementType = parser.getManagementType();
      Class<?> classType = managementType.getClassType();

      if (classType == TraderFCFSService.class) {
        new TraderFCFSService(traderBid, managementType.getDelay());
      } else {
        return internalServerError(JsonMsgUtils.caughtException(classType.getName() 
          + " management type not found for Bid " + Long.toString(traderBid.getId()) 
          + "\n"));
      }

      processedTraderBids.add(traderBid);
      traderBid.save();
    }

    TraderService traderService = new EbeanTraderService();
    Batch batch = new Batch(traderService.getById(traderId), processedTraderBids);
    batch.save();

    Result emailResult = sendBatch(batch.getId());
    if(emailResult.status() != 200) {
      return emailResult;
    }

    try {
      return created(jsonMapper.writeValueAsString(batch));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  @Security.Authenticated(AdminSecured.class)
  @BodyParser.Of(BodyParser.Json.class)
  public Result updateBid(long id) {
    JsonNode data = request().body().asJson();

    if(data == null) {
      return badRequest(JsonMsgUtils.expectingData());
    }

    TraderBidJsonParser parser = new TraderBidJsonParser(data);

    if(!parser.isValid()) {
      return badRequest(JsonMsgUtils.caughtException(parser.getErrorMessage()));
    }

    TraderBid traderBid = traderBidService.getById(id);
    parser.updateBid(traderBid);
    traderBid.save();

    try {
      return created(jsonMapper.writeValueAsString(traderBid));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  @Security.Authenticated(AdminSecured.class)
  @BodyParser.Of(BodyParser.Json.class)
  public Result addHandlerSellersToBid(long bidId) {

    TraderBid traderBid = traderBidService.getById(bidId);
    if(traderBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }

    JsonNode data = request().body().asJson();

    if (data == null) {
      return badRequest(JsonMsgUtils.expectingData());
    }

    if(!data.isArray()) {
      //Log error, return badResult or something
    }

    List<HandlerSeller> addedHandlerSellers = new ArrayList<>();
    for(JsonNode node : data) {
      Long handlerSellerId = Long.parseLong(node.asText());

      Logger.info("\nHandler Seller Id: " + handlerSellerId);

      HandlerSeller handlerSeller = handlerSellerService.getById(handlerSellerId);
      
      if(handlerSeller == null) {
        return notFound(JsonMsgUtils.handlerSellerNotFoundMessage(handlerSellerId));
      }
      addedHandlerSellers.add(handlerSeller);
    }

    traderBid.addHandlerSellers(addedHandlerSellers);
    traderBid.save();

    List<TraderBid> processedTraderBids = Collections.singletonList(traderBid);

    Result sendResult = sendBatch(new Batch(traderBid.getTrader(), processedTraderBids));
    if(sendResult.status() != 200) {
      return sendResult;
    }

    try {
      return created(jsonMapper.writeValueAsString(traderBid));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    } 
  }

  @Security.Authenticated(AdminSecured.class)
  public Result getAllBids() {
    List<TraderBid> traderBids = traderBidService.getAll();
    traderBids.sort((bid1, bid2) -> bid2.getCreatedAt().compareTo(bid1.getCreatedAt()));
    try {
      return ok(jsonMapper.writeValueAsString(traderBids));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    } 
  }

  @Security.Authenticated(AdminSecured.class)
  public Result getBid(long id) {
    TraderBid traderBid = traderBidService.getById(id);

    if (traderBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(id));
    }

    try {
      return ok(jsonMapper.writeValueAsString(traderBid));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  @Security.Authenticated(AdminSecured.class)
  public Result getAllBatches() {
    try {
      return ok(jsonMapper.writeValueAsString(batchService.getAll()));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    } 
  }  

  @Security.Authenticated(AdminSecured.class) 
  public Result getBatch(long id) {
    Batch batch = batchService.getById(id);

    if(batch == null) {
      return notFound(JsonMsgUtils.batchNotFoundMessage(id)); 
    }

    try {
      return ok(jsonMapper.writeValueAsString(batch));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }  
  }

  @Security.Authenticated(AdminSecured.class)
  public Result deleteBid(long id) {
    TraderBid traderBid = traderBidService.getById(id);

    if (traderBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(id));
    }

    traderBid.delete();
    return ok(JsonMsgUtils.bidDeleted(id));
  }  

  @Security.Authenticated(AdminSecured.class)
  public Result closeBid(long id) {
    TraderBid traderBid = traderBidService.getById(id);

    if (traderBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(id));
    }

    traderBid.manualCloseBid();
    return ok(JsonMsgUtils.bidClosed(id));
  }  

  @Security.Authenticated(AdminSecured.class)
  public Result getAllTraderBids(long traderId) {
    List<TraderBid> traderBids = traderBidService.getByTrader(traderId);

    // handlerBids will be null if Hanlder with handlerId cannot be found.
    if (traderBids == null) {
      return notFound(JsonMsgUtils.traderNotFoundMessage(traderId));
    }
    
    try {
      return ok(jsonMapper.writeValueAsString(traderBids));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  @Security.Authenticated(AdminSecured.class)
  public Result getAllHandlerSellerBids(long handlerSellerId) {
    List<TraderBid> handlerSellerBids =
      traderBidService.getByHandlerSeller(handlerSellerId);

    // handlerSellerBids will be null if HandlerSeller with handlerSellerId cannot be found.
    if (handlerSellerBids == null) {
      return notFound(JsonMsgUtils.handlerSellerNotFoundMessage(handlerSellerId));
    }

    try {
      return ok(jsonMapper.writeValueAsString(handlerSellerBids));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }
}
