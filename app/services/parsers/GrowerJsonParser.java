package services.parsers;

import com.fasterxml.jackson.databind.JsonNode;

import models.Handler;

import services.HandlerService;

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
  public GrowerJsonParser(JsonNode data) {
    super();

    Handler handler = parseHandler(data);
    if (handler == null) {
      // Parser set to invalid with proper error message.
      return;
    }
    
    String firstName = parseGrowerName(data, JsonConstants.FIRST_NAME);
    if (firstName == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    String lastName = parseGrowerName(data, JsonConstants.LAST_NAME);
    if (lastName == null) {
      // Parser set to invalid with proper error message.
      return;
    }
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

  private static Long parseLong(String numStr) {
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
