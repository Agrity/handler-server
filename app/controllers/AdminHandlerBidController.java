package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import java.util.List;
import java.util.ArrayList;

import controllers.security.AdminSecured;

import models.HandlerBid;
import models.Grower;

import models.BidResponseResult;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import services.HandlerBidService;
import services.GrowerService;
import services.messaging.bid.HandlerBidMessageService;
import services.bid_management.HandlerFCFSService;
import services.parsers.HandlerBidJsonParser;
import services.parsers.HandlerBidJsonParser.HandlerManagementTypeInfo;

import utils.JsonMsgUtils;

import play.Logger;

import services.bid_management.WaterfallService;

public class AdminHandlerBidController extends Controller {

  private final HandlerBidService handlerBidService;
  private final HandlerBidMessageService bidMessageService;
  private final GrowerService growerService;

  private final ObjectMapper jsonMapper;

  @Inject
  public AdminHandlerBidController(HandlerBidService handlerBidService, 
                                  HandlerBidMessageService bidMessageService,
                                  GrowerService growerService) {
    this.handlerBidService = handlerBidService;
    this.bidMessageService = bidMessageService;
    this.growerService = growerService;

    this.jsonMapper = new ObjectMapper();
  }

  // TODO Not Secured. Implement non-admin means of responding to bid.
  public Result acceptBid(long bidId, long growerId) {
    HandlerBid handlerBid = handlerBidService.getById(bidId);
    if (handlerBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }

    // TODO Change to actual pounds accepted once implemented.
    BidResponseResult success = handlerBid.growerAcceptBid(growerId, handlerBid.getAlmondPounds());
    
    return success.isValid() ? ok(JsonMsgUtils.successfullAccept())
        : internalServerError(JsonMsgUtils.bidNotAccepted(success.getInvalidResponseMessage()));
  }

  // TODO Not Secured. Implement non-admin means of responding to bid.
  public Result rejectBid(long bidId, long growerId) {
    HandlerBid handlerBid = handlerBidService.getById(bidId);
    if (handlerBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }

    BidResponseResult success = handlerBid.growerRejectBid(growerId);

    return success.isValid() ? ok(JsonMsgUtils.successfullReject())
        : internalServerError(JsonMsgUtils.bidNotRejected(success.getInvalidResponseMessage()));
  }

  // TODO Not Secured. Implement non-admin means of responding to bid.
  public Result requestCall(long bidId, long growerId) {
    HandlerBid handlerBid = handlerBidService.getById(bidId);
    if (handlerBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(growerId));
    }

    BidResponseResult success = handlerBid.growerRequestCall(growerId);

