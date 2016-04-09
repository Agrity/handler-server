package services.parsers;

import com.fasterxml.jackson.databind.JsonNode;

import services.HandlerService;

/**
 * Class to parse json data to create new Handler.
 *
 * Expected Json Structure: { COMPANY_NAME: ... }
 */
public class HandlerJsonParser {
  // Error variables
  private boolean valid;
  private String errorMessage;

  // Parsed variables
  private final String companyName;

  // TODO check if company name already exists.
  public HandlerJsonParser(JsonNode data) {
    companyName = data.findValue(JsonConstants.COMPANY_NAME).asText();
    if (!checkValidCompanyName(companyName)) {
      return;
    }

    // Check that company name parameter is present.

    // Json data is valid for new Handler.
    valid = true;
    errorMessage = null;
  }

  public boolean isValid() {
    return valid;
  }

  // WARNING: Should only be called after isValud() has been checked to be false
  public String getErrorMessage() {
    ensureInvalid();

    return errorMessage;
  }

  // WARNING: Should only be called after isValud() has been checked to be true
  public String getCompanyName() {
    ensureValid();

    return companyName;
  }

  private boolean checkValidCompanyName(String companyName) {
    if (companyName == null) {
      valid = false;
      errorMessage = missingParameterError(JsonConstants.COMPANY_NAME);
      return false;

    // Check if company name is already in use.
    } else if (!checkHandlerCompanyNameAvailable(companyName)) {
      valid = false;
      errorMessage = "Handler name [" + companyName + "] is already in use.\n";
      return false;
    }

    return true;
  }

  private void ensureValid() {
    if (!isValid()) {
      throw new RuntimeException("Parser Invalid: valid parser expected.");
    }
  }

  private void ensureInvalid() {
    if (isValid()) {
      throw new RuntimeException("Parser Valid: invalid parser expected.");
    }
  }

  private boolean checkHandlerCompanyNameAvailable(String companyName) {
    return HandlerService.getHandler(companyName) == null;
  }


  private static String missingParameterError(String paramaterName) {
    return "Missing paramater [ " + paramaterName + " ]\n";
  }


  private static class JsonConstants {
    private static final String COMPANY_NAME = "company_name";
  }
}
