package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import models.Grower;
import models.Handler;
import models.Offer;

import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Content;

import services.GrowerService;
import services.HandlerService;
import services.OfferService;
import services.messaging.offer.OfferMessageService;

public class OfferController extends Controller {

  private final OfferMessageService offerMessageService;

  @Inject
  public OfferController(OfferMessageService offerMessageService) {
    this.offerMessageService = offerMessageService;
  }

  public Result indexOffer(long id) {
    Content html =
        views.html.emailOfferBody.render(OfferService.getOffer(id), GrowerService.getGrower(1L));
    return ok(html);
  }

  public Result acceptOffer(long offerId, long growerId) {
    Offer offer = OfferService.getOffer(offerId);
    if (offer == null) {
      return notFound(ErrorMessages.offerNotFoundMessage(growerId));
    }

    boolean success = offer.growerAcceptOffer(growerId);

    return success ? ok("Successfully Accepted Offer.")
        : internalServerError("Internal Error: Offer could not be accepted.");
  }

  public Result rejectOffer(long offerId, long growerId) {
    Offer offer = OfferService.getOffer(offerId);
    if (offer == null) {
      return notFound(ErrorMessages.offerNotFoundMessage(growerId));
    }

    boolean success = offer.growerRejectOffer(growerId);

    return success ? ok("Successfully Rejected Offer.")
        : internalServerError("Internal Error: Offer could not be accepted.");
  }

  public Result requestCall(long offerId, long growerId) {
    Offer offer = OfferService.getOffer(offerId);
    if (offer == null) {
      return notFound(ErrorMessages.offerNotFoundMessage(growerId));
    }

    boolean success = offer.growerRequestCall(growerId);

    return success ? ok("Successfully Requested Call.")
        : internalServerError("Internal Error: Offer could not be accepted.");
  }


  public Result sendOffer(long id) {
    Offer offer = OfferService.getOffer(id);
    boolean emailSucces = offerMessageService.send(offer);
  return emailSucces ? redirect("/") : internalServerError("Some or all of the emails were unable to be sent");
  }

  // Annotation ensures that POST request is of type application/json. If not HTTP 400 response
  // returned.
  @BodyParser.Of(BodyParser.Json.class)
  public Result createOffer() {
    JsonNode data = request().body().asJson();

    if (data == null) {
      return badRequest("Expecting Some Data.\n");
    }

    Logger.info("Offer Data Recieved: " + data.toString());
    return OfferService.createOfferResult(data);
  }

  public Result getAllOffers() {
    return ok(OfferService.getAllOffers().toString());
  }

  public Result getOffer(long id) {
    Offer offer = OfferService.getOffer(id);

    if (offer == null) {
      return notFound(ErrorMessages.offerNotFoundMessage(id));
    }

    return ok(offer.toPrettyString());
  }

  public Result getAllHandlerOffers(long handlerId) {
    Handler handler = HandlerService.getHandler(handlerId);

    if (handler == null) {
      return notFound(ErrorMessages.handlerNotFoundMessage(handlerId));
    }

    return ok(Helpers.fetchList(handler.getOfferList()).toString());
  }

  public Result getAllGrowerOffers(long growerId) {
    Grower grower = GrowerService.getGrower(growerId);

    if (grower == null) {
      return notFound(ErrorMessages.offerNotFoundMessage(growerId));
    }

    // TODO Implement
    return ok("TODO");
  }
}
