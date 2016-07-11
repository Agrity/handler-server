package utils;

import models.Grower;
import models.Handler;
import models.HandlerBid;

import play.libs.Json;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonMsgUtils {

  /* ==================== Json converters for Error Messages =======================*/
  public static ObjectNode handlerNotFoundMessage(Long id) {
    return errorToJson("Handler with id '" + id + "' could not be found\n");
  } 

  public static ObjectNode growerNotFoundMessage(Long id) {
    return errorToJson("Grower with id '" + id + "' could not be found\n");
  }

  public static ObjectNode bidNotFoundMessage(Long id) {
    return errorToJson("Bid with id '" + id + "' could not be found\n");
  }

  public static ObjectNode handlerDoesNotOwnGrowerMessage(Handler handler, Grower grower) {
    return errorToJson("Handler " + handler.getCompanyName() + " does not own Grower " + grower.getFullName()
        + ".\n");
  }

  public static ObjectNode handlerDoesNotOwnBidMessage(Handler handler, HandlerBid handlerBid) {
    return errorToJson("Handler " + handler.getCompanyName() + " does not own bid " + handlerBid.getId()
        + ".\n");
  }

  public static ObjectNode bidNotAccepted(String invalidResponseMessage) {
    return errorToJson("Bid Could not be Accepted: " + invalidResponseMessage + " \n");
  }

  public static ObjectNode bidNotRejected(String invalidResponseMessage) {
    return errorToJson("Bod Could not be Rejected: " + invalidResponseMessage + " \n");
  }

  public static ObjectNode callNotRequested(String invalidResponseMessage) {
    return errorToJson("Call Could not be Requested: " + invalidResponseMessage + " \n");
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

  public static ObjectNode growerInBid(long growerId, long bidId) {
    return errorToJson("Grower " + Long.toString(growerId) + " still in bid " 
      + Long.toString(bidId));
  }

  private static ObjectNode errorToJson(String msg) {
    ObjectNode result = Json.newObject();
    result.put("error", msg);  
    return result;
  }
  

  /* ==================== Json converters for successes =======================*/
  public static ObjectNode successfullAccept() {
    return validToJson("Successfully Accepted Bid.");
  }

  public static ObjectNode successfullReject() {
    return validToJson("Successfully Rejected Bid.");
  }

  public static ObjectNode successfullCallRequest() {
    return validToJson("Successfully Requested Call.");
  }

  public static ObjectNode successfullEmail() {
    return validToJson("Emails sent successfully!");
  }

  public static ObjectNode bidDeleted(long bidId) {
    return validToJson("Bid " + bidId + " deleted.");
  }

  public static ObjectNode growerDeleted(long growerId) {
    return validToJson("Grower " + Long.toString(growerId) +" successfully deleted");
  }

  private static ObjectNode validToJson(String msg) {
    ObjectNode result = Json.newObject();
    result.put("valid", msg);  
    return result;
  }

}

