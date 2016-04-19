package services.parsers;

import com.fasterxml.jackson.databind.JsonNode;

import models.Handler;

import services.HandlerService;

/**
 * Base class to parse json data. Intended to be extended for specific json data types.
 */
public abstract class JsonParser {
  // Error variables
  private boolean valid;
  private String errorMessage;

  private boolean validitySet = false;

  public JsonParser() {}

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
   * For use in Grower/OfferJsonParser's.
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
    Handler handler = HandlerService.getHandler(handlerId);
    if (handler == null) {
      setInvalid("Handler does not exist with handler id [" + handlerId + "].\n");
      return null;
    }

    return handler;
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
  }
}
