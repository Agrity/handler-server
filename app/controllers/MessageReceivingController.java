package controllers;

import com.google.inject.Inject;
import java.util.*;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import services.OfferService;
import services.messaging.offer.OfferMessageService;
import models.Offer;
import play.Logger;

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


  /* === TODO: Error Checking === */
  /* === TODO: Phone number and body strings formatting === */
  /* === TODO: Grower look-up based on phone number === */
  /* === TODO: Update offer based on grower's response === */
  /* === TODO: Reply to grower? === */
  /* === TODO: How is offer updated if grower doesn't go through Twilio to accept from handler? === */
  public Result receiveTwilioResponse() {
    numResponses++;
    Map<String, String[]> bodyMap = request().body().asFormUrlEncoded();

    String phoneNum = Arrays.toString(bodyMap.get("From"));
    String smsMessage = Arrays.toString(bodyMap.get("Body"));
    String phoneNum2 = bodyMap.get("Body")[0];
    String smsMessage2 = bodyMap.get("From")[0];
    if (smsMessage == null) {
      //send error message back to sender
      return badRequest("No body in SMS message.");
    }

    Logger.info("From: " + phoneNum + " message: " + smsMessage);
    Logger.info("From2: " + phoneNum2 + " message2 " + smsMessage2);
    return ok("From: " + phoneNum + "message: " + smsMessage);
  }

    
}
