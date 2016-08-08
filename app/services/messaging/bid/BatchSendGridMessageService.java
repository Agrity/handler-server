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
import models.Trader;
import models.Batch;
import models.EmailAddress;

import play.Logger;

import services.messaging.MessageServiceConstants;
import services.bid_management.TraderBidManagementService;
import services.HandlerSellerService;
import services.impl.EbeanHandlerSellerService;

public class BatchSendGridMessageService implements BatchMessageService {

  private static final SendGridMessageService sendGridService = new SendGridMessageService();
  private static final HandlerSellerService handlerSellerService = new EbeanHandlerSellerService();

  private static final Email FROM_EMAIL
      = new Email(MessageServiceConstants.EmailFields.getFromEmailAddress());

  public BatchSendGridMessageService() {}

  @Override
  public boolean send(Batch batch) {
    boolean success = true;

    for (HandlerSeller handlerSeller : batch.getAllHandlerSellers()) { 
      if(!send(batch, handlerSeller)) success = false;
    }

    return success;
  }

  public boolean send(Batch batch, List<HandlerSeller> handlerSellers) {
    boolean success = true;

    for (HandlerSeller handlerSeller : handlerSellers) { 
      if(!send(batch, handlerSeller)) success = false;
    }

    return success;
  }

  /* ===== TODO: HTML for accept ===== */
  public boolean send(Batch batch, HandlerSeller handlerSeller) {
    String emailAddr = handlerSeller.getEmailAddress().getEmailAddress();

    Email toEmail = new Email(emailAddr);

    Content content
    = new Content(
      "text/html", 
      MessageServiceConstants.EmailFields.getBatchHTMLContent(batch, handlerSeller));

    Mail mail
    = new Mail(
      FROM_EMAIL,
      MessageServiceConstants.EmailFields.getSubjectLineNewBid(),
      toEmail,
      content);
  
    return sendGridService.sendEmail(mail, toEmail);
  }

  public boolean sendClosed(Batch batch) {
    boolean success = true;

    for (HandlerSeller handlerSeller : batch.getAllHandlerSellers()) { 
      if(!send(batch, handlerSeller)) success = false;
    }

    return success; 
  }

  public boolean sendClosed(Batch batch, HandlerSeller handlerSeller) {
    String emailAddr = handlerSeller.getEmailAddressString();

    Email toEmail = new Email(emailAddr);

    Content content
    = new Content(
      "text/html",
      "Your batch " + batch.getId() + " has expired.");

    Mail mail
    = new Mail(
      FROM_EMAIL,
      MessageServiceConstants.EmailFields.getSubjectLineExpired(batch.getId()),
      toEmail,
      content);

    return sendGridService.sendEmail(mail, toEmail);
  }

  public boolean sendReceipt(TraderBid traderBid, long handlerSellerId, long pounds) {
    HandlerSeller handlerSeller = handlerSellerService.getById(handlerSellerId);
    String handlerSellerEmailAddr = handlerSeller.getEmailAddressString();
    Email handlerSellerEmail = new Email(handlerSellerEmailAddr);

    Content handlerSellerContent
    = new Content(
      "text/html",
      MessageServiceConstants.EmailFields
      .getHandlerSellerReceiptHTMLContent(traderBid, handlerSeller, pounds));

    Mail handlerSellerMail
    = new Mail(
      FROM_EMAIL,
      MessageServiceConstants.EmailFields.getSubjectLineReceipt(traderBid.getId()),
      handlerSellerEmail,
      handlerSellerContent);


    Trader trader = handlerSeller.getTrader();
    String traderEmailAddr = trader.getEmailAddressString();
    Email traderEmail = new Email(traderEmailAddr);

    Content traderContent
    = new Content(
      "text/html",
      MessageServiceConstants.EmailFields
      .getTraderReceiptHTMLContent(traderBid, handlerSeller, pounds));

    Mail traderMail
    = new Mail(
      FROM_EMAIL,
      MessageServiceConstants.EmailFields.getSubjectLineReceipt(traderBid.getId()),
      traderEmail,
      traderContent);

    return sendGridService.sendEmail(handlerSellerMail, handlerSellerEmail) 
      && sendGridService.sendEmail(traderMail, traderEmail);
  }

}
