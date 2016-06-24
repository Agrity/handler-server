package services.parsers;

import com.fasterxml.jackson.databind.JsonNode;

import models.Handler;

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
public class HandlerJsonParser extends JsonParser {
  // Parsed variables
  private String companyName;
  private String emailAddress;
  private String password;

  public HandlerJsonParser(JsonNode data) {
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

    // Valid json data received and processed.
    setValid();
  }

  public Handler formHandler() {
    if (!isValid()) {
      throw new RuntimeException("Attempted to create Handler from invalid parser.\n");
    }
    return new Handler(
        getCompanyName(),
        getEmailAddress(),
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

  /* 
   * Attempt to extract the company name from the given json data. If there is an error, the parser
   * will be set to invalid with appropriate error message, and null will be returned.
   *
   * WARNING: Parser set to invalid if error is encountered.
   */
  private String parseCompanyName(JsonNode data) {

    // Check company name is present.
    if (!data.has(HandlerJsonConstants.COMPANY_NAME)) {
      setInvalid(missingParameterError(HandlerJsonConstants.COMPANY_NAME));
      return null;

    } 
    
    String companyName = data.findValue(HandlerJsonConstants.COMPANY_NAME).asText();

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
    if (!data.has(HandlerJsonConstants.EMAIL_ADDRESS)) {
      setInvalid(missingParameterError(HandlerJsonConstants.EMAIL_ADDRESS));
      return null;

    } 
    
    String emailAddress = data.findValue(HandlerJsonConstants.EMAIL_ADDRESS).asText();

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
    if (!data.has(HandlerJsonConstants.PASSWORD)) {
      setInvalid(missingParameterError(HandlerJsonConstants.PASSWORD));
      return null;

    } 
    
    return data.findValue(HandlerJsonConstants.PASSWORD).asText();
  }

  private static class HandlerJsonConstants {
    private static final String COMPANY_NAME = "company_name";
    private static final String EMAIL_ADDRESS = "email_address";
    private static final String PASSWORD = "password";
  }
}
