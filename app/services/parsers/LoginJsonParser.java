package services.parsers;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Class to parse json data to create new Handler.
 *
 * Expected Json Structure:
 *  {
 *    EMAIL_ADDRESS : ...,
 *    PASSWORD : ....
 *  }
 */
public class LoginJsonParser extends JsonParser {
  // Parsed variables
  private String emailAddress;
  private String password;

  public LoginJsonParser(JsonNode data) {
    super();

    emailAddress = parseEmailAddress(data);
    if (emailAddress == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    password = parsePassword(data);
    if (emailAddress == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    // Valid json data received and processed.
    setValid();
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
   * WARNING: Parser set to invalid if error is encountered.
   */
  private String parseEmailAddress(JsonNode data) {

    // Check email address is present.
    if (!data.has(LoginJsonConstants.EMAIL_ADDRESS)) {
      setInvalid(missingParameterError(LoginJsonConstants.EMAIL_ADDRESS));
      return null;
    } 
    
    return data.findValue(LoginJsonConstants.EMAIL_ADDRESS).asText();
  }

  /* 
   * WARNING: Parser set to invalid if error is encountered.
   */
  private String parsePassword(JsonNode data) {

    // Check email address is present.
    if (!data.has(LoginJsonConstants.PASSWORD)) {
      setInvalid(missingParameterError(LoginJsonConstants.PASSWORD));
      return null;
    } 
    
    return data.findValue(LoginJsonConstants.PASSWORD).asText();
  }

  private static class LoginJsonConstants {
    private static final String EMAIL_ADDRESS = "email_address";
    private static final String PASSWORD = "password";
  }
}
