package services.parsers;

import com.fasterxml.jackson.databind.JsonNode;

import models.EmailAddress;
import models.Handler;
import models.Trader;
import models.PhoneNumber;

import java.util.ArrayList;
import java.util.List;

import services.EmailService;
import services.HandlerService;
import services.impl.EbeanHandlerService;
import services.TraderService;
import services.impl.EbeanTraderService;
import services.PhoneMessageService;
import services.HandlerSellerService;
import services.impl.EbeanHandlerSellerService;

import play.Logger;

/**
 * Base class to parse json data. Intended to be extended for specific json data types.
 */
public abstract class BaseParser {
  // Error variables
  private boolean valid;
  private String errorMessage;

  private boolean validitySet = false;

  protected final HandlerService handlerService;
  protected final TraderService traderService;
  protected final HandlerSellerService handlerSellerService;

  public BaseParser() {
    // TODO -- Extremely Hacky -- Change to Dependency Injection.
    //      See Guice AssistedInjection
    handlerService = new EbeanHandlerService();
    traderService = new EbeanTraderService();
    handlerSellerService = new EbeanHandlerSellerService();
  }

  public boolean isValid() {
    ensureValidityIsSet();
    return valid;
  }

  // WARNING: Should only be called after isValid() has been checked to be false
  public String getErrorMessage() {
    ensureInvalid();

    return errorMessage;
  }

  protected void setValid() {
    ensureValidityIsNotSet();
    validitySet = true;

    valid = true;
    errorMessage = null;
  }

  protected void setInvalid(String errorMessge) {
    ensureValidityIsNotSet();
    validitySet = true;

    valid = false;
    errorMessage = errorMessge;
  }

  protected void ensureValid() {
    ensureValidityIsSet();
    if (!isValid()) {
      throw new RuntimeException("Parser Invalid: valid parser expected.");
    }
  }

  protected void ensureInvalid() {
    ensureValidityIsSet();
    if (isValid()) {
      throw new RuntimeException("Parser Valid: invalid parser expected.");
    }
  }

  /* 
   * For use in Grower/HandlerBidJsonParser's.
   *
   * Attempt to extract the Handler from the given json data, via the HANDLER_ID field. If there
   * is an error, the parser will be set to invalid with appropriate error message, and null will
   * be returned.
   *
   * WARNING: Parser set to invalid if error is encountered.
   */

  protected Handler parseHandler(JsonNode data) {
    // Check handler id is present.
    if (!data.has(JsonConstants.HANDLER_ID)) {
      setInvalid(missingParameterError(JsonConstants.HANDLER_ID));
      return null;

    } 

    String handlerIdStr = data.findValue(JsonConstants.HANDLER_ID).asText();

    // Ensure handler id is valid integer format.
    Long handlerId = parseLong(handlerIdStr);
    if (handlerId == null) {
      setInvalid("Handler id value [" + handlerIdStr + "] is not a valid long integer.\n");
      return null;
    }
    
    // Check handler exists with given id.
    Handler handler = handlerService.getById(handlerId);
    if (handler == null) {
      setInvalid("Handler does not exist with handler id [" + handlerId + "].\n");
      return null;
    }

    return handler;
  }

  protected Trader parseTrader(JsonNode data) {
    // Check trader id is present.
    if (!data.has(JsonConstants.TRADER_ID)) {
      setInvalid(missingParameterError(JsonConstants.TRADER_ID));
      return null;

    } 

    String traderIdStr = data.findValue(JsonConstants.TRADER_ID).asText();

    // Ensure trader id is valid integer format.
    Long traderId = parseLong(traderIdStr);
    if (traderId == null) {
      setInvalid("Trader id value [" + traderIdStr + "] is not a valid long integer.\n");
      return null;
    }
    
    // Check trader exists with given id.
    Trader trader = traderService.getById(traderId);
    if (trader == null) {
      setInvalid("Trader does not exist with trader id [" + traderId + "].\n");
      return null;
    }

    return trader;
  }

