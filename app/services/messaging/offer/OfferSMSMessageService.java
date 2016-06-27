package services.messaging.offer;

import java.util.*;
import com.twilio.sdk.*;
import com.twilio.sdk.resource.factory.*;
import com.twilio.sdk.resource.instance.*;
import com.twilio.sdk.resource.list.*;
import com.twilio.sdk.TwilioRestResponse;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import models.Grower;
import models.Offer;
import play.Logger;
import services.messaging.MessageServiceConstants.TwilioFields;


public class OfferSMSMessageService implements OfferMessageService {
  
  /* Takes an offer object and sends out SMS message containing bid to all growers using Twilio account */
  public boolean send(Offer offer) {
    boolean success = true;

    for (Grower grower : offer.getAllGrowers()) { 
      if(!send(offer, grower)) success = false;
    }

    return success;
  }
    
  public boolean send(Offer offer, Grower grower) {
    return sendUpdated(offer, grower, createBodyText(grower, offer));
  }

  public boolean sendClosed(Offer offer) {
    boolean success = true;

    for (Grower grower : offer.getAllGrowers()) {
      if(!sendClosed(offer, grower)) success = false;
    }

    return success; 
  }

  public boolean sendClosed(Offer offer, Grower grower) {
    String msg = "Your offer <" + offer.getAlmondVariety() + " for " 
        + offer.getPricePerPound() + "/lb.> has expired.";
    return sendUpdated(offer, grower, msg);
  }

  public boolean sendUpdated(Offer offer, String msg) {
    boolean success = true;

    for (Grower grower : offer.getAllGrowers()) {
      if(!sendUpdated(offer, grower, msg)) success = false;
    }

    return success; 
  }

  public boolean sendUpdated(Offer offer, Grower grower, String msg) {
    boolean success = true;
    for (String phoneNumber: grower.getPhoneNumbers()) {
      List<NameValuePair> params = new ArrayList<NameValuePair>(); 
      params.add(new BasicNameValuePair("To", phoneNumber));    
      params.add(new BasicNameValuePair("From", TwilioFields.getTwilioNumber())); 
      params.add(new BasicNameValuePair("Body", msg));
      try {
        Message message = TwilioFields.getMessageFactory().create(params);
      } catch (TwilioRestException e) {
        success = false;
        Logger.error("=== Error Sending SMS Message === to " + phoneNumber + " " + e.getErrorMessage() + "\n\n");
      }
    }
    return success;
  }

  private String createBodyText(Grower curGrower, Offer offer) {
    String body = "Hi " + curGrower.getFullName() + ",\n"
                + "Here are the specs for a new bid: \n"
                + offer.getAlmondVariety() + "\n"
                /* === TODO: Almond Size === */
                + offer.getAlmondPoundsString() + "lbs\n"
                + offer.getPricePerPound() + "/lb\n" 
                + offer.getComment() + "\n"
                + "Offer Id: " + offer.getId() + "/n"
                + "To respond to this bid, respond with the offer Id followed by the number of pounds "
                + "that you would like to accept (0 for rejection).\n" 
                + "-" + /*add handler's contact's name in addition?*/  offer.getHandler().getCompanyName(); 
    return body;
  } 

}