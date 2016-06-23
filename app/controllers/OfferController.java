package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import java.time.Duration;
import java.util.List;
import java.lang.reflect.Constructor;

import models.Offer;

import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import services.OfferService;
import services.messaging.offer.OfferMessageService;
import services.offer_management.FCFSService;
import services.parsers.OfferJsonParser;
import services.parsers.OfferJsonParser.ManagementTypeInfo;
import services.offer_management.WaterfallService;
import services.offer_management.FCFSService;

public class OfferController extends Controller {

  private final OfferService offerService;
  private final OfferMessageService offerMessageService;

  private final ObjectMapper jsonMapper;

  @Inject
  public OfferController(OfferService offerService, OfferMessageService offerMessageService) {
    this.offerService = offerService;
    this.offerMessageService = offerMessageService;

    this.jsonMapper = new ObjectMapper();
  }

  public Result indexOffer(long id) {
    //Content html =
    //    views.html.emailOfferBody.render(OfferService.getOffer(id), GrowerService.getGrower(1L));
    //return ok(html);
    return null;
  }

  public Result acceptOffer(long offerId, long growerId) {
    Offer offer = offerService.getById(offerId);
    if (offer == null) {
      return notFound(JsonMsgUtils.offerNotFoundMessage(offerId));
    }

    boolean success = offer.growerAcceptOffer(growerId);

    return success ? ok(JsonMsgUtils.successfullAccept())
        : internalServerError(JsonMsgUtils.offerNotAccepted());
  }

  public Result rejectOffer(long offerId, long growerId) {
    Offer offer = offerService.getById(offerId);
    if (offer == null) {
      return notFound(JsonMsgUtils.offerNotFoundMessage(offerId));
    }

    boolean success = offer.growerRejectOffer(growerId);

    return success ? ok(JsonMsgUtils.successfullReject())
        : internalServerError(JsonMsgUtils.offerNotRejected());
  }

  public Result requestCall(long offerId, long growerId) {
    Offer offer = offerService.getById(offerId);
    if (offer == null) {
      return notFound(JsonMsgUtils.offerNotFoundMessage(growerId));
    }

    boolean success = offer.growerRequestCall(growerId);

    return success ? ok(JsonMsgUtils.successfullCallRequest())
        : internalServerError(JsonMsgUtils.callNotRequested());
  }


  public Result sendOffer(long id) {
    Offer offer = offerService.getById(id);
    boolean emailSuccess = offerMessageService.send(offer);

    return emailSuccess
        ? ok(JsonMsgUtils.successfullEmail())
        : internalServerError(JsonMsgUtils.emailsNotSent());
  }

  // Annotation ensures that POST request is of type application/json. If not HTTP 400 response
  // returned.
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

  public Result getAllOffers() {
    try {
      return ok(jsonMapper.writeValueAsString(offerService.getAll()));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

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
