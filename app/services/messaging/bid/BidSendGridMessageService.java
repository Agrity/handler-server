package services.messaging.bid;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.client.HttpResponseException;

import java.io.IOException;
import java.util.List;

import models.Grower;
import models.HandlerBid;
import models.EmailAddress;

import play.Logger;

import services.messaging.MessageServiceConstants;
import services.bid_management.BidManagementService;

public class BidSendGridMessageService implements BidMessageService {

  private static final String SENDGRID_API_KEY = "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";

  private static final SendGrid SENDGRID = new SendGrid(SENDGRID_API_KEY);

  private static final Email FROM_EMAIL
      = new Email(MessageServiceConstants.EmailFields.getFromEmailAddress());

  public BidSendGridMessageService() {}

  @Override
  public boolean send(HandlerBid handlerBid) {
    boolean success = true;

    for (Grower grower : handlerBid.getAllGrowers()) {
      if(!send(handlerBid, grower)) success = false;
    }

    return success;
  }

  public boolean send(HandlerBid handlerBid, Grower grower) {
    boolean success = true;
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

    if (!sendEmail(mail, toEmail)) success = false;

    return success;    
  }

  public boolean sendClosed(HandlerBid handlerBid) {
    boolean success = true;

    for (Grower grower : handlerBid.getAllGrowers()) {
      if(!sendClosed(handlerBid, grower)) success = false;
    }

    return success; 
  }

  public boolean sendClosed(HandlerBid handlerBid, Grower grower) {
    boolean success = true;
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

    if (!sendEmail(mail, toEmail)) success = false;

    return success; 
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

    if (!sendEmail(mail, toEmail)) success = false;
    
    return success; 
  }


  private boolean sendEmail(Mail mail, Email email) {
    Request request = new Request();

    try {
      request.method = Method.POST;
      request.endpoint = "mail/send";
      request.body = mail.build();

      Response response = SENDGRID.api(request);

      if (response.statusCode != 250 && response.statusCode != 202) {
        Logger.error("==== Error Sending Email ====");
        Logger.error("" + response.statusCode);
        Logger.error(response.body);
        Logger.error(response.headers.toString() + "\n");

        Logger.error("Email Sent:\n" + mail.buildPretty() + "\n\n");

        return false;
      }
    } catch (HttpResponseException e) {
        Logger.error("==== Error Sending Email ====");
        Logger.error("HttpResponseException: " + e.getMessage());
        Logger.error(ExceptionUtils.getStackTrace(e));

        return false;
    } catch (IOException e) {
        Logger.error("==== Error Sending Email ====");
        Logger.error("IOException: " + e.getMessage());
        Logger.error(ExceptionUtils.getStackTrace(e));

        return false;
    }

    return true;
  }
}
