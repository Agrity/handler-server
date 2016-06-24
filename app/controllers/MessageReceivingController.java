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
      boolean sent = sendResponse("Please respond with a non-empty SMS message.", phoneNum);
      Logger.error("Empty SMS message received from: " + phoneNum);
      return badRequest("Empty SMS message received from: " + phoneNum);
    }

    /* if we reach here we have non-null smsMessage from phoneNum in format +11234567890 */
     
    EbeanGrowerService ebean = new EbeanGrowerService(); 
    Grower grower = ebean.growerLookupByPhoneNum(phoneNum);
    if (grower == null) {
      boolean sent = sendResponse("This phone number is not authorized in Agrity's grower list.", phoneNum);
      Logger.error("Message received from " + phoneNum + " does not correspond to a grower in our system.");
      return badRequest("Message received from " + phoneNum + " does not correspond to a grower in our system.");
    }

    /* if we reach here, we received a SMS message from a valid grower */

    SMSParser parser = new SMSParser(smsMessage);
    if (!parser.isValid()) {
      sendResponse(parser.getErrorMessage(), phoneNum);
      Logger.error(parser.getErrorMessage());
      return badRequest(parser.getErrorMessage());
    }
 
    Long offerID = parser.getOfferID();
    Integer almondPounds = parser.getAlmoundPounds();
    
    /* if we reach here, the SMS message has a well-formatted offerID and almondAmount response */

    Offer offer = grower.offerLookupByID(offerID);
    if (offer == null) {
      boolean sent = sendResponse("OfferId: " + offerID + " does not exist.", phoneNum);
      Logger.error("OfferId: " + offerID + " does not exist. From: " + phoneNum);
      return badRequest("OfferId: " + offerID + " does not exist. From: " + phoneNum);
    }

    Logger.info("The valid offerID is: " + offerID + " the amount accepted is: " + almondPounds)
    updateOffer(grower, offer, almondPounds, phoneNum);
    return ok("Grower response ingested properly");
  } 

  /* === TODO: Grower request call? === */
  /* === TODO: More robust/detailed responses === */
  private void updateOffer(Grower grower, Offer offer, Integer almondPounds, String phoneNum) {
    if (almondPounds > 0) {
      boolean accepted = offer.growerAcceptOffer(grower.getId(), almondPounds);
      if (accepted) {
        boolean sent = sendResponse("Congratulations! You accepted the bid!", phoneNum);
        Logger.info("Offer: " + offer.getId() + " was accepted by: " + grower.getFullName()
                  + " for " + almondPounds + "lbs.");
      } else {
        boolean sent = sendResponse("Unfortunately we were not able to process your accepted bid."
                                  + " The bid has either expired or been accepted by another grower.", phoneNum);
        Logger.info("Offer: " + offer.getId() + " could not be accepted by: " + grower.getFullName()
                  + " for " + almondPounds + "lbs.");
      }
    } else {
      boolean rejected = offer.growerRejectOffer(grower.getId());
      if (rejected) {
        boolean sent = sendResponse("The bid has successfully been rejected.", phoneNum);
        Logger.info("Offer: " + offer.getId() + " was rejected by: " + grower.getFullName());
      } else {
        boolean sent = sendResponse("Error rejecting bid.", phoneNum);
        Logger.info("Offer: " + offer.getId() + " could not be rejected by: " + grower.getFullName());
      }
    }
  }

  private boolean sendResponse(String response, String phoneNumber) {
    List<NameValuePair> params = new ArrayList<NameValuePair>(); 
    params.add(new BasicNameValuePair("To", phoneNumber));
    params.add(new BasicNameValuePair("From", TwilioFields.getTwilioNumber()));
    params.add(new BasicNameValuePair("Body", response));
    try {
      Message message = TwilioFields.getMessageFactory().create(params);
    } catch (TwilioRestException e) {
       Logger.error("=== Error Sending SMS Response Message === to " + phoneNumber + " " + e.getErrorMessage() + "\n\n");
       return false;
     }
     return true;
  } 
}