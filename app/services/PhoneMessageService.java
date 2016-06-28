package services;

import play.Logger;
import java.util.ArrayList;
import java.util.List;
import models.PhoneNumber;

public class PhoneMessageService {

  private static final String formatExample = "Example phone number: +1650123456";

  public static boolean verifyPhoneNumber(String phoneNumber) {
     if (phoneNumber.length() != 12) {
       Logger.error(phoneNumber + " has an invalid phone number length. " + formatExample);
       return false;
     }

     if (phoneNumber.charAt(0) != '+') {
       Logger.error(phoneNumber + " does not begin with leading '+'. " + formatExample);
       return false;
     }

     if (phoneNumber.charAt(1) != '1') {
       Logger.error(phoneNumber + " does not include a trunk prefix of 1. " + formatExample);
     	return false;
     }
    
    return true;
  }

  /* Converts list of Strings into new list of PhoneNumbers. Returned PhoneNumbers will
   * be saved as they are added to the list.
   * List of phone numbers should already have been verified to be a valid set of Strings.
   * 
   * WARNING: Error will be thrown if any strings from the list are not a phone number.
   */
  public static List<PhoneNumber> stringToPhoneNumberList(List<String> phoneNumberStrings) {
    List<PhoneNumber> phoneNumbers = new ArrayList<>();

    for (String phoneStr : phoneNumberStrings) {
      if (!verifyPhoneNumber(phoneStr)) {
        throw new RuntimeException("Invalid Phone Number passed to phone number list creation.");
      }

      phoneNumbers.add(new PhoneNumber(phoneStr));
    }

    return phoneNumbers;
  }
}
