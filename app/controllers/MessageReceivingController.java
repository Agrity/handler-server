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
    String phoneNum = "";
    String smsMessage = "";
    try {
      phoneNum = bodyMap.get("From")[0]; 
    } catch (NullPointerException e) {
      Logger.error("Error receiving SMS message.");
      return badRequest("Error receiving SMS message.");
    }
    try {
      smsMessage = bodyMap.get("Body")[0]; 
    } catch (NullPointerException e) {
      /* send SMS back that there was an error */
      Logger.error("Empty SMS message received from: " + phoneNum);
      return badRequest("Empty SMS message received from: " + phoneNum);
    }
    
    /* non-null smsMessage from phoneNum in format +11234567890 */

    Logger.info("From: " + phoneNum + " message: " + smsMessage);
    return ok("From: " + phoneNum + "message: " + smsMessage);
  }

    
}
