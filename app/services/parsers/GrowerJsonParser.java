package services.parsers;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

import models.EmailAddress;
import models.Grower;
import models.Handler;
import models.PhoneNumber;

import services.EmailService;
import services.PhoneMessageService;
import play.Logger;

/**
 * Class to parse json data to create new Grower.
 *
 * Expected Json Structure:
 *  {
 *    HANDLER_ID: ... (Constant in JsonParser super class)
 *
 *    FIRST_NAME: ...
 *    LAST_NAME: ...
 *
 *
 *    ---- Optional Parameters ----
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
public class GrowerJsonParser extends BaseParser {

  private Handler handler;
  private String firstName;
  private String lastName;
  private List<EmailAddress> emailAddresses;
  private List<PhoneNumber> phoneNumbers;

  public GrowerJsonParser(JsonNode data) {
    super();

    handler = parseHandler(data);
    if (handler == null) {
      // Parser set to invalid with proper error message.
      return;
    }
    
    firstName = parseGrowerName(data, GrowerJsonConstants.FIRST_NAME);
    if (firstName == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    lastName = parseGrowerName(data, GrowerJsonConstants.LAST_NAME);
    if (lastName == null) {
      // Parser set to invalid with proper error message.
      return;
    }
    
    emailAddresses = parseEmailAddresses(data);
    if (emailAddresses == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    phoneNumbers = parsePhoneNumbers(data);
    if (phoneNumbers == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    // Valid json data recieved
    setValid();
  }

  public Grower formGrower() {
    if (!isValid()) {
      throw new RuntimeException("Attempted to create Grower from invalid parser.\n");
    }

    Grower newGrower = new Grower(
        getHandler(),
        getFirstName(),
        getLastName(),
        getEmailAddresses(),
        getPhoneNumbers());

    return newGrower;
  }

  public Handler getHandler() {
    ensureValid();
    return handler;
  }

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

  /* 
   * Attempt to extract the grower name from the given json data. Parameter name of the json data
   * to be extracted is given by parameterName. If there is an error, the parser will be set to
   * invalid with appropriate error message, and null will be returned.
   *
   * WARNING: Parser set to invalid if error is encountered.
   */
  private String parseGrowerName(JsonNode data, String paramaterName) {
    // Ensure parameterName is present.
    if (!data.has(paramaterName)) {
      setInvalid(missingParameterError(paramaterName));
      return null;
    } 
    String name = data.findValue(paramaterName).asText();
    
    
    // TODO Check valid human name. (i.e. length, no numbers, etc.)
    
    return name;
  }

  /* 
   * Attempt to extract emails from the given json data, via the HANDLER_ID field. If there
   * is an error, the parser will be set to invalid with appropriate error message, and null will
   * be returned.
   * 
   * Note: Email is an optional parameter so an error will not be set if the value is not found in
   * the json data. And empty List will be returned instead.
   *
   * WARNING: Parser set to invalid if error is encountered.
   */
  private List<EmailAddress> parseEmailAddresses(JsonNode data) {
    // Email addresses not present in json node. Returning empty list.
    if (!data.has(GrowerJsonConstants.EMAIL_ADDRESSES)) {
      return new ArrayList<>();
    }

    JsonNode emailAddrs = data.get(GrowerJsonConstants.EMAIL_ADDRESSES);

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
  private List<PhoneNumber> parsePhoneNumbers(JsonNode data) {
    // Phone numbers not present in json node. Returning empty list.


    if (!data.has(GrowerJsonConstants.PHONE_NUMBERS)) {
      return new ArrayList<>();
    }

    JsonNode phoneNums = data.get(GrowerJsonConstants.PHONE_NUMBERS);

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

  private static class GrowerJsonConstants {
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";

    // Optional Parameters
    private static final String EMAIL_ADDRESSES = "email_addresses";
    private static final String PHONE_NUMBERS = "phone_numbers";
  }
}
