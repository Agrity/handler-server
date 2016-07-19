package services.messaging.bid;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.client.HttpResponseException;
import java.io.IOException;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

import play.Logger;
import models.EmailAddress;

import services.messaging.MessageServiceConstants;

public class SendGridMessageService {
	

	private static final String SENDGRID_API_KEY = "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";

  private static final SendGrid SENDGRID = new SendGrid(SENDGRID_API_KEY);


	public boolean sendEmail(Mail mail, Email email) {
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