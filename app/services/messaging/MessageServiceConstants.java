package services.messaging;

import models.EmailAddress;
import models.Grower;
import models.Offer;

import com.twilio.sdk.*;
import com.twilio.sdk.resource.factory.*;
import com.twilio.sdk.resource.instance.*;
import com.twilio.sdk.resource.list.*;
import com.twilio.sdk.TwilioRestResponse;

public abstract class MessageServiceConstants {
  public static class EmailFields {
    // Used for SendGrid
    private static final String FROM_NAME = "Agrity";
    private static final String FROM_EMAIL_ADDRESS = "agritycommodities@gmail.com";

    // Used for Raw Email
    private static final String FROM_ADDRESS = "Agrity <agritycommodities@gmail.com>";

    public static String getFromName() {
      return FROM_NAME;
    }

    public static String getFromEmailAddress() {
      return FROM_EMAIL_ADDRESS;
    }

    public static String getEmailHTMLContent(Offer offer, Grower grower) {
      return views.html.emailOfferBody.render(offer, grower).toString();
    }

    public static String getFromAddress() {
      return FROM_ADDRESS;
    }

    public static String getToAddress(String name, EmailAddress emailAddress) {
      return name + " <" + emailAddress + ">";
    }

    public static String getSubjectLine() {
      return "[Action Required] New Almond Transaction Offer";
    }
  }

  public static class TwilioFields {
    private static final String ACCOUNT_SID = "ACd061aae08076e124be28d7e5b9f1db7d";
    private static final String AUTH_TOKEN = "cd993273e55be2bd301082722c3ad064";
    private static final String TWILIO_NUMBER = "+12095806972";
    
    private static final TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
    private static final Account account = client.getAccount();
    private static final MessageFactory messageFactory = account.getMessageFactory();

    public static MessageFactory getMessageFactory() {
      return messageFactory;
    }

    public static String getTwilioNumber() {
      return TWILIO_NUMBER;
    }
  }
}
