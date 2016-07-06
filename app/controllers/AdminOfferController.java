package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import java.util.List;

import controllers.security.AdminSecured;

import models.Offer;

import models.OfferResponseResult;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import services.OfferService;
import services.messaging.offer.OfferMessageService;
import services.offer_management.FCFSService;
import services.parsers.OfferJsonParser;
import services.parsers.OfferJsonParser.ManagementTypeInfo;

import utils.JsonMsgUtils;

import services.offer_management.WaterfallService;

public class AdminOfferController extends Controller {

  private final OfferService offerService;
  private final OfferMessageService offerMessageService;

  private final ObjectMapper jsonMapper;

  @Inject
  public AdminOfferController(OfferService offerService, OfferMessageService offerMessageService) {
    this.offerService = offerService;
    this.offerMessageService = offerMessageService;

    this.jsonMapper = new ObjectMapper();
  }

  // TODO Not Secured. Implement non-admin means of responding to offer.
  public Result acceptOffer(long offerId, long growerId) {
    Offer offer = offerService.getById(offerId);
    if (offer == null) {
      return notFound(JsonMsgUtils.offerNotFoundMessage(offerId));
    }

    // TODO Change to actual pounds accepted once implemented.
    OfferResponseResult success = offer.growerAcceptOffer(growerId, offer.getAlmondPounds());
    
    return success.isValid() ? ok(JsonMsgUtils.successfullAccept())
        : internalServerError(JsonMsgUtils.offerNotAccepted(success.getInvalidResponseMessage()));
  }

  // TODO Not Secured. Implement non-admin means of responding to offer.
  public Result rejectOffer(long offerId, long growerId) {
    Offer offer = offerService.getById(offerId);
    if (offer == null) {
      return notFound(JsonMsgUtils.offerNotFoundMessage(offerId));
    }

    OfferResponseResult success = offer.growerRejectOffer(growerId);

    return success.isValid() ? ok(JsonMsgUtils.successfullReject())
        : internalServerError(JsonMsgUtils.offerNotRejected(success.getInvalidResponseMessage()));
  }

  // TODO Not Secured. Implement non-admin means of responding to offer.
  public Result requestCall(long offerId, long growerId) {
    Offer offer = offerService.getById(offerId);
    if (offer == null) {
      return notFound(JsonMsgUtils.offerNotFoundMessage(growerId));
    }

    OfferResponseResult success = offer.growerRequestCall(growerId);

    return success.isValid() ? ok(JsonMsgUtils.successfullCallRequest())
        : internalServerError(JsonMsgUtils.callNotRequested(success.getInvalidResponseMessage()));
  }


  @Security.Authenticated(AdminSecured.class)
  public Result sendOffer(long id) {
    Offer offer = offerService.getById(id);
    boolean emailSuccess = offerMessageService.send(offer);

    return emailSuccess
        ? ok(JsonMsgUtils.successfullEmail())
        : internalServerError(JsonMsgUtils.emailsNotSent());
  }

  // Annotation ensures that POST request is of type application/json. If not HTTP 400 response
  // returned.
  @Security.Authenticated(AdminSecured.class)
  @BodyParser.Of(BodyParser.Json.class)
  public Result createOffer() {
    JsonNode data = request().body().asJson();

    if (data == null) {
      return badRequest(JsonMsgUtils.expectingData());
    }

    OfferJsonParser parser = new OfferJsonParser(data);

    if (!parser.isValid()) {
      return badRequest(JsonMsgUtils.caughtException(parser.getErrorMessage()));
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

  @Security.Authenticated(AdminSecured.class)
  public Result getAllOffers() {
    try {
      return ok(jsonMapper.writeValueAsString(offerService.getAll()));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    } 
  }

  @Security.Authenticated(AdminSecured.class)
  public Result getOffer(long id) {
    Offer offer = offerService.getById(id);

    if (offer == null) {
      return notFound(JsonMsgUtils.offerNotFoundMessage(id));
    }

    try {
      return ok(jsonMapper.writeValueAsString(offer));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  @Security.Authenticated(AdminSecured.class)
  public Result deleteOffer(long id) {
    Offer offer = offerService.getById(id);

    if (offer == null) {
      return notFound(JsonMsgUtils.offerNotFoundMessage(id));
    }

    offer.delete();
    return ok(JsonMsgUtils.offerDeleted(id));
  }  

  @Security.Authenticated(AdminSecured.class)
  public Result getAllHandlerOffers(long handlerId) {
    List<Offer> handlerOffers = offerService.getByHandler(handlerId);

    // handlerOffers will be null if Hanlder with handlerId cannot be found.
    if (handlerOffers == null) {
      return notFound(JsonMsgUtils.handlerNotFoundMessage(handlerId));
    }
    
    try {
      return ok(jsonMapper.writeValueAsString(handlerOffers));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  @Security.Authenticated(AdminSecured.class)
  public Result getAllGrowerOffers(long growerId) {
    List<Offer> growerOffers = offerService.getByGrower(growerId);

    // growerOffers will be null if Grower with growerId cannot be found.
    if (growerOffers == null) {
      return notFound(JsonMsgUtils.growerNotFoundMessage(growerId));
    }

    try {
      return ok(jsonMapper.writeValueAsString(growerOffers));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }
}
