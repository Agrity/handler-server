package services.messaging.bid;

import java.util.ArrayList;
import java.util.List;

import models.PhoneNumber;
import models.HandlerSeller;
import models.Batch;
import services.messaging.MessageServiceConstants.TwilioFields;
import services.messaging.MessageServiceConstants;
import services.bid_management.TraderBidManagementService;


public class BatchSMSMessageService implements BatchMessageService {

  private static final TwilioMessageService twilioMessageService = new TwilioMessageService();
  
  /* Takes a batch and sends out SMS message containing bid to all growers using Twilio account */
  public boolean send(Batch batch) {
    boolean success = true;

    for (HandlerSeller handlerSeller : batch.getAllHandlerSellers()) { 
      if(!send(batch, handlerSeller)) success = false;
    }

    return success;
  }
    
  public boolean send(Batch batch, HandlerSeller handlerSeller) {
    Long batchId = batch.getId();
    String msg
      = "Hi " + handlerSeller.getFullName() + ",\n"
      + "You have received new bids from " + batch.getTrader().getCompanyName()
      + "! Click on the link below to view and/or accept them.\n" 
      + TwilioFields.getDomain() + "/traderBids/batch/" 
      + batchId + "/display/" + handlerSeller.getId() + "\n"
      + "- " + batch.getTrader().getFullName() + "\n"
      + batch.getTrader().getPhoneNumberString();
    return sendMessage(batch, handlerSeller, msg);
  }

  public boolean sendClosed(Batch batch) {
    boolean success = true;

    for (HandlerSeller handlerSeller : batch.getAllHandlerSellers()) {
      if(!sendClosed(batch, handlerSeller)) success = false;
    }

    return success; 
  }

  public boolean sendClosed(Batch batch, HandlerSeller handlerSeller) {
    String msg = "Your batch (ID " + batch.getId() + ") has expired.";
    return sendMessage(batch, handlerSeller, msg);
  }

  public boolean sendMessage(Batch batch, HandlerSeller handlerSeller, String msg) {
    return twilioMessageService.sendMessage(handlerSeller.getPhoneNumberString(), msg);
  }

}