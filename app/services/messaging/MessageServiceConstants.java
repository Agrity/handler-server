package services.messaging;

import models.EmailAddress;
import models.Grower;
import models.HandlerBid;
import models.Batch;
import models.HandlerSeller;
import models.TraderBid;

import com.twilio.sdk.*;
import com.twilio.sdk.resource.factory.*;
import com.twilio.sdk.resource.instance.*;
import com.twilio.sdk.resource.list.*;
import com.twilio.sdk.TwilioRestResponse;

public abstract class MessageServiceConstants {
  public static final String DOMAIN_KEY = "domainString";

  public static class EmailFields {
    private static String domain;
    // Used for SendGrid
    private static final String FROM_NAME = "Agrity";
    private static final String FROM_EMAIL_ADDRESS = "noreply@agrity.net";

    // Used for Raw Email
    private static final String FROM_ADDRESS = "Agrity <noreply@agrity.net>";

    public static String getFromName() {
      return FROM_NAME;
    }

    public static String getFromEmailAddress() {
      return FROM_EMAIL_ADDRESS;
    }

    public static String getEmailHTMLContent(HandlerBid handlerBid, Grower grower) {
      return views.html.emailBidBody.render(handlerBid, grower, domain).toString();
    }

    public static String getBatchHTMLContent(Batch batch, HandlerSeller handlerSeller) {
      return views.html.emailBatchBody.render(batch, handlerSeller, domain).toString();
    }

    public static String getTraderReceiptHTMLContent(TraderBid traderBid, HandlerSeller handlerSeller) {
      return views.html.emailTraderReceiptBody.render(traderBid, handlerSeller, domain).toString();
    }

    public static String getHandlerSellerReceiptHTMLContent(TraderBid traderBid, HandlerSeller handlerSeller) {
      return views.html.emailHandlerSellerReceiptBody.render(traderBid, handlerSeller, domain).toString();
    }

    public static void setDomain(String domainString) {
      domain = domainString;
    }

    public static String getDomain() {
      return domain;
    }

    public static String getFromAddress() {
      return FROM_ADDRESS;
    }

    public static String getToAddress(String name, EmailAddress emailAddress) {
      return name + " <" + emailAddress + ">";
    }

    public static String getSubjectLineNewBid() {
      return "[Action Required] New Almond Transaction Bid";
    }

    public static String getSubjectLineExpired(Long id) {
      return "Bid " + Long.toString(id) + " Expired";
    }

    public static String getSubjectLineUpdated(Long id) {
      return "Bid " + Long.toString(id) + " Updated";
    }

    public static String getSubjectLineReceipt(Long id) {
      return "Bid (ID " + id + ") Transaction Receipt";
    }
  }

  public static class TwilioFields {
    private static final String ACCOUNT_SID = "AC486e1d38a0597859c70f32589ea3ab1a";
    private static final String AUTH_TOKEN = "8e930e13c7f8f31606ac3089baf8adec";
    private static final String TEST_AUTH_TOKEN = "4d6520f79e75c93c4d80ec72a68fd6f1";
    private static final String TWILIO_NUMBER = "+15592057098";
    
    private static final TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
    private static final Account account = client.getAccount();
    private static final MessageFactory messageFactory = account.getMessageFactory();

    public static MessageFactory getMessageFactory() {
      return messageFactory;
    }

    public static String getTwilioNumber() {
      return TWILIO_NUMBER;
    }

    public static String getDomain() {
      return EmailFields.getDomain();
    }
  }
}
