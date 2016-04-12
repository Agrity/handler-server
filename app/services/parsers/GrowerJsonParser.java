package services.parsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.List;

import models.Handler;

import play.Logger;

import services.EmailService;
import services.HandlerService;
import services.PhoneMessageService;

/**
 * Class to parse json data to create new Grower.
 *
 * Expected Json Structure:
 *  {
 *    HANDLER_ID: ...
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
public class GrowerJsonParser extends JsonParser {

  private Handler handler;
  private String firstName;
  private String lastName;
  private List<String> emailAddresses;
  private List<String> phoneNumbers;

  public GrowerJsonParser(JsonNode data) {
    super();

    handler = parseHandler(data);
    if (handler == null) {
      // Parser set to invalid with proper error message.
      return;
    }
    
    firstName = parseGrowerName(data, JsonConstants.FIRST_NAME);
    if (firstName == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    lastName = parseGrowerName(data, JsonConstants.LAST_NAME);
    if (lastName == null) {
      // Parser set to invalid with proper error message.
      return;
    }
    
    emailAddresses = parseEmailAddresses(data);
    if (emailAddresses == null) {
      return;
    }

    phoneNumbers = parsePhoneNumbers(data);
    if (phoneNumbers == null) {
      return;
    }

    // Valid json data recieved
    setValid();
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

  public List<String> getEmailAddresses() {
    ensureValid();
    return emailAddresses;
  }

  public List<String> getPhoneNumbers() {
    ensureValid();
    return phoneNumbers;
  }

  /* 
   * Attempt to extract the Handler from the given json data, via the HANDLER_ID field. If there
   * is an error, the parser will be set to invalid with appropriate error message, and null will
   * be returned.
   *
   * WARNING: Parser set to invalid if error is encountered.
   */
  private Handler parseHandler(JsonNode data) {
    String handlerIdStr = data.findValue(JsonConstants.HANDLER_ID).asText();

    // Check handler id is present.
    if (handlerIdStr == null) {
      setInvalid(missingParameterError(JsonConstants.HANDLER_ID));
      return null;

    } 

    // Ensure handler id is valid integer format.
    Long handlerId = parseLong(handlerIdStr);
    if (handlerId == null) {
      setInvalid("Handler id value [" + handlerIdStr + "] is not a valid long integer.\n");
      return null;
    }
    
    // Check handler exists with given id.
    Handler handler = HandlerService.getHandler(handlerId);
    if (handler == null) {
      setInvalid("Handler does not exist with handler id [" + handlerId + "].\n");
      return null;
    }

    return handler;
  }

  /* 
   * Attempt to extract the grower name from the given json data. Parameter name of the json data
   * to be extracted is given by parameterName. If there is an error, the parser will be set to
   * invalid with appropriate error message, and null will be returned.
   *
   * WARNING: Parser set to invalid if error is encountered.
   */
  private String parseGrowerName(JsonNode data, String paramaterName) {
    String name = data.findValue(paramaterName).asText();
    if (name == null) {
      setInvalid(missingParameterError(paramaterName));
      return null;
    } 
    
    // TODO Check valid human name. (i.e. length, no numbers, etc.)
    
    return null;
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
  private List<String> parseEmailAddresses(JsonNode data) {
    JsonNode emailAddrs = data.get(JsonConstants.EMAIL_ADDRESSES);

    // Email addresses not present in json node. Returning empty list.
    if (emailAddrs == null) {
      return new ArrayList<>();
    }

    // Emails should be formatted as an array of strings.
    if (!emailAddrs.isArray()) {
      setInvalid("Email Addresses Format Invalid: array of strings expected.");
      return null;
    }


    List<String> processedEmailAddresses = new ArrayList<>();

    for (JsonNode node : emailAddrs) {
      String emailAddr = node.asText();

      // Ensure email address is valid.
      if (EmailService.verifyEmailAddress(emailAddr)) {
        setInvalid("Invalid Email Address: [" + node + "] is not a valid email address.");
        return null;
      }

      processedEmailAddresses.add(emailAddr);
    }

    return processedEmailAddresses;
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
  private List<String> parsePhoneNumbers(JsonNode data) {
    JsonNode phoneNums = data.get(JsonConstants.PHONE_NUMBERS);

    // Phone numbers not present in json node. Returning empty list.
    if (phoneNums == null) {
      return new ArrayList<>();
    }

    // Phone numbers should be formatted as an array of strings.
    if (!phoneNums.isArray()) {
      setInvalid("Phone Number Format Invalid: array of strings expected.");
      return null;
    }


    List<String> processedPhoneNumbers = new ArrayList<>();

    for (JsonNode node : phoneNums) {
      String phoneNum = node.asText();

      // Ensure phone number is valid.
      if (PhoneMessageService.verifyPhoneNumber(phoneNum)) {
        setInvalid("Invalid Email Address: [" + node + "] is not a valid email address.");
        return null;
      }

      processedPhoneNumbers.add(phoneNum);
    }

    return processedPhoneNumbers;
  }

  /*
   * Wrapper to parse given string to long. If string is null, or not in proper integer format,
   * null will be returned instead.
   */
  private static Long parseLong(String numStr) {
    if (numStr == null) {
      return null;
    }

    try {
      Long num = Long.parseLong(numStr);
      return num;

    } catch (NumberFormatException e) {
      return null;
    }
  }

  private static class JsonConstants {
    private static final String HANDLER_ID = "handler_id";

    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";

    // Optional Parameters
    private static final String EMAIL_ADDRESSES = "email_addresses";
    private static final String PHONE_NUMBERS = "phone_numbers";
  }
}
