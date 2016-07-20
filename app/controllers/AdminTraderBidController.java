package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import java.util.List;

import controllers.security.AdminSecured;

import models.TraderBid;

import models.BidResponseResult;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import services.TraderBidService;
import services.messaging.bid.HandlerBidMessageService;
import services.bid_management.FCFSService;
import services.parsers.TraderBidJsonParser;
import services.parsers.BidJsonParser.ManagementTypeInfo;

import utils.JsonMsgUtils;

import play.Logger;

import services.bid_management.WaterfallService;

public class AdminTraderBidController extends Controller {

  private final TraderBidService traderBidService;
  private final HandlerBidMessageService bidMessageService;

  private final ObjectMapper jsonMapper;

  @Inject
  public AdminTraderBidController(TraderBidService traderBidService,
      HandlerBidMessageService bidMessageService) {
    this.traderBidService = traderBidService;
    this.bidMessageService = bidMessageService;

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
  public Result sendBid(long id) {
    TraderBid traderBid = traderBidService.getById(id);
    // boolean emailSuccess = bidMessageService.send(traderBid);

    // return emailSuccess
    //     ? ok(JsonMsgUtils.successfullEmail())
    //     : internalServerError(JsonMsgUtils.emailsNotSent());
    return ok("In Progress.");
  }

  // Annotation ensures that POST request is of type application/json. If not HTTP 400 response
  // returned.
  @Security.Authenticated(AdminSecured.class)
  @BodyParser.Of(BodyParser.Json.class)
  public Result createBid() {
    JsonNode data = request().body().asJson();

    if (data == null) {
      return badRequest(JsonMsgUtils.expectingData());
    }

    TraderBidJsonParser parser = new TraderBidJsonParser(data);

    if (!parser.isValid()) {
      return badRequest(JsonMsgUtils.caughtException(parser.getErrorMessage()));
    }

    TraderBid traderBid = parser.formBid();
    traderBid.save();

    // ManagementTypeInfo managementType = parser.getManagementType();
    // Class<?> classType = managementType.getClassType();

    // if (classType == WaterfallService.class) {
    //   new WaterfallService(handlerBid, managementType.getDelay());
    // } else if (classType == FCFSService.class) {
    //   new FCFSService(handlerBid, managementType.getDelay());
    // } else {
    //   return internalServerError(JsonMsgUtils.caughtException(classType.getName() 
    //     + " management type not found\n"));
    // }

    try {
      return created(jsonMapper.writeValueAsString(traderBid));
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
  public Result getAllBids() {
    try {
      return ok(jsonMapper.writeValueAsString(traderBidService.getAll()));
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
  public Result deleteBid(long id) {
    TraderBid traderBid = traderBidService.getById(id);

    if (traderBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(id));
    }

    traderBid.delete();
    return ok(JsonMsgUtils.bidDeleted(id));
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
