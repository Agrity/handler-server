package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import java.time.Duration;
import java.util.List;

import models.Offer;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import services.OfferService;
import services.messaging.offer.OfferMessageService;
import services.offer_management.FCFSService;
import services.parsers.OfferJsonParser;

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
      // TODO Change to Valid Error JSON
      return notFound(ErrorMessages.offerNotFoundMessage(offerId));
    }

    boolean success = offer.growerAcceptOffer(growerId);

    // TODO Change to Ok/Error to valid JSON
    return success ? ok("Successfully Accepted Offer.")
        : internalServerError("Internal Error: Offer could not be accepted.");
  }

  public Result rejectOffer(long offerId, long growerId) {
    Offer offer = offerService.getById(offerId);
    if (offer == null) {
      // TODO Change to Valid Error JSON
      return notFound(ErrorMessages.offerNotFoundMessage(offerId));
    }

    boolean success = offer.growerRejectOffer(growerId);

    // TODO Change to Ok/Error to valid JSON
    return success ? ok("Successfully Rejected Offer.")
        : internalServerError("Internal Error: Offer could not be accepted.");
  }

  public Result requestCall(long offerId, long growerId) {
    Offer offer = offerService.getById(offerId);
    if (offer == null) {
      // TODO Change to Valid Error JSON
      return notFound(ErrorMessages.offerNotFoundMessage(growerId));
    }

    boolean success = offer.growerRequestCall(growerId);

    // TODO Change to Ok/Error to valid JSON
    return success ? ok("Successfully Requested Call.")
        : internalServerError("Internal Error: Offer could not be accepted.");
  }


  public Result sendOffer(long id) {
    Offer offer = offerService.getById(id);
    boolean emailSucces = offerMessageService.send(offer);

    // TODO Change to Ok/Error to valid JSON
    return emailSucces
        ? ok("Emails Sent Successfully!")
        : internalServerError("Some or all of the emails were unable to be sent");
  }

  // Annotation ensures that POST request is of type application/json. If not HTTP 400 response
  // returned.
  @BodyParser.Of(BodyParser.Json.class)
  public Result createOffer() {
    JsonNode data = request().body().asJson();

    if (data == null) {
      // TODO Change to Valid Error JSON
      return badRequest("Expecting Some Data.\n");
    }

    OfferJsonParser parser = new OfferJsonParser(data);

    if (!parser.isValid()) {
      // TODO Change to Valid Error JSON
      return badRequest(parser.getErrorMessage());
    }

    Offer offer = parser.formOffer();
    offer.save();

    // TODO Parse Which Type Would Be Desired.
    new FCFSService(offer, Duration.ofHours(1));

    try {
      return created(jsonMapper.writeValueAsString(offer));
    } catch (JsonProcessingException e) {
      // TODO Change to Valid Error JSON
      return internalServerError(e.toString());
    }
  }

  public Result getAllOffers() {
    try {
      return ok(jsonMapper.writeValueAsString(offerService.getAll()));
    } catch (JsonProcessingException e) {
      // TODO Change to Valid Error JSON
      return internalServerError(e.toString());
    }
  }

  public Result getOffer(long id) {
    Offer offer = offerService.getById(id);

    if (offer == null) {
      // TODO Change to Valid Error JSON
      return notFound(ErrorMessages.offerNotFoundMessage(id));
    }

    try {
      return ok(jsonMapper.writeValueAsString(offer));
    } catch (JsonProcessingException e) {
      // TODO Change to Valid Error JSON
      return internalServerError(e.toString());
    }
  }

  public Result getAllHandlerOffers(long handlerId) {
    List<Offer> handlerOffers = offerService.getByHandler(handlerId);

    // handlerOffers will be null if Hanlder with handlerId cannot be found.
    if (handlerOffers == null) {
      // TODO Change to Valid Error JSON
      return notFound(ErrorMessages.handlerNotFoundMessage(handlerId));
    }
    
    try {
      return ok(jsonMapper.writeValueAsString(handlerOffers));
    } catch (JsonProcessingException e) {
      // TODO Change to Valid Error JSON
      return internalServerError(e.toString());
    }
  }

  public Result getAllGrowerOffers(long growerId) {
    List<Offer> growerOffers = offerService.getByGrower(growerId);

    // growerOffers will be null if Grower with growerId cannot be found.
    if (growerOffers == null) {
      // TODO Change to Valid Error JSON
      return notFound(ErrorMessages.growerNotFoundMessage(growerId));
    }

    try {
      return ok(jsonMapper.writeValueAsString(growerOffers));
    } catch (JsonProcessingException e) {
      // TODO Change to Valid Error JSON
      return internalServerError(e.toString());
    }

  }
}
