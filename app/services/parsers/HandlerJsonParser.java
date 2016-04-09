package services.parsers;

import com.fasterxml.jackson.databind.JsonNode;

import services.HandlerService;

/**
 * Class to parse json data to create new Handler.
 *
 * Expected Json Structure: { COMPANY_NAME: ... }
 */
public class HandlerJsonParser extends JsonParser {
  // Parsed variables
  private final String companyName;

  public HandlerJsonParser(JsonNode data) {
    super();

    companyName = data.findValue(JsonConstants.COMPANY_NAME).asText();
    if (!checkValidCompanyName(companyName)) {
      // Parser set to invalid with proper error message.
      return;
    }

    // Valid json data recieved and processed.
    setValid();
  }

  // WARNING: Should only be called after isValud() has been checked to be true
  public String getCompanyName() {
    ensureValid();

    return companyName;
  }

  private boolean checkValidCompanyName(String companyName) {
    // Check companyName is present.
    if (companyName == null) {
      setInvalid(missingParameterError(JsonConstants.COMPANY_NAME));
      return false;

      // Check if company name is already in use.
    } else if (!checkHandlerCompanyNameAvailable(companyName)) {
      setInvalid("Handler name [" + companyName + "] is already in use.\n");
      return false;
    }

    return true;
  }

  private boolean checkHandlerCompanyNameAvailable(String companyName) {
    return HandlerService.getHandler(companyName) == null;
  }

  private static class JsonConstants {
    private static final String COMPANY_NAME = "company_name";
  }
}