   /* 
   * Attempt to extract the user name from the given json data. Parameter name of the json data
   * to be extracted is given by parameterName. If there is an error, the parser will be set to
   * invalid with appropriate error message, and null will be returned.
   *
   * WARNING: Parser set to invalid if error is encountered.
   */
  protected String parseName(JsonNode data, String paramaterName) {
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
   * Attempt to extract the company name from the given json data. If there is an error, the parser
   * will be set to invalid with appropriate error message, and null will be returned.
   *
   * WARNING: Parser set to invalid if error is encountered.
   */
  protected String parseCompanyName(JsonNode data) {

    // Check company name is present.
    if (!data.has(JsonConstants.COMPANY_NAME)) {
      setInvalid(missingParameterError(JsonConstants.COMPANY_NAME));
      return null;

    } 
    
    String companyName = data.findValue(JsonConstants.COMPANY_NAME).asText();

    /* Check if company name is already in use (only for users). */
    if (!handlerService.checkCompanyNameAvailable(companyName)
        || !traderService.checkCompanyNameAvailable(companyName)) {
      setInvalid("Company name [" + companyName + "] is already in use.\n");
      return null;
    }


    return companyName;
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
  protected PhoneNumber parsePhoneNumber(JsonNode data) {
    // Check if phone number is present.
    if (!data.has(JsonConstants.PHONE_NUMBER)) {
      setInvalid(missingParameterError(JsonConstants.PHONE_NUMBER));
      return null;
    } 
    
    String phoneNumberString = "+1" + data.findValue(JsonConstants.PHONE_NUMBER).asText();


    PhoneNumber phoneNumber = PhoneMessageService.stringToPhoneNumber(phoneNumberString);

    if (phoneNumber == null) {
      setInvalid("Phone number " + phoneNumberString + " is not in a valid format.\n");
      return null;
    }

    return phoneNumber;
  }

  /* NOTE: No check for previously used email. */
  protected EmailAddress parserSellerEmailAddress(JsonNode data) {
    return parseEmailAddress(data, /* Unique User Email Check */ false);
  }

  /* NOTE: Checks for previously used email. */
  protected EmailAddress parserUserEmailAddress(JsonNode data) {
    return parseEmailAddress(data, /* Unique User Email Check */ true);
  }

  /* WARNING: Parser set to invalid if error is encountered.
   * NOTE: uniqueUserEmail is flag to check whether emailAdress is already in use
   *       by another USER. */
  private EmailAddress parseEmailAddress(JsonNode data, boolean uniqueUserEmail) {

    // Check email address is present.
    if (!data.has(JsonConstants.EMAIL_ADDRESS)) {
      setInvalid(missingParameterError(JsonConstants.EMAIL_ADDRESS));
      return null;

    } 
    
    String emailAddressString 
      = (data.findValue(JsonConstants.EMAIL_ADDRESS).asText()).toLowerCase();


    /* Check if email is already in use. */
    if (uniqueUserEmail) {
      if (!handlerService.checkEmailAddressAvailable(emailAddressString)
          || !traderService.checkEmailAddressAvailable(emailAddressString)) {
        setInvalid("Email address [" + emailAddressString + "] is already in use.\n");
        return null;
      }
    }

    EmailAddress emailAddress = EmailService.stringToEmailAddress(emailAddressString);

    if (emailAddress == null) {
      setInvalid("Email address [" + emailAddressString + "] is not in a valid format.\n");
      return null;
    }

    return emailAddress;
  }


  /*
   * Wrapper to parse given string to long. If string is null, or not in proper integer format,
   * null will be returned instead.
   */
  protected static Long parseLong(String numStr) {
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

  protected static Integer parseInteger(String numStr) {
    if (numStr == null) {
      return null;
    }

    try {
      Integer num = Integer.parseInt(numStr);
      return num;

    } catch (NumberFormatException e) {
      return null;
    }
  }

  protected static String missingParameterError(String paramaterName) {
    return "Missing parameter [ " + paramaterName + " ]\n";
  }

  private void ensureValidityIsSet() {
    if (!validitySet) {
      throw new RuntimeException("Parser Validity Not Set: validity expected to be set.");
    }
  }

  private void ensureValidityIsNotSet() {
    if (validitySet) {
      throw new RuntimeException("Parser Validity Set: validity expected to not be set.");
    }
  }


  protected static class JsonConstants {
    private static final String HANDLER_ID = "handler_id";
    private static final String TRADER_ID = "trader_id";

    private static final String EMAIL_ADDRESS = "email_address";
    private static final String PHONE_NUMBER = "phone_number";
    protected static final String FIRST_NAME = "first_name";
    protected static final String LAST_NAME = "last_name";
    protected static final String COMPANY_NAME = "company_name";
  }
}
