package controllers;

import com.google.inject.Inject;
import java.util.*;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import services.messaging.offer.OfferSMSMessageService;
import models.Offer;
import play.Logger;
import models.OfferResponse.ResponseStatus;
import models.Grower;
import models.OfferResponseResult;

import com.avaje.ebean.Model.Finder;
import services.impl.EbeanGrowerService;

import services.messaging.MessageServiceConstants.TwilioFields;
import services.parsers.SMSParser;

public class MessageReceivingController extends Controller {

  private static Integer numResponses = 0;

  private final OfferSMSMessageService messageService;

  @Inject
  public MessageReceivingController(OfferSMSMessageService messageService) {
    this.messageService = messageService;
  }

  public Result numberTwilioResponses() {
    return ok("Number Responses Recieved: " + numResponses);
  }

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
 
    Long offerID = parser.getID();
    boolean accepted = parser.getAccepted();
    Integer almondPounds = parser.getPounds();
    
    /* if we reach here, the SMS message has a well-formatted offerID and almondAmount response */

    OfferService offerService = new EbeanOfferService();
    Offer offer = offerService.getByID(offerID);
    if (offer == null) {
      Logger.error("OfferID " + offerID + " does not exist. From: " + phoneNum);
      return ok("Bid: " + offerID + " does not exist.");
    }
    if (grower.offerLookupById(offerId) == null) {
      Logger.error("OfferID " + offerID + " not owned by grower " + grower.getId() +". From: " + phoneNum);
      return ok("You are not authorized to accept bid " + offerID + ".");
    }

    Logger.info("The valid offerID is: " + offerID);
    return updateOffer(grower, offer, accepted, almondPounds);
  } 

  /* === TODO: Grower request call? === */
  private Result updateOffer(Grower grower, Offer offer, boolean accepted, Integer almondPounds) {
    if (accepted) {
      OfferResponseResult result = offer.growerAcceptOffer(grower.getId(), almondPounds);

      if (result.isValid()) {
        Logger.info("Bid: " + offer.getId() + " was accepted by: " + grower.getFullName());
        return ok("Congratulations! Your bid (ID " + offer.getId() + ") <" + offer.getAlmondVariety() + " for " 
        + offer.getPricePerPound() + "/lb.> has been accepted for " + almondPounds + "lbs.");

      } else {
        Logger.info("Bid: " + offer.getId() + " could not be accepted by: " + grower.getFullName()
                  + " for " + result.getInvalidResponseMessage());
        return ok(result.getInvalidResponseMessage());
      }

    } else {

      OfferResponseResult result = offer.growerRejectOffer(grower.getId());
      if (result.isValid()) {
        Logger.info("Bid: " + offer.getId() + " was rejected by: " + grower.getFullName());
        return ok("Bid #" + offer.getId() + " has successfully been rejected.");

      } else {
        Logger.info("Bid: " + offer.getId() + " could not be rejected by: " 
                  + grower.getFullName() + result.getInvalidResponseMessage());
        return ok(result.getInvalidResponseMessage());
      }
    }
  }
}