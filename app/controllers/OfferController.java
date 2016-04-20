package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import models.Grower;
import models.Handler;
import models.Offer;

import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import services.GrowerService;
import services.HandlerService;
import services.OfferService;

public class OfferController extends Controller {

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
      return notFound(ErrorMessages.offerNotFoundMessage(handlerId));
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