    return success.isValid() ? ok(JsonMsgUtils.successfullCallRequest())
        : internalServerError(JsonMsgUtils.callNotRequested(success.getInvalidResponseMessage()));
  }

  public Result approveBid(long bidId, long growerId) {
    return ok("Not implemented.");
  }

  public Result disapproveBid(long bidId, long growerId) {
    return ok("Not implemented.");
  }

  @Security.Authenticated(AdminSecured.class)
  public Result sendBid(long id) {
    HandlerBid handlerBid = handlerBidService.getById(id);
    boolean emailSuccess = bidMessageService.send(handlerBid);

    return emailSuccess
        ? ok(JsonMsgUtils.successfullEmail())
        : internalServerError(JsonMsgUtils.emailsNotSent());
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

    HandlerBidJsonParser parser = new HandlerBidJsonParser(data);

    if (!parser.isValid()) {
      return badRequest(JsonMsgUtils.caughtException(parser.getErrorMessage()));
    }

    HandlerBid handlerBid = parser.formBid();
    handlerBid.save();

    HandlerManagementTypeInfo managementType = parser.getManagementType();
    Class<?> classType = managementType.getClassType();

    if (classType == WaterfallService.class) {
      new WaterfallService(handlerBid, managementType.getDelay());
    } else if (classType == HandlerFCFSService.class) {
      new HandlerFCFSService(handlerBid, managementType.getDelay());
    } else {
      return internalServerError(JsonMsgUtils.caughtException(classType.getName() 
        + " management type not found\n"));
    }

    try {
      return created(jsonMapper.writeValueAsString(handlerBid));
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

    HandlerBidJsonParser parser = new HandlerBidJsonParser(data);

    if(!parser.isValid()) {
      return badRequest(JsonMsgUtils.caughtException(parser.getErrorMessage()));
    }

    HandlerBid handlerBid = handlerBidService.getById(id);
    parser.updateBid(handlerBid);
    handlerBid.save();

    try {
      return created(jsonMapper.writeValueAsString(handlerBid));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  @Security.Authenticated(AdminSecured.class)
  @BodyParser.Of(BodyParser.Json.class)
  public Result addGrowersToBid(long bidId) {

    HandlerBid handlerBid = handlerBidService.getById(bidId);
    if(handlerBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }

    if(!handlerBid.bidCurrentlyOpen()) {
      return badRequest(JsonMsgUtils.cantAddSeller(bidId));
    }

    JsonNode data = request().body().asJson();

    if (data == null) {
      return badRequest(JsonMsgUtils.expectingData());
    }

    if(!data.isArray()) {
      //Log error, return badResult or something
    }

    List<Grower> addedGrowers = new ArrayList<>();
    for(JsonNode node : data) {
      Long growerId = Long.parseLong(node.asText());

      Logger.info("\nHandler Seller Id: " + growerId);

      Grower grower = growerService.getById(growerId);
      
      if(grower == null) {
        return notFound(JsonMsgUtils.growerNotFoundMessage(growerId));
      }
      addedGrowers.add(grower);
    }

    handlerBid.addGrowers(addedGrowers);
    handlerBid.save();

    try {
      return created(jsonMapper.writeValueAsString(handlerBid));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    } 
  }

  @Security.Authenticated(AdminSecured.class)
  public Result getAllBids() {
    List<HandlerBid> handlerBids = handlerBidService.getAll();
    handlerBids.sort((bid1, bid2) -> bid2.getCreatedAt().compareTo(bid1.getCreatedAt()));
    try {
      return ok(jsonMapper.writeValueAsString(handlerBids));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    } 
  }

  @Security.Authenticated(AdminSecured.class)
  public Result getBid(long id) {
    HandlerBid handlerBid = handlerBidService.getById(id);

    if (handlerBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(id));
    }

    try {
      return ok(jsonMapper.writeValueAsString(handlerBid));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  @Security.Authenticated(AdminSecured.class)
  public Result deleteBid(long id) {
    HandlerBid handlerBid = handlerBidService.getById(id);

    if (handlerBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(id));
    }

    handlerBid.delete();
    return ok(JsonMsgUtils.bidDeleted(id));
  }  

  @Security.Authenticated(AdminSecured.class)
  public Result closeBid(long id) {
    HandlerBid handlerBid = handlerBidService.getById(id);

    if (handlerBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(id));
    }

    handlerBid.manualCloseBid();
    return ok(JsonMsgUtils.bidClosed(id));
  }

  @Security.Authenticated(AdminSecured.class)
  public Result getAllHandlerBids(long handlerId) {
    List<HandlerBid> handlerBids = handlerBidService.getByHandler(handlerId);

    // handlerBids will be null if Hanlder with handlerId cannot be found.
    if (handlerBids == null) {
      return notFound(JsonMsgUtils.handlerNotFoundMessage(handlerId));
    }
    
    try {
      return ok(jsonMapper.writeValueAsString(handlerBids));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  @Security.Authenticated(AdminSecured.class)
  public Result getAllGrowerBids(long growerId) {
    List<HandlerBid> growerBids = handlerBidService.getByGrower(growerId);

    // growerBids will be null if Grower with growerId cannot be found.
    if (growerBids == null) {
      return notFound(JsonMsgUtils.growerNotFoundMessage(growerId));
    }

    try {
      return ok(jsonMapper.writeValueAsString(growerBids));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }
}
