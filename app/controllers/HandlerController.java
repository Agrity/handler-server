package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;

import java.util.List;

import models.Grower;
import models.Handler;
import models.Offer;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import controllers.security.HandlerSecured;
import controllers.security.HandlerSecurityController;

import services.GrowerService;
import services.HandlerService;
import services.OfferService;
import services.messaging.offer.OfferMessageService;
import services.offer_management.FCFSService;
import services.offer_management.WaterfallService;
import services.parsers.GrowerJsonParser;
import services.parsers.OfferJsonParser;
import services.parsers.OfferJsonParser.ManagementTypeInfo;

import utils.JsonMsgUtils;
import utils.ResponseHeaders;

@Security.Authenticated(HandlerSecured.class)
public class HandlerController extends Controller {

  private final HandlerService handlerService;
  private final GrowerService growerService;
  private final OfferService offerService;
  private final OfferMessageService offerMessageService;

  private final ObjectMapper jsonMapper;

  @Inject
  public HandlerController(
      HandlerService handlerService,
      GrowerService growerService,
      OfferService offerService,
      OfferMessageService offerMessageService) {
    this.handlerService = handlerService;
    this.growerService = growerService;
    this.offerService = offerService;
    this.offerMessageService = offerMessageService;

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

    if (data.isObject()) {
      // TODO Change String to Literal to Constant.
      ((ObjectNode) data).put("handler_id", handler.getId());
    }

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

  // Annotation ensures that POST request is of type application/json. If not HTTP 400 response
  // returned.
  @BodyParser.Of(BodyParser.Json.class)
  public Result createOffer() {
    ResponseHeaders.addResponseHeaders(response());

    Handler handler = HandlerSecurityController.getHandler();

    if (handler == null) {
      return handlerNotFound();
    }

    JsonNode data = request().body().asJson();

    if (data == null) {
      return badRequest(JsonMsgUtils.expectingData());
    }

    OfferJsonParser parser = new OfferJsonParser(data);

    if (!parser.isValid()) {
      return badRequest(JsonMsgUtils.caughtException(parser.getErrorMessage()));
    }

    if (!parser.getHandler().equals(handler)) {
      JsonMsgUtils.caughtException(
          "Can only create offers that belong to "
          + handler.getCompanyName() + ".");
    }

    Offer offer = parser.formOffer();
    offer.save();

    ManagementTypeInfo managementType = parser.getManagementType();
    Class<?> classType = managementType.getClassType();

    if (classType == WaterfallService.class) {
      new WaterfallService(offer, managementType.getDelay());
    } else if (classType == FCFSService.class) {
      new FCFSService(offer, managementType.getDelay());
    } else {
      return internalServerError(JsonMsgUtils.caughtException(classType.getName() 
        + " management type not found\n"));
    }

    try {
      return created(jsonMapper.writeValueAsString(offer));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  public Result getAllOffers() {
    ResponseHeaders.addResponseHeaders(response());

    Handler handler = HandlerSecurityController.getHandler();

    if (handler == null) {
      return handlerNotFound();
    }

    List<Offer> handlerOffers = offerService.getByHandler(handler.getId());

    // handlerOffers will be null if Hanlder with handlerId cannot be found.
    if (handlerOffers == null) {
      return notFound(JsonMsgUtils.handlerNotFoundMessage(handler.getId()));
    }

    try {
      return ok(jsonMapper.writeValueAsString(handlerOffers));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  public Result getOffer(long offerId) {
    ResponseHeaders.addResponseHeaders(response());

    Handler handler = HandlerSecurityController.getHandler();

    if (handler == null) {
      return handlerNotFound();
    }

    Offer offer = offerService.getById(offerId);
    if (offer == null) {
      return notFound(JsonMsgUtils.offerNotFoundMessage(offerId));
    }

    if (!handlerService.checkHandlerOwnsOffer(handler, offer)) {
      return badRequest(JsonMsgUtils.handlerDoesNotOwnOfferMessage(handler, offer));
    }

    try {
      return ok(jsonMapper.writeValueAsString(offer));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }
  
  public Result sendOffer(long id) {
    ResponseHeaders.addResponseHeaders(response());

    Handler handler = HandlerSecurityController.getHandler();

    if (handler == null) {
      return handlerNotFound();
    }
    
    Offer offer = offerService.getById(id);
    if (offer == null) {
      return notFound(JsonMsgUtils.offerNotFoundMessage(id));
    }

    if (!handlerService.checkHandlerOwnsOffer(handler, offer)) {
      return badRequest(JsonMsgUtils.handlerDoesNotOwnOfferMessage(handler, offer));
    }
    
    boolean emailSuccess = offerMessageService.send(offer);


    return emailSuccess
        ? ok(JsonMsgUtils.successfullEmail())
        : internalServerError(JsonMsgUtils.emailsNotSent());
  }

  private Result handlerNotFound() {
    return redirect("/handler/logout");
  }
}
