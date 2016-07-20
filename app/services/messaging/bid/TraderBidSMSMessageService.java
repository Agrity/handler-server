package services.messaging.bid;

import java.util.ArrayList;
import java.util.List;

import models.PhoneNumber;
import models.HandlerSeller;
import models.TraderBid;
import play.Logger;
import services.messaging.MessageServiceConstants.TwilioFields;
import services.bid_management.BidManagementService;


public class TraderBidSMSMessageService implements TraderBidMessageService {

  private static final TwilioMessageService twilioMessageService = new TwilioMessageService();
  
  /* Takes an bid object and sends out SMS message containing bid to all growers using Twilio account */
  public boolean send(TraderBid traderBid) {
    boolean success = true;

    for (HandlerSeller handlerSeller : traderBid.getAllHandlerSellers()) { 
      if(!send(traderBid, handlerSeller)) success = false;
    }

    return success;
  }
    
  public boolean send(TraderBid traderBid, HandlerSeller handlerSeller) {
    return sendUpdated(traderBid, handlerSeller, createBodyText(handlerSeller, traderBid));
  }

  public boolean sendClosed(TraderBid traderBid) {
    boolean success = true;

    for (HandlerSeller handlerSeller : traderBid.getAllHandlerSellers()) {
      if(!sendClosed(traderBid, handlerSeller)) success = false;
    }

    return success; 
  }

  public boolean sendClosed(TraderBid traderBid, HandlerSeller handlerSeller) {
    String msg = "Your bid (ID " + traderBid.getId() + ") <" + traderBid.getAlmondVariety() + " for " 
        + traderBid.getPricePerPound() + "/lb.> has expired.";
    return sendUpdated(traderBid, handlerSeller, msg);
  }

  public boolean sendUpdated(TraderBid traderBid, String msg) {
    boolean success = true;

    for (HandlerSeller handlerSeller : traderBid.getAllHandlerSellers()) {
      if(!sendClosed(traderBid, handlerSeller)) success = false;
    }

    return success; 
  }

  public boolean sendUpdated(TraderBid traderBid, HandlerSeller handlerSeller, String msg) {
    return twilioMessageService.sendMessage(handlerSeller.getPhoneNumberString(), msg);
  }

  /* ====== TODO ====== */
  private String createBodyText(HandlerSeller handlerSeller, TraderBid traderBid) {
    // Long id = handlerBid.getId();
    // String body = "Hi " + curGrower.getFullName() + ",\n"
    //             + "You have received a new bid: \n"
    //             + handlerBid.getAlmondVariety() + "\n"
    //             + handlerBid.getAlmondSize() + "\n"
    //             + handlerBid.getAlmondPoundsString() + "lbs\n"
    //             + handlerBid.getPricePerPound() + "/lb\n" 
    //             + handlerBid.getComment() + "\n"
    //             + "-" + handlerBid.getHandler().getCompanyName() + " " 
    //             + handlerBid.getHandler().getPhoneNumberString() + "\n\n"
    //             + "Respond with the bid ID(" + id + ") "
    //             + "followed by the amount of pounds you would like to accept (0 for rejection).\n"
    //             + "Bid ID: " + id + "\n"
    //             + "Example: " + id + " 10,000";
    // return body;
    return "";
  } 

}