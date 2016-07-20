package services.messaging.bid;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;


import java.io.IOException;
import java.util.List;

import models.HandlerSeller;
import models.TraderBid;
import models.EmailAddress;

import play.Logger;

import services.messaging.MessageServiceConstants;
import services.bid_management.TraderBidManagementService;

public class TraderBidSendGridMessageService implements TraderBidMessageService {

  private static final SendGridMessageService sendGridService = new SendGridMessageService();

  private static final Email FROM_EMAIL
      = new Email(MessageServiceConstants.EmailFields.getFromEmailAddress());

  public TraderBidSendGridMessageService() {}

  @Override
  public boolean send(TraderBid traderBid) {
    boolean success = true;

    for (HandlerSeller handlerSeller : traderBid.getAllHandlerSellers()) { 
      if(!send(traderBid, handlerSeller)) success = false;
    }

    return success;
  }

  /* ===== TODO: HTML for accept ===== */
  public boolean send(TraderBid traderBid, HandlerSeller handlerSeller) {
    String emailAddr = handlerSeller.getEmailAddress().getEmailAddress();

    Email toEmail = new Email(emailAddr);

    Content content
    = new Content(
      "text/html", "In progress.");
      //MessageServiceConstants.EmailFields.getEmailHTMLContent(traderBid, handlerSeller));

    Mail mail
    = new Mail(
      FROM_EMAIL,
      MessageServiceConstants.EmailFields.getSubjectLineNewBid(),
      toEmail,
      content);

    return sendGridService.sendEmail(mail, toEmail);
  }

  public boolean sendClosed(TraderBid traderBid) {
    boolean success = true;

    for (HandlerSeller handlerSeller : traderBid.getAllHandlerSellers()) { 
      if(!send(traderBid, handlerSeller)) success = false;
    }

    return success; 
  }

  public boolean sendClosed(TraderBid traderBid, HandlerSeller handlerSeller) {
    String emailAddr = handlerSeller.getEmailAddressString();

    Email toEmail = new Email(emailAddr);

    Content content
    = new Content(
      "text/plain",
      "Your bid <" + traderBid.getAlmondVariety() + " for " 
      + traderBid.getPricePerPound() + "/lb.> has expired.");

    Mail mail
    = new Mail(
      FROM_EMAIL,
      MessageServiceConstants.EmailFields.getSubjectLineExpired(traderBid.getId()),
      toEmail,
      content);

    return sendGridService.sendEmail(mail, toEmail);
  }

  public boolean sendUpdated(TraderBid traderBid, String msg) {
    boolean success = true;

    for (HandlerSeller handlerSeller : traderBid.getAllHandlerSellers()) { 
      if(!send(traderBid, handlerSeller)) success = false;
    }

    return success; 
  }

  public boolean sendUpdated(TraderBid traderBid, HandlerSeller handlerSeller, String msg) {
    boolean success = true;
    String emailAddr = handlerSeller.getEmailAddressString();

    Email toEmail = new Email(emailAddr);

    Content content = new Content("text/plain", msg);

    Mail mail
    = new Mail(
      FROM_EMAIL,
      MessageServiceConstants.EmailFields.getSubjectLineUpdated(traderBid.getId()),
      toEmail,
      content);

    return sendGridService.sendEmail(mail, toEmail);
  }

}
