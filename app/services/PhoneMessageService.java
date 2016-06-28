package services;

import play.Logger;


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

}
