package utils;

import models.Grower;
import models.Handler;
import models.Offer;

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

  public static ObjectNode offerNotFoundMessage(Long id) {
    return errorToJson("Offer with id '" + id + "' could not be found\n");
  }

  public static ObjectNode handlerDoesNotOwnGrowerMessage(Handler handler, Grower grower) {
    return errorToJson("Handler " + handler.getCompanyName() + " does not own Grower " + grower.getFullName()
        + ".\n");
  }

  public static ObjectNode handlerDoesNotOwnOfferMessage(Handler handler, Offer offer) {
    return errorToJson("Handler " + handler.getCompanyName() + " does not own Offer " + offer.getId()
        + ".\n");
  }

  public static ObjectNode offerNotAccepted(String invalidResponseMessage) {
    return errorToJson("Offer Could not be Accepted: " + invalidResponseMessage + " \n");
  }

  public static ObjectNode offerNotRejected(String invalidResponseMessage) {
    return errorToJson("Offer Could not be Rejected: " + invalidResponseMessage + " \n");
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

  public static ObjectNode growerInOffer(long growerId, long offerId) {
    return errorToJson("Grower " + Long.toString(growerId) + " still in offer " 
      + Long.toString(offerId));
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

