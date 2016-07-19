package services.messaging.bid;

import java.util.ArrayList;
import java.util.List;

import models.PhoneNumber;
import models.Grower;
import models.HandlerBid;
import play.Logger;
import services.messaging.MessageServiceConstants.TwilioFields;
import services.bid_management.BidManagementService;


public class HandlerBidSMSMessageService implements HandlerBidMessageService {

  private static final TwilioMessageService twilioMessageService = new TwilioMessageService();
  
  /* Takes an offer object and sends out SMS message containing bid to all growers using Twilio account */
  public boolean send(HandlerBid handlerBid) {
    boolean success = true;

    for (Grower grower : handlerBid.getAllGrowers()) { 
      if(!send(handlerBid, grower)) success = false;
    }

    return success;
  }
    
  public boolean send(HandlerBid handlerBid, Grower grower) {
    return sendUpdated(handlerBid, grower, createBodyText(grower, handlerBid));
  }

  public boolean sendClosed(HandlerBid handlerBid) {
    boolean success = true;

    for (Grower grower : handlerBid.getAllGrowers()) {
      if(!sendClosed(handlerBid, grower)) success = false;
    }

    return success; 
  }

  public boolean sendClosed(HandlerBid handlerBid, Grower grower) {
    String msg = "Your bid (ID " + handlerBid.getId() + ") <" + handlerBid.getAlmondVariety() + " for " 
        + handlerBid.getPricePerPound() + "/lb.> has expired.";
    return sendUpdated(handlerBid, grower, msg);
  }

  public boolean sendUpdated(HandlerBid handlerBid, String msg) {
    boolean success = true;

    for (Grower grower : handlerBid.getAllGrowers()) {
      if(!sendUpdated(handlerBid, grower, msg)) success = false;
    }

    return success; 
  }

  public boolean sendUpdated(HandlerBid handlerBid, Grower grower, String msg) {
    return twilioMessageService.sendMessage(grower.getPhoneNumberString(), msg);
  }

  private String createBodyText(Grower curGrower, HandlerBid handlerBid) {
    Long id = handlerBid.getId();
    String body = "Hi " + curGrower.getFullName() + ",\n"
                + "You have received a new bid: \n"
                + handlerBid.getAlmondVariety() + "\n"
                + handlerBid.getAlmondSize() + "\n"
                + handlerBid.getAlmondPoundsString() + "lbs\n"
                + handlerBid.getPricePerPound() + "/lb\n" 
                + handlerBid.getComment() + "\n"
                + "-" + handlerBid.getHandler().getCompanyName() + " " 
                + handlerBid.getHandler().getPhoneNumberString() + "\n\n"
                + "Respond with the bid ID(" + id + ") "
                + "followed by the amount of pounds you would like to accept (0 for rejection).\n"
                + "Bid ID: " + id + "\n"
                + "Example: " + id + " 10,000";
    return body;
  } 

}