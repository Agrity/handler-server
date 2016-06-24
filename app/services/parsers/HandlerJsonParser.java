package services.parsers;

import com.fasterxml.jackson.databind.JsonNode;

import models.Handler;

/**
 * Class to parse json data to create new Handler.
 *
 * Expected Json Structure:
 *  {
 *    COMPANY_NAME: ...
 *  }
 */
public class HandlerJsonParser extends BaseParser {
  // Parsed variables
  private final String companyName;

  public HandlerJsonParser(JsonNode data) {
    super();

    companyName = parseCompanyName(data);
    if (companyName == null) {
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
    return new Handler(getCompanyName());
  }

  // WARNING: Should only be called after isValid() has been checked to be true
  public String getCompanyName() {
    ensureValid();

    return companyName;
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
    if (!checkHandlerCompanyNameAvailable(companyName)) {
      setInvalid("Handler name [" + companyName + "] is already in use.\n");
      return null;
    }

    return companyName;
  }

  private boolean checkHandlerCompanyNameAvailable(String companyName) {
    return handlerService.getByCompanyName(companyName) == null;
  }

  private static class HandlerJsonConstants {
    private static final String COMPANY_NAME = "company_name";
  }
}
