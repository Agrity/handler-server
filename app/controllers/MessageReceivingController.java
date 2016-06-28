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
 
    Long offerID = parser.getOfferID();
    Integer almondPounds = parser.getAlmoundPounds();
    
    /* if we reach here, the SMS message has a well-formatted offerID and almondAmount response */

    Offer offer = grower.offerLookupByID(offerID);
    if (offer == null) {
      Logger.error("OfferId: " + offerID + " does not exist. From: " + phoneNum);
      return ok("OfferId: " + offerID + " does not exist.");
    }

    Logger.info("The valid offerID is: " + offerID + " the amount accepted is: " + almondPounds);
    return updateOffer(grower, offer, almondPounds);
  } 

  /* === TODO: Grower request call? === */
  private Result updateOffer(Grower grower, Offer offer, Integer almondPounds) {
    if (almondPounds > 0) {
      OfferResponseResult result = offer.growerAcceptOffer(grower.getId(), almondPounds);

      if (result.isValid()) {
        Logger.info("Offer: " + offer.getId() + " was accepted by: " + grower.getFullName()
                  + " for " + almondPounds + "lbs.");
        return ok("Congratulations! You accepted the bid!");

      } else {
        Logger.info("Offer: " + offer.getId() + " could not be accepted by: " + grower.getFullName()
                  + " for " + almondPounds + "lbs. " + result.getInvalidResponseMessage());
        return ok(result.getInvalidResponseMessage());
      }

    } else {
      OfferResponseResult result = offer.growerRejectOffer(grower.getId());
      if (result.isValid()) {
        Logger.info("Offer: " + offer.getId() + " was rejected by: " + grower.getFullName());
        return ok("Bid #" + offer.getId() + " has successfully been rejected.");

      } else {
        Logger.info("Offer: " + offer.getId() + " could not be rejected by: " 
                  + grower.getFullName() + result.getInvalidResponseMessage());
        return ok(result.getInvalidResponseMessage());
      }
    }
  }
}