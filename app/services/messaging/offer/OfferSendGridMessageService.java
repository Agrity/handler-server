package services.messaging.offer;

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
import models.Offer;

import play.Logger;

import services.messaging.MessageServiceConstants;

public class OfferSendGridMessageService implements OfferMessageService {

  private static final String SENDGRID_API_KEY = "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";

  private static final SendGrid SENDGRID = new SendGrid(SENDGRID_API_KEY);

  private static final Email FROM_EMAIL
      = new Email(MessageServiceConstants.EmailFields.getFromEmailAddress());

  public OfferSendGridMessageService() {}

  @Override
  public boolean send(Offer offer) {
    boolean success = true;

    for (Grower grower : offer.getAllGrowers()) {
      if(!sendToOne(offer, grower)) success = false;
    }

    return success;
  }

  public boolean sendToOne(Offer offer, Grower grower) {
    boolean success = true;
    List<String> growerEmailAddresses = grower.getEmailAddressStrings();
      for (String emailAddr : growerEmailAddresses) {
        Email toEmail = new Email(emailAddr);

        Content content
            = new Content(
                "text/html",
                MessageServiceConstants.EmailFields.getEmailHTMLContent(offer, grower));

        Mail mail
            = new Mail(
                FROM_EMAIL,
                MessageServiceConstants.EmailFields.getSubjectLine(),
                toEmail,
                content);

        if (!sendEmail(mail, toEmail)) success = false;
      }
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
