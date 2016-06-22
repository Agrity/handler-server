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
  
    Map<String, String[]> map = request().headers();
    for (Map.Entry<String, String[]> entry : map.entrySet()) {
      Logger.info("Header: " + entry.getKey() + " Value: " + Arrays.toString(entry.getValue()));
    }
    Map<String, String[]> bodyMap = request().body().asFormUrlEncoded();
    for (Map.Entry<String, String[]> entry : bodyMap.entrySet()) {
      Logger.info("Body Key: " + entry.getKey() + "     Body Value: " + Arrays.toString(entry.getValue()));
    }

    String phoneNum = Arrays.toString(bodyMap.get("From"));
    String smsMessage = Arrays.toString(bodyMap.get("Body"));
    Logger.info("From: " + phoneNum + " message: " + smsMessage);
    return ok("From: " + phoneNum + "\nmessage: " + smsMessage);
  }

    
}
