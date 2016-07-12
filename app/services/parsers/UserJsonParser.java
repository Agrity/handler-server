package services.parsers;

import com.fasterxml.jackson.databind.JsonNode;

import models.User;
import models.Trader;
import models.Handler;
import models.PhoneNumber;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to parse json data to create new Handler.
 *
 * Expected Json Structure:
 *  {
 *    COMPANY_NAME: ...,
 *
 *    EMAIL_ADDRESS: ...,
 *
 *    PASSWORD: ...
 *  }
 */

public class UserJsonParser extends BaseParser {
  // Parsed variables
  private String companyName;
  private String emailAddress;
  private String password;

  private String firstName;
  private String lastName;

  private List<PhoneNumber> phoneNumbers;

  public UserJsonParser(JsonNode data) {
    super();

    companyName = parseCompanyName(data);
    if (companyName == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    emailAddress = parseEmailAddress(data);
    if (emailAddress == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    password = parsePassword(data);
    if (password == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    firstName = parseName(data, UserJsonConstants.FIRST_NAME);
    if (firstName == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    lastName = parseName(data, UserJsonConstants.LAST_NAME);
    if (lastName == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    phoneNumbers = parsePhoneNumbers(data);
    if (phoneNumbers == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    // Valid json data received and processed.
    setValid();
  }

  public Handler formHandler() {
    if (!isValid()) {
      throw new RuntimeException("Attempted to create Handler from invalid parser.\n");
    }
    return new Handler(
        getCompanyName(),
        getFirstName(),
        getLastName(),
        getEmailAddress(),
        getPhoneNumbers(),
        getPassword());
  }

  public Trader formTrader() {
    if (!isValid()) {
      throw new RuntimeException("Attempted to create Trader from invalid parser.\n");
    }
    return new Trader(
        getCompanyName(),
        getFirstName(),
        getLastName(),
        getEmailAddress(),
        getPhoneNumbers(),
        getPassword());
  }

  // WARNING: Should only be called after isValid() has been checked to be true
  public String getCompanyName() {
    ensureValid();

    return companyName;
  }

  // WARNING: Should only be called after isValid() has been checked to be true
  public String getEmailAddress() {
    ensureValid();

    return emailAddress;
  }

  // WARNING: Should only be called after isValid() has been checked to be true
  public String getPassword() {
    ensureValid();

    return password;
  }

  public String getFirstName() {
    ensureValid();
    return firstName;
  }

  public String getLastName() {
    ensureValid();
    return lastName;
  }

  public List<PhoneNumber> getPhoneNumbers() {
    ensureValid();
    return phoneNumbers;
  }

  /* 
   * Attempt to extract the company name from the given json data. If there is an error, the parser
   * will be set to invalid with appropriate error message, and null will be returned.
   *
   * WARNING: Parser set to invalid if error is encountered.
   */
  private String parseCompanyName(JsonNode data) {

    // Check company name is present.
    if (!data.has(UserJsonConstants.COMPANY_NAME)) {
      setInvalid(missingParameterError(UserJsonConstants.COMPANY_NAME));
      return null;

    } 
    
    String companyName = data.findValue(UserJsonConstants.COMPANY_NAME).asText();

    // Check if company name is already in use.
    if (!handlerService.checkCompanyNameAvailable(companyName)) {
      setInvalid("Handler name [" + companyName + "] is already in use.\n");
      return null;
    }

    return companyName;
  }

  /* WARNING: Parser set to invalid if error is encountered.  */
  private String parseEmailAddress(JsonNode data) {

    // Check email address is present.
    if (!data.has(UserJsonConstants.EMAIL_ADDRESS)) {
      setInvalid(missingParameterError(UserJsonConstants.EMAIL_ADDRESS));
      return null;

    } 
    
    String emailAddress = data.findValue(UserJsonConstants.EMAIL_ADDRESS).asText();

    // Check if email is already in use.
    if (!handlerService.checkEmailAddressAvailable(emailAddress)) {
      setInvalid("Email address [" + emailAddress + "] is already in use.\n");
      return null;
    }

    return emailAddress;
  }

  /* WARNING: Parser set to invalid if error is encountered.  */
  private String parsePassword(JsonNode data) {

    // Check password is present.
    if (!data.has(UserJsonConstants.PASSWORD)) {
      setInvalid(missingParameterError(UserJsonConstants.PASSWORD));
      return null;

    } 
    
    return data.findValue(UserJsonConstants.PASSWORD).asText();
  }

  private static class UserJsonConstants {
    private static final String COMPANY_NAME = "company_name";
    private static final String EMAIL_ADDRESS = "email_address";
    private static final String PASSWORD = "password";

    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";

  }

}