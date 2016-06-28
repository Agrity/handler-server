package controllers;

import com.google.inject.Inject;
import java.util.*;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import services.messaging.offer.OfferSMSMessageService;
import models.Offer;
import play.Logger;
import models.OfferResponse;
import models.OfferResponse.ResponseStatus;
import models.Grower;
import models.OfferResponseResult;

import com.avaje.ebean.Model.Finder;
import services.impl.EbeanGrowerService;

import services.messaging.MessageServiceConstants.TwilioFields;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.twilio.sdk.*;
import com.twilio.sdk.resource.factory.*;
import com.twilio.sdk.resource.instance.*;
import com.twilio.sdk.resource.list.*;
import com.twilio.sdk.TwilioRestResponse;
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
      //boolean sent = sendResponse("Please respond with a non-empty SMS message.", phoneNum);
      Logger.error("Empty SMS message received from: " + phoneNum);
      //return badRequest("Empty SMS message received from: " + phoneNum);
      return ok("Please respond with a non-empty SMS message.");
    }

    /* if we reach here we have non-null smsMessage from phoneNum in format +11234567890 */
     
    EbeanGrowerService ebean = new EbeanGrowerService(); 
    Grower grower = ebean.growerLookupByPhoneNum(phoneNum);
    if (grower == null) {
      //boolean sent = sendResponse("This phone number is not authorized in Agrity's grower list.", phoneNum);
      Logger.error("Message received from " + phoneNum + " does not correspond to a grower in our system.");
      //return badRequest("Message received from " + phoneNum + " does not correspond to a grower in our system.");
      return ok("This phone number is not authorized in Agrity's grower list.");
    }

    /* if we reach here, we received a SMS message from a valid grower */

    SMSParser parser = new SMSParser(smsMessage);
    if (!parser.isValid()) {
      //sendResponse(parser.getErrorMessage(), phoneNum);
      Logger.error(parser.getErrorMessage());
      //return badRequest(parser.getErrorMessage());
      return ok(parser.getErrorMessage());
    }
 
    Long offerID = parser.getOfferID();
    Integer almondPounds = parser.getAlmoundPounds();
    
    /* if we reach here, the SMS message has a well-formatted offerID and almondAmount response */

    Offer offer = grower.offerLookupByID(offerID);
    if (offer == null) {
      //boolean sent = sendResponse("OfferId: " + offerID + " does not exist.", phoneNum);
      Logger.error("OfferId: " + offerID + " does not exist. From: " + phoneNum);
      //return badRequest("OfferId: " + offerID + " does not exist. From: " + phoneNum);
      return ok("OfferId: " + offerID + " does not exist.");
    }

    Logger.info("The valid offerID is: " + offerID + " the amount accepted is: " + almondPounds);
    return updateOffer(grower, offer, almondPounds);
  } 

  /* === TODO: Grower request call? === */
  /* === TODO: More robust/detailed responses === */
  private Result updateOffer(Grower grower, Offer offer, Integer almondPounds) {
    if (almondPounds > 0) {
      OfferResponseResult result = offer.growerAcceptOffer(grower.getId(), almondPounds);
      if (result.isValid()) {
        //boolean sent = messageService.sendUpdated(offer, grower, "Congratulations! You accepted the bid!");
        Logger.info("Offer: " + offer.getId() + " was accepted by: " + grower.getFullName()
                  + " for " + almondPounds + "lbs.");
        return ok("Congratulations! You accepted the bid!");
      } else {
        //boolean sent = messageService.sendUpdated(offer, grower, result.getInvalidResponseMessage());
        Logger.info("Offer: " + offer.getId() + " could not be accepted by: " + grower.getFullName()
                  + " for " + almondPounds + "lbs. " + result.getInvalidResponseMessage());
        return ok(result.getInvalidResponseMessage());
      }
    } else {
      OfferResponseResult result = offer.growerRejectOffer(grower.getId());
      if (result.isValid()) {
        //boolean sent = messageService.sendUpdated(offer, grower, 
        //                          "Bid #" + offer.getId() + " has successfully been rejected.");
        Logger.info("Offer: " + offer.getId() + " was rejected by: " + grower.getFullName());
        return ok("Bid #" + offer.getId() + " has successfully been rejected.");
      } else {
        //boolean sent = messageService.sendUpdated(offer, grower, result.getInvalidResponseMessage());
        Logger.info("Offer: " + offer.getId() + " could not be rejected by: " 
                  + grower.getFullName() + result.getInvalidResponseMessage());
        return ok(result.getInvalidResponseMessage());
      }
    }
  }

  /* === TODO: Get rid of this helper function because we do same process in OfferSMSMessageService
   * However, we need this for now as we need to send error messages without having valid grower/offer === */
  public boolean sendResponse(String msg, String phoneNum) {
    List<NameValuePair> params = new ArrayList<NameValuePair>(); 
    params.add(new BasicNameValuePair("To", phoneNum));    
    params.add(new BasicNameValuePair("From", TwilioFields.getTwilioNumber())); 
    params.add(new BasicNameValuePair("Body", msg));
    try {
      Message message = TwilioFields.getMessageFactory().create(params);
    } catch (TwilioRestException e) {
      Logger.error("=== Error Sending SMS Message === to " + phoneNum + " " + e.getErrorMessage() + "\n\n");
      return false;
    }
    return true;
  }
}