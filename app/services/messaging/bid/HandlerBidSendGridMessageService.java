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

import models.Grower;
import models.HandlerBid;
import models.EmailAddress;

import play.Logger;

import services.messaging.MessageServiceConstants;
import services.bid_management.BidManagementService;

public class HandlerBidSendGridMessageService implements HandlerBidMessageService {

  private static final SendGridMessageService sendGridService = new SendGridMessageService();

  private static final Email FROM_EMAIL
      = new Email(MessageServiceConstants.EmailFields.getFromEmailAddress());

  public HandlerBidSendGridMessageService() {}

  @Override
  public boolean send(HandlerBid handlerBid) {
    boolean success = true;

    for (Grower grower : handlerBid.getAllGrowers()) {
      if(!send(handlerBid, grower)) success = false;
    }

    return success;
  }

  public boolean send(HandlerBid handlerBid, Grower grower) {
    String emailAddr = grower.getEmailAddress().getEmailAddress();

    Email toEmail = new Email(emailAddr);

    Content content
    = new Content(
      "text/html",
      MessageServiceConstants.EmailFields.getEmailHTMLContent(handlerBid, grower));

    Mail mail
    = new Mail(
      FROM_EMAIL,
      MessageServiceConstants.EmailFields.getSubjectLineNewBid(),
      toEmail,
      content);

    return sendGridService.sendEmail(mail, toEmail);
  }

  public boolean sendClosed(HandlerBid handlerBid) {
    boolean success = true;

    for (Grower grower : handlerBid.getAllGrowers()) {
      if(!sendClosed(handlerBid, grower)) success = false;
    }

    return success; 
  }

  public boolean sendClosed(HandlerBid handlerBid, Grower grower) {
    String emailAddr = grower.getEmailAddressString();

    Email toEmail = new Email(emailAddr);

    Content content
    = new Content(
      "text/plain",
      "Your bid <" + handlerBid.getAlmondVariety() + " for " 
      + handlerBid.getPricePerPound() + "/lb.> has expired.");

    Mail mail
    = new Mail(
      FROM_EMAIL,
      MessageServiceConstants.EmailFields.getSubjectLineExpired(handlerBid.getId()),
      toEmail,
      content);

    return sendGridService.sendEmail(mail, toEmail);
  }

  public boolean sendUpdated(HandlerBid handlerBid, String msg) {
    boolean success = true;

    for (Grower grower : handlerBid.getAllGrowers()) {
      if(!sendUpdated(handlerBid, grower, msg)) success = false;
    }

    return success; 
  }

  public boolean sendUpdated(HandlerBid handlerBid, Grower grower, String msg) {
    boolean success = true;
    String emailAddr = grower.getEmailAddressString();

    Email toEmail = new Email(emailAddr);

    Content content = new Content("text/plain", msg);

    Mail mail
    = new Mail(
      FROM_EMAIL,
      MessageServiceConstants.EmailFields.getSubjectLineUpdated(handlerBid.getId()),
      toEmail,
      content);

    return sendGridService.sendEmail(mail, toEmail);
  }

}
