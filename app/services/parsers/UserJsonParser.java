package services.parsers;

import com.fasterxml.jackson.databind.JsonNode;

import services.PhoneMessageService;

import models.User;
import models.Trader;
import models.EmailAddress;
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
  private EmailAddress emailAddress;
  private String password;

  private String firstName;
  private String lastName;

  private PhoneNumber phoneNumber;

  public UserJsonParser(JsonNode data) {
    super();

    companyName = parseCompanyName(data);
    if (companyName == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    emailAddress = parserUserEmailAddress(data);
    if (emailAddress == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    password = parsePassword(data);
    if (password == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    firstName = parseName(data, JsonConstants.FIRST_NAME);
    if (firstName == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    lastName = parseName(data, JsonConstants.LAST_NAME);
    if (lastName == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    phoneNumber = parsePhoneNumber(data);
    if (phoneNumber == null) {
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
  public EmailAddress getEmailAddress() {
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

  public PhoneNumber getPhoneNumbers() {
    ensureValid();
    return phoneNumber;
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
    private static final String PASSWORD = "password";
  }

}
