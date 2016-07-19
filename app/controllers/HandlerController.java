package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;

import java.util.List;

import play.Logger;

import models.Grower;
import models.Handler;
import models.HandlerBid;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.Status;

import controllers.security.HandlerSecured;
import controllers.security.HandlerSecurityController;

import services.GrowerService;
import services.HandlerService;
import services.HandlerBidService;
import services.messaging.bid.HandlerBidMessageService;
import services.bid_management.FCFSService;
import services.bid_management.WaterfallService;
import services.parsers.GrowerJsonParser;
import services.parsers.HandlerBidJsonParser;
import services.parsers.BidJsonParser.ManagementTypeInfo;

import utils.JsonMsgUtils;
import utils.ResponseHeaders;

@Security.Authenticated(HandlerSecured.class)
public class HandlerController extends Controller {

  private final HandlerService handlerService;
  private final GrowerService growerService;
  private final HandlerBidService handlerBidService;
  private final HandlerBidMessageService bidMessageService;

  private final ObjectMapper jsonMapper;

  @Inject
  public HandlerController(
      HandlerService handlerService,
      GrowerService growerService,
      HandlerBidService handlerBidService,
      HandlerBidMessageService bidMessageService) {
    this.handlerService = handlerService;
    this.growerService = growerService;
    this.handlerBidService = handlerBidService;
    this.bidMessageService = bidMessageService;

    this.jsonMapper = new ObjectMapper();
  }

  public Result getHandler() {
    ResponseHeaders.addResponseHeaders(response());

    Handler handler = HandlerSecurityController.getHandler();

    if (handler == null) {
      return handlerNotFound();
    }

    try {
      return ok(jsonMapper.writeValueAsString(handler));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  // Annotation ensures that POST request is of type application/json. If not HTTP 400 response
  // returned.
  @BodyParser.Of(BodyParser.Json.class)
  public Result createGrower() {
    ResponseHeaders.addResponseHeaders(response());

    Handler handler = HandlerSecurityController.getHandler();

    if (handler == null) {
      return handlerNotFound();
    }

    JsonNode data = request().body().asJson();

    if (data == null) {
      return badRequest(JsonMsgUtils.expectingData());
    }

    addCurrentHandlerId(handler, data);

    GrowerJsonParser parser = new GrowerJsonParser(data);

    if (!parser.isValid()) {
      return badRequest(JsonMsgUtils.caughtException(parser.getErrorMessage()));
    }

    if (!parser.getHandler().equals(handler)) {
      JsonMsgUtils.caughtException(
          "Can only create growers that belong to "
          + handler.getCompanyName() + ".");
    }

    Grower grower = parser.formGrower();
    grower.save();

    try {
      return created(jsonMapper.writeValueAsString(grower));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }

  }

  public Result getAllGrowers() {
    ResponseHeaders.addResponseHeaders(response());

    Handler handler = HandlerSecurityController.getHandler();

    if (handler == null) {
      return handlerNotFound();
    }

    try {
      return ok(jsonMapper.writeValueAsString(growerService.getByHandler(handler.getId())));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  public Result getGrower(long growerId) {
    ResponseHeaders.addResponseHeaders(response());

    Handler handler = HandlerSecurityController.getHandler();

    if (handler == null) {
      return handlerNotFound();
    }

    Grower grower = growerService.getById(growerId);
    if (grower == null) {
      return notFound(JsonMsgUtils.growerNotFoundMessage(growerId));
    }

    if (!handlerService.checkHandlerOwnsGrower(handler, grower)) {
      return badRequest(JsonMsgUtils.handlerDoesNotOwnGrowerMessage(handler, grower));
    }

    try {
      return ok(jsonMapper.writeValueAsString(grower));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  public Result deleteGrower(long growerId) {
    ResponseHeaders.addResponseHeaders(response());

    Handler handler = HandlerSecurityController.getHandler();

    if (handler == null) {
      return handlerNotFound();
    }

    Grower grower = growerService.getById(growerId);
    if (grower == null) {
      return notFound(JsonMsgUtils.growerNotFoundMessage(growerId));
    }

    if (!handlerService.checkHandlerOwnsGrower(handler, grower)) {
      return badRequest(JsonMsgUtils.handlerDoesNotOwnGrowerMessage(handler, grower));
    }

    for(HandlerBid handlerBid: handlerBidService.getByGrower(growerId)) {
      if(handlerBid.bidCurrentlyOpen()) {
        //Conflict response
        return status(409, JsonMsgUtils.growerInBid(growerId, handlerBid.getId()));
      }
    }

    grower.delete();
    return ok(JsonMsgUtils.growerDeleted(growerId));
  }

  public Result updateGrower(long growerId) {
    ResponseHeaders.addResponseHeaders(response());

    Handler handler = HandlerSecurityController.getHandler();

    if (handler == null) {
      return handlerNotFound();
    }

    JsonNode data = request().body().asJson();

    if (data == null) {
      return badRequest(JsonMsgUtils.expectingData());
    }

    addCurrentHandlerId(handler, data);

    Grower grower = growerService.getById(growerId);
    if (grower == null) {
      return notFound(JsonMsgUtils.growerNotFoundMessage(growerId));
    }

    GrowerJsonParser parser = new GrowerJsonParser(data);

    if (!parser.isValid()) {
      return badRequest(JsonMsgUtils.caughtException(parser.getErrorMessage()));
    }

    if (!parser.getHandler().equals(handler)) {
      JsonMsgUtils.caughtException(
          "Can only update growers that belong to "
          + handler.getCompanyName() + ".");
    }

    if (!handlerService.checkHandlerOwnsGrower(handler, grower)) {
      return badRequest(JsonMsgUtils.handlerDoesNotOwnGrowerMessage(handler, grower));
    }

    for(HandlerBid handlerBid: handlerBidService.getByGrower(growerId)) {
      if(handlerBid.bidCurrentlyOpen()) {
        //Conflict response
        return status(409, JsonMsgUtils.growerInBid(growerId, handlerBid.getId()));
      }
    }

    parser.updateGrower(grower);
    grower.save();

    try {
      return created(jsonMapper.writeValueAsString(grower));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  // Annotation ensures that POST request is of type application/json. If not HTTP 400 response
  // returned.
  @BodyParser.Of(BodyParser.Json.class)
  public Result createBid() {
    ResponseHeaders.addResponseHeaders(response());

    Handler handler = HandlerSecurityController.getHandler();

    if (handler == null) {
      return handlerNotFound();
    }

    JsonNode data = request().body().asJson();

    if (data == null) {
      return badRequest(JsonMsgUtils.expectingData());
    }

    addCurrentHandlerId(handler, data);

    HandlerBidJsonParser parser = new HandlerBidJsonParser(data);

    if (!parser.isValid()) {
      return badRequest(JsonMsgUtils.caughtException(parser.getErrorMessage()));
    }

    if (!parser.getHandler().equals(handler)) {
      JsonMsgUtils.caughtException(
          "Can only create bids that belong to "
          + handler.getCompanyName() + ".");
    }

    HandlerBid handlerBid = parser.formBid();
    handlerBid.save();

    ManagementTypeInfo managementType = parser.getManagementType();
    Class<?> classType = managementType.getClassType();

    if (classType == WaterfallService.class) {
      new WaterfallService(handlerBid, managementType.getDelay());
    } else if (classType == FCFSService.class) {
      new FCFSService(handlerBid, managementType.getDelay());
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

  public Result updateBid(long bidId) {
    ResponseHeaders.addResponseHeaders(response());

    Handler handler = HandlerSecurityController.getHandler();

    if (handler == null) {
      return handlerNotFound();
    }

    JsonNode data = request().body().asJson();

    if (data == null) {
      return badRequest(JsonMsgUtils.expectingData());
    }

    addCurrentHandlerId(handler, data);

    HandlerBidJsonParser parser = new HandlerBidJsonParser(data);

    if (!parser.isValid()) {
      return badRequest(JsonMsgUtils.caughtException(parser.getErrorMessage()));
    }

    if (!parser.getHandler().equals(handler)) {
      JsonMsgUtils.caughtException(
          "Can only create bids that belong to "
          + handler.getCompanyName() + ".");
    }
  
    HandlerBid handlerBid = handlerBidService.getById(bidId);
    parser.updateBid(handlerBid);
    handlerBid.save();

    try {
      return created(jsonMapper.writeValueAsString(handlerBid));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }    
  }

  public Result deleteBid(long bidId) {
    ResponseHeaders.addResponseHeaders(response());

    Handler handler = HandlerSecurityController.getHandler();

    if (handler == null) {
      return handlerNotFound();
    }

    HandlerBid handlerBid = handlerBidService.getById(bidId);
    if (handlerBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }

    if (!handlerService.checkHandlerOwnsBid(handler, handlerBid)) {
      return badRequest(JsonMsgUtils.handlerDoesNotOwnBidMessage(handler, handlerBid));
    }

    handlerBid.delete();
    return ok(JsonMsgUtils.bidDeleted(bidId));
  }

  public Result getAllBids() {
    ResponseHeaders.addResponseHeaders(response());

    Handler handler = HandlerSecurityController.getHandler();

    if (handler == null) {
      return handlerNotFound();
    }

    List<HandlerBid> handlerBids = handlerBidService.getByHandler(handler.getId());

    // handlerBids will be null if Hanlder with handlerId cannot be found.
    if (handlerBids == null) {
      return notFound(JsonMsgUtils.handlerNotFoundMessage(handler.getId()));
    }

    try {
      return ok(jsonMapper.writeValueAsString(handlerBids));
    } catch (JsonProcessingException e) {
      Logger.error(e.toString());
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  public Result getBid(long bidId) {
    ResponseHeaders.addResponseHeaders(response());

    Handler handler = HandlerSecurityController.getHandler();

    if (handler == null) {
      return handlerNotFound();
    }

    HandlerBid handlerBid = handlerBidService.getById(bidId);
    if (handlerBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }

    if (!handlerService.checkHandlerOwnsBid(handler, handlerBid)) {
      return badRequest(JsonMsgUtils.handlerDoesNotOwnBidMessage(handler, handlerBid));
    }

    try {
      return ok(jsonMapper.writeValueAsString(handlerBid));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  public Result getGrowersBids(long growerId) {
    ResponseHeaders.addResponseHeaders(response());

    Handler handler = HandlerSecurityController.getHandler();

    if (handler == null) {
      return handlerNotFound();
    }

    Grower grower = growerService.getById(growerId);
    if (grower == null) {
      return notFound(JsonMsgUtils.growerNotFoundMessage(growerId));
    }

    if (!handlerService.checkHandlerOwnsGrower(handler, grower)) {
      return badRequest(JsonMsgUtils.handlerDoesNotOwnGrowerMessage(handler, grower));
    }

    List<HandlerBid> handlerBids = handlerBidService.getByGrower(growerId);
    if (handlerBids == null) {
      return notFound(
          JsonMsgUtils.caughtException("Could not get bids for grower with id " + growerId));
    }

    try {
      return ok(jsonMapper.writeValueAsString(handlerBids));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }
  
  public Result sendBid(long id) {
    ResponseHeaders.addResponseHeaders(response());

    Handler handler = HandlerSecurityController.getHandler();

    if (handler == null) {
      return handlerNotFound();
    }
    
    HandlerBid handlerBid = handlerBidService.getById(id);
    if (handlerBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(id));
    }

    if (!handlerService.checkHandlerOwnsBid(handler, handlerBid)) {
      return badRequest(JsonMsgUtils.handlerDoesNotOwnBidMessage(handler, handlerBid));
    }
    
    boolean emailSuccess = bidMessageService.send(handlerBid);


    return emailSuccess
        ? ok(JsonMsgUtils.successfullEmail())
        : internalServerError(JsonMsgUtils.emailsNotSent());
  }

  private Result handlerNotFound() {
    return redirect("/handler/logout");
  }

  private void addCurrentHandlerId(Handler handler, JsonNode data) {
    if (data.isObject()) {
      // TODO Change String to Literal to Constant.
      ((ObjectNode) data).put("handler_id", handler.getId());
    }
  }
}
