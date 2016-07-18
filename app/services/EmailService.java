package services;

import java.util.ArrayList;
import java.util.List;

import models.EmailAddress;

public class EmailService {

  public static boolean verifyEmailAddress(String emailAddr) {
    // TODO Implement
    
    return true;
  }

  /* Converts list of Strings into new list of EmailAddresses. Returned Email Addresses will
   * be saved as they are added to the list.
   * List of emailStrings should already have been verified to be a valid set of Strings.
   * 
   * WARNING: Error will be thrown if any strings from the list are not a valid email.
   */
  public static List<EmailAddress> stringToEmailAddressList(List<String> emailStrings) {
    List<EmailAddress> emailAddresses = new ArrayList<>();

    for (String addrStr : emailStrings) {
      if (!verifyEmailAddress(addrStr)) {
        throw new RuntimeException("Invalid Email Address passed to email list creation.");
      }

      emailAddresses.add(new EmailAddress(addrStr));
    }

    return emailAddresses;
  }

  public static EmailAddress stringToEmailAddress(String emailAddressString) {
    if (!verifyEmailAddress(emailAddressString)) {
      return null;
    }

    return new EmailAddress(emailAddressString);
  }
}
