package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import models.Grower;
import models.Handler;
import models.Offer;

import play.api.libs.mailer.MailerClient;
import play.libs.mailer.Email;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import services.GrowerService;
import services.HandlerService;
import services.OfferService;

public class OfferController extends Controller {

  private final MailerClient mailer;

  @Inject
  public OfferController(MailerClient mailerClient) {
    this.mailer = mailerClient;
  }

  public Result send() {

    //final Email email = new Email()
    //  .setSubject("AUTOMATED FUCKING EMAILS")
    //  .setFrom("Agrity <agritycommodities@gmail.com>")
    //  .addTo("<larsenj@stanford.edu>")
    //  .setBodyText("Start Up Done");

    //final Email email2 = new Email()
    //  .setSubject("AUTOMATED FUCKING EMAILS")
    //  .setFrom("Agrity <agritycommodities@gmail.com>")
    //  .addTo("<jackmcc@stanford.edu>")
    //  .setBodyText("Start Up Done");

    //try {
    //  String id = mailer.send(email);
    //  String id2 = mailer.send(email2);
    //  return ok("Email " + id + " " + id2 + " sent!");
    //} catch (Exception e) {
    //  Logger.error("=== Error Sending Email ===\n" + e.getMessage());
    //  return internalServerError("Error sending email");
    //}

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
