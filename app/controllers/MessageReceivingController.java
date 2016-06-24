package controllers;

import com.google.inject.Inject;
import java.util.*;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import services.OfferService;
import services.messaging.offer.OfferMessageService;
import services.messaging.offer.OfferSMSMessageService;
import models.Offer;
import play.Logger;
import models.OfferResponse;
import models.OfferResponse.ResponseStatus;
import models.Grower;

import com.avaje.ebean.Model.Finder;
import services.impl.EbeanGrowerService;

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
    String phoneNum;
    String smsMessage;

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


    /* if we reach here we have non-null smsMessage from phoneNum in format +11234567890 
     * NOTE: respond with SMS if there is an error in their response 
     */
    EbeanGrowerService ebean = new EbeanGrowerService(); 
    Grower grower = ebean.growerLookupByPhoneNum(phoneNum);
    if (grower == null) {
      /* send SMS message to grower letting them know they are not authorized */
      Logger.error("Message received from " + phoneNum + " does not correspond to a grower in our system.");
      return badRequest("Message received from " + phoneNum + " does not correspond to a grower in our system.");
    }

    /* if we reach here, we received a SMS message from a valid grower */

    Long offerID = -1L;
    Integer almondPounds = -1;
    boolean formatted = parseSMSMessage(smsMessage, offerID, almondPounds);
    if (!formatted) {
      /* send SMS message to grower letting them know their text was not formatted correctly */
      Logger.error("SMS message was not formatted correctly.");
      return badRequest("SMS message was not formatted correctly.");
    }

    /* if we reach here, the SMS message has valid offerID and almondAmount response */

    Offer offer = grower.offerLookupByID(offerID);
    if (offer == null) {
      /* send SMS message to grower letting them know their offerID was not correct */
      Logger.error("SMS message was not formatted correctly.");
      return badRequest("SMS message was not formatted correctly.");
    }

    /* now we have to update the offer response (and check if it is still a live offer) */

    Logger.info("From: " + phoneNum + " message: " + smsMessage);
    return ok("From: " + phoneNum + "message: " + smsMessage);
  }  

  boolean parseSMSMessage(String smsMessage, Long offerID, Integer almondPounds) {
    /* parse the smsMessage to pull out the offerID and almondAmount
     * return false if not formatted correctly
     */
    return false;
  }

}