package services.parsers;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

import models.EmailAddress;
import models.PhoneNumber;

import services.EmailService;
import services.PhoneMessageService;
import play.Logger;

/**
 * Class to parse json data to create new Seller.
 *
 * Expected Json Structure:
 *  {
 *    BUYER_ID: ... (Constant in JsonParser super class)
 *
 *    FIRST_NAME: ...
 *    LAST_NAME: ...
 *
 *
 *
 *    EMAIL_ADDRESSES: [
 *      ... ,
 *      ... ,
 *      ...
 *    ]
 *
 *    PHONE_NUMBERS: [
 *      ... ,
 *      ... ,
 *      ...
 *    ]
 *  }
 */
public abstract class BaseSellerJsonParser extends BaseParser {

  private String firstName;
  private String lastName;
  private List<EmailAddress> emailAddresses;
  private List<PhoneNumber> phoneNumbers;


  public String getFirstName() {
    ensureValid();
    return firstName;
  }

  public String getLastName() {
    ensureValid();
    return lastName;
  }

  public List<EmailAddress> getEmailAddresses() {
    ensureValid();
    return emailAddresses;
  }

  public List<PhoneNumber> getPhoneNumbers() {
    ensureValid();
    return phoneNumbers;
  }

  protected void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  protected void setLastName(String lastName) {
    this.lastName = lastName;
  }

  protected void setEmailAddresses(List<EmailAddress> emailAddresses) {
    this.emailAddresses = emailAddresses;
  }

  protected void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
    this.phoneNumbers = phoneNumbers;
  }

  /* 
   * Attempt to extract emails from the given json data, via the BUYER_ID field. If there
   * is an error, the parser will be set to invalid with appropriate error message, and null will
   * be returned.
   * 
   * Note: Email is an optional parameter so an error will not be set if the value is not found in
   * the json data. And empty List will be returned instead.
   *
   * WARNING: Parser set to invalid if error is encountered.
   */
  protected List<EmailAddress> parseEmailAddresses(JsonNode data) {
    // Email addresses not present in json node. Returning empty list.
    if (!data.has(SellerJsonConstants.EMAIL_ADDRESSES)) {
      return new ArrayList<>();
    }

    JsonNode emailAddrs = data.get(SellerJsonConstants.EMAIL_ADDRESSES);

    // Emails should be formatted as an array of strings.
    if (!emailAddrs.isArray()) {
      setInvalid("Email Addresses Format Invalid: array of strings expected.");
      return null;
    }


    List<String> processedEmailAddresses = new ArrayList<>();

    for (JsonNode node : emailAddrs) {
      String emailAddr = node.asText();

      // Ensure email address is valid.
      if (!EmailService.verifyEmailAddress(emailAddr)) {
        setInvalid("Invalid Email Address: [" + node + "] is not a valid email address.");
        return null;
      }

      processedEmailAddresses.add(emailAddr);
    }

    return EmailService.stringToEmailAddressList(processedEmailAddresses);
  }

  /* 
   * Attempt to extract phone numbers from the given json data, via the HANDLER_ID field. If there
   * is an error, the parser will be set to invalid with appropriate error message, and null will
   * be returned.
   * 
   * Note: Phone numbers are an optional parameter so an error will not be set if the value is not
   * found in the json data. And empty List will be returned instead.
   *
   * WARNING: Parser set to invalid if error is encountered.
   */
  protected List<PhoneNumber> parsePhoneNumbers(JsonNode data) {
    // Phone numbers not present in json node. Returning empty list.


    if (!data.has(SellerJsonConstants.PHONE_NUMBERS)) {
      return new ArrayList<>();
    }

    JsonNode phoneNums = data.get(SellerJsonConstants.PHONE_NUMBERS);

    // Phone numbers should be formatted as an array of strings.
    if (!phoneNums.isArray()) {
      setInvalid("Phone Number Format Invalid: array of strings expected.");
      return null;
    }

    List<String> processedPhoneNumbers = new ArrayList<>();

    for (JsonNode node : phoneNums) {
      String phoneNum = node.asText();
      
      phoneNum = "+1" + phoneNum;

      // Ensure phone number is valid.
      if (!PhoneMessageService.verifyPhoneNumber(phoneNum)) {
        setInvalid("Invalid Phone Number: [" + node + "] is not a valid Phone Number.");
        return null;
      }

     

      processedPhoneNumbers.add(phoneNum);
    }

    return PhoneMessageService.stringToPhoneNumberList(processedPhoneNumbers);
  }

  protected static class SellerJsonConstants {
    protected static final String FIRST_NAME = "first_name";
    protected static final String LAST_NAME = "last_name";

    protected static final String EMAIL_ADDRESSES = "email_addresses";
    protected static final String PHONE_NUMBERS = "phone_numbers";
  }
}
