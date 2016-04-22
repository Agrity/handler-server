package services.messaging;

import models.EmailAddress;

public abstract class MessageServiceConstants {
  public static class EmailFields {
    private static final String FROM_ADDRESS = "Agrity <agritycommodities@gmail.com>";

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
}
