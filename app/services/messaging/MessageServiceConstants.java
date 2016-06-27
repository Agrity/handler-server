package services.messaging;

import models.EmailAddress;
import models.Grower;
import models.Offer;

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

    public static String getSubjectLineNewOffer() {
      return "[Action Required] New Almond Transaction Offer";
    }

    public static String getSubjectLineExpired(Long id) {
      return "Offer " + Long.toString(id) + " Expired";
    }

    public static String getSubjectLineUpdated(Long id) {
      return "Offer " + Long.toString(id) + " Updated";
    }
  }
}
