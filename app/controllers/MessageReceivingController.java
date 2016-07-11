package controllers;

import com.google.inject.Inject;
import java.util.*;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import services.messaging.bid.BidSMSMessageService;
import models.HandlerBid;
import play.Logger;
import models.BidResponse.ResponseStatus;
import models.Grower;
import models.BidResponseResult;

import com.avaje.ebean.Model.Finder;
import services.impl.EbeanGrowerService;

import services.messaging.MessageServiceConstants.TwilioFields;
import services.parsers.SMSParser;

public class MessageReceivingController extends Controller {

  private static Integer numResponses = 0;

  private final BidSMSMessageService messageService;

  @Inject
  public MessageReceivingController(BidSMSMessageService messageService) {
    this.messageService = messageService;
  }

  public Result numberTwilioResponses() {
    return ok("Number Responses Recieved: " + numResponses);
  }

  /* === TODO: How is bid updated if grower doesn't go through Twilio to accept from handler? === */

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
      Logger.error("Empty SMS message received from: " + phoneNum);
      return ok("Please respond with a non-empty SMS message.");
    }

    /* if we reach here we have non-null smsMessage from phoneNum in format +11234567890 */
     
    EbeanGrowerService ebean = new EbeanGrowerService(); 
    Grower grower = ebean.growerLookupByPhoneNum(phoneNum);
    if (grower == null) {
      Logger.error("Message received from " + phoneNum + " does not correspond to a grower in our system.");
      return ok("This phone number is not authorized in Agrity's grower list.");
    }

    /* if we reach here, we received a SMS message from a valid grower */

    SMSParser parser = new SMSParser(smsMessage);
    if (!parser.isValid()) {
      Logger.error(parser.getErrorMessage());
      return ok(parser.getErrorMessage());
    }
 
    Long bidID = parser.getID();
    boolean accepted = parser.getAccepted();
    Integer almondPounds = parser.getPounds();
    
    /* if we reach here, the SMS message has a well-formatted bidID and almondAmount response */

    HandlerBid handlerBid = grower.bidLookupByID(bidID);
    if (handlerBid == null) {
      Logger.error("BidID " + bidID + " does not exist. From: " + phoneNum);
      return ok("Bid: " + bidID + " does not exist.");
    }

    Logger.info("The valid bidID is: " + bidID);
    return updateBid(grower, handlerBid, accepted, almondPounds);
  } 

  /* === TODO: Grower request call? === */
  private Result updateBid(Grower grower, HandlerBid handlerBid, boolean accepted, Integer almondPounds) {
    if (accepted) {
      BidResponseResult result = handlerBid.growerAcceptBid(grower.getId(), almondPounds);

      if (result.isValid()) {
        Logger.info("Bid: " + handlerBid.getId() + " was accepted by: " + grower.getFullName());
        return ok("Congratulations! Your bid (ID " + handlerBid.getId() + ") <" + handlerBid.getAlmondVariety() + " for " 
        + handlerBid.getPricePerPound() + "/lb.> has been accepted for " + almondPounds + "lbs.");

      } else {
        Logger.info("Bid: " + handlerBid.getId() + " could not be accepted by: " + grower.getFullName()
                  + " for " + result.getInvalidResponseMessage());
        return ok(result.getInvalidResponseMessage());
      }

    } else {

      BidResponseResult result = handlerBid.growerRejectBid(grower.getId());
      if (result.isValid()) {
        Logger.info("Bid: " + handlerBid.getId() + " was rejected by: " + grower.getFullName());
        return ok("Bid #" + handlerBid.getId() + " has successfully been rejected.");

      } else {
        Logger.info("Bid: " + handlerBid.getId() + " could not be rejected by: " 
                  + grower.getFullName() + result.getInvalidResponseMessage());
        return ok(result.getInvalidResponseMessage());
      }
    }
  }
}