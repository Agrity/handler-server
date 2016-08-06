package utils;

import models.Grower;
import models.Handler;
import models.Trader;
import models.HandlerSeller;
import models.HandlerBid;
import models.TraderBid;
import models.Batch;

import play.libs.Json;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonMsgUtils {

  /* ==================== Json converters for Error Messages =======================*/
  
  public static ObjectNode handlerNotFoundMessage(Long id) {
    return errorToJson("Handler with id '" + id + "' could not be found\n");
  } 

  public static ObjectNode traderNotFoundMessage(Long id) {
    return errorToJson("Trader with id '" + id + "' could not be found\n");
  }

  public static ObjectNode growerNotFoundMessage(Long id) {
    return errorToJson("Grower with id '" + id + "' could not be found\n");
  }

  public static ObjectNode handlerSellerNotFoundMessage(Long id) {
    return errorToJson("Handler seller with id '" + id + "' could not be found\n");
  }

  public static ObjectNode bidNotFoundMessage(Long id) {
    return errorToJson("Bid with id '" + id + "' could not be found\n");
  }

  public static ObjectNode batchNotFoundMessage(Long id) {
    return errorToJson("Batch with id '" + id + "' could not be found\n");
  }

  public static ObjectNode handlerDoesNotOwnGrowerMessage(Handler handler, Grower grower) {
    return errorToJson("Handler " + handler.getCompanyName() + " does not own Grower " + grower.getFullName()
        + ".\n");
  }

  public static ObjectNode traderDoesNotOwnHandlerMessage(Trader trader, HandlerSeller handlerSeller) {
    return errorToJson("Trader " + trader.getCompanyName() + " does not own handler seller " 
      + handlerSeller.getFullName()+ ".\n");
  }

  public static ObjectNode handlerDoesNotOwnBidMessage(Handler handler, HandlerBid handlerBid) {
    return errorToJson("Handler " + handler.getCompanyName() + " does not own bid " + handlerBid.getId()
        + ".\n");
  }

  public static ObjectNode traderDoesNotOwnBidMessage(Trader trader, TraderBid traderBid) {
    return errorToJson("Trader " + trader.getCompanyName() + " does not own bid " + traderBid.getId()
        + ".\n");
  }

  public static ObjectNode traderDoesNotOwnBatchMessage(Trader trader, Batch batch) {
    return errorToJson("Trader " + trader.getCompanyName() + " does not own batch " + batch.getId()
        + ".\n");
  }

  public static ObjectNode bidNotAccepted(String invalidResponseMessage) {
    return errorToJson("Bid Could not be Accepted: " + invalidResponseMessage + " \n");
  }

  public static ObjectNode bidNotRejected(String invalidResponseMessage) {
    return errorToJson("Bid Could not be Rejected: " + invalidResponseMessage + " \n");
  }

  public static ObjectNode bidNotClosed(String invalidResponseMessage) {
    return errorToJson("Bid Could not be closed: " + invalidResponseMessage + " \n");
  }

  public static ObjectNode cantAddSeller(Long id) {
    return errorToJson("Bid " + id + " is already closed, seller could not be added.");
  }

  public static ObjectNode callNotRequested(String invalidResponseMessage) {
    return errorToJson("Call Could not be Requested: " + invalidResponseMessage + " \n");
  }

  public static ObjectNode emailsNotSent() {
    return errorToJson("Some or all of the emails were unable to be sent.\n");
  }

  public static ObjectNode messagesNotSent() {
    return errorToJson("Some or all of the emails or text messages were unable to be sent.\n");
  }

  public static ObjectNode caughtException(String err) {
    return errorToJson(err);
  }

  public static ObjectNode expectingData() {
    return errorToJson("Expecting Some Data.\n");
  }

  public static ObjectNode expectingArray() {
    return errorToJson("Expecting JsonNode as Array. \n");
  }

  public static ObjectNode growerInBid(long growerId, long bidId) {
    return errorToJson("Grower " + Long.toString(growerId) + " still in bid " 
      + Long.toString(bidId));
  }

  public static ObjectNode handlerSellerInBid(long handlerSellerId, long bidId) {
    return errorToJson("HandlerSeller " + Long.toString(handlerSellerId) + " still in bid " 
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

  public static ObjectNode bidClosed(long bidId) {
    return validToJson("Bid " + bidId + " closed.");
  }

  public static ObjectNode growerDeleted(long growerId) {
    return validToJson("Grower " + Long.toString(growerId) +" successfully deleted");
  }

  public static ObjectNode handlerSellerDeleted(long handlerSellerId) {
    return validToJson("HandlerSeller " + Long.toString(handlerSellerId) +" successfully deleted");
  }

  private static ObjectNode validToJson(String msg) {
    ObjectNode result = Json.newObject();
    result.put("valid", msg);  
    return result;
  }

}

