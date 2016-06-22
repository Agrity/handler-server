package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import java.util.List;

import models.Offer;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import services.OfferService;
import services.messaging.offer.OfferMessageService;
import services.parsers.OfferJsonParser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public class MessageReceivingController extends Controller {

  private static Integer numResponses = 0;

  private final OfferService offerService;
  private final OfferMessageService offerMessageService;

  @Inject
  public MessageReceivingController(OfferService offerService, OfferMessageService offerMessageService) {
    this.offerService = offerService;
    this.offerMessageService = offerMessageService;
  }

  public Result numberTwilioResponses() {
    return ok("Number Responses Recieved: " + numResponses);
  }

  public Result receiveTwilioResponse() {
    numResponses++;
    JsonNode data = request().body().asJson();

    if (data == null) {
      // TODO Change to Valid Error JSON
      return ok("Expecting Some Data.\n"); /* bad request? */
    }

    String num = data.findValue("From").textValue();
    if (num == null) {
      return ok("From parameter is empty."); /* bad request? */
    }
    String text = data.findValue("Body").textValue();
    if (text == null) {
      return ok("Body parameter is empty."); /* bad request? */
    }
    return ok(num + "\n" + text);
  }

    
}
