package controllers;

import models.Grower;
import models.Handler;

import play.libs.Json;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

public class JsonMsgUtils {
  /* ==================== Json converters for Error Messages =======================*/
  public static ObjectNode handlerNotFoundMessage(Long id) {
    return errorToJson("Handler with id '" + id + "' could not be found\n");
  } 

  public static ObjectNode growerNotFoundMessage(Long id) {
    return errorToJson("Grower with id '" + id + "' could not be found\n");
  }

  public static ObjectNode offerNotFoundMessage(Long id) {
    return errorToJson("Offer with id '" + id + "' could not be found\n");
  }

  public static ObjectNode handlerDoesNotOwnGrowerMessage(Handler handler, Grower grower) {
    return errorToJson("Handler " + handler.getCompanyName() + " does not own Grower " + grower.getFullName()
        + ".\n");
  }

  public static ObjectNode offerNotAccepted() {
    return errorToJson("Internal Error: Offer could not be accepted.\n");
  }

  public static ObjectNode offerNotRejected() {
    return errorToJson("Internal Error: Offer could not be rejected.\n");
  }

  public static ObjectNode callNotRequested() {
    return errorToJson("Internal Error: Call could not be requested.\n");
  }

  public static ObjectNode emailsNotSent() {
    return errorToJson("Some or all of the emails were unable to be sent.\n");
  }

  public static ObjectNode caughtException(String err) {
    return errorToJson(err);
  }

  public static ObjectNode expectingData() {
    return errorToJson("Expecting Some Data.\n");
  }

  private static ObjectNode errorToJson(String msg) {
    ObjectNode result = Json.newObject();
    result.put("error", msg);  
    return result;
  }
  

  /* ==================== Json converters for successes =======================*/
  public static ObjectNode successfullAccept() {
    return validToJson("Successfully Accepted Offer.");
  }

  public static ObjectNode successfullReject() {
    return validToJson("Successfully Rejected Offer.");
  }

  public static ObjectNode successfullCallRequest() {
    return validToJson("Successfully Requested Call.");
  }

  public static ObjectNode successfullEmail() {
    return validToJson("Emails sent successfully!");
  }

  private static ObjectNode validToJson(String msg) {
    ObjectNode result = Json.newObject();
    result.put("valid", msg);  
    return result;
  }

}

