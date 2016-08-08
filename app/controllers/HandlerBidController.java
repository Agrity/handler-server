package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import play.Configuration;

import services.HandlerBidService;
import services.GrowerService;

import models.HandlerBid;
import models.BidResponseResult;
import models.Grower;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import utils.JsonMsgUtils;
import services.messaging.MessageServiceConstants;
import services.messaging.bid.HandlerBidSendGridMessageService;

import play.Logger;

public class HandlerBidController extends Controller {
  
  private final HandlerBidService handlerBidService;
  private final GrowerService growerService;
  private static final HandlerBidSendGridMessageService sendGridService = new HandlerBidSendGridMessageService();

 // private final ObjectMapper jsonMapper;

  @Inject
  public HandlerBidController(HandlerBidService handlerBidService, 
      GrowerService growerService, Configuration config) {
    this.handlerBidService = handlerBidService;
    this.growerService = growerService;
    MessageServiceConstants.EmailFields.setDomain(
      config.getString(
        MessageServiceConstants.DOMAIN_KEY
      )
    );

  //  this.jsonMapper = new ObjectMapper();
  }

  /* Accept whole bid */
  public Result acceptBid(long bidId, long growerId) {
    HandlerBid handlerBid = handlerBidService.getById(bidId);
    if (handlerBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }

    BidResponseResult success =
      handlerBid.growerAcceptBid(growerId, handlerBid.getAlmondPounds());

    if (success.isValid() && handlerBid.getManagementService()
        .equals("services.bid_management.HandlerFCFSService")) {
      /* Send Receipt */
      boolean sendSuccess =
        sendGridService.sendReceipt(handlerBid, growerId, handlerBid.getAlmondPounds());
      if (!sendSuccess) Logger.error("Error sending bid receipts.");
    }
    
    return success.isValid() ? ok(JsonMsgUtils.successfullAccept())
        : internalServerError(JsonMsgUtils.bidNotAccepted(success.getInvalidResponseMessage()));
  }

  /* Partially accept bid */
  public Result acceptPartial(long bidId, long growerId, long pounds) {
    HandlerBid handlerBid = handlerBidService.getById(bidId);
    if (handlerBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }

    BidResponseResult success =
      handlerBid.growerAcceptBid(growerId, pounds);

    if (success.isValid() && handlerBid.getManagementService()
        .equals("services.bid_management.HandlerFCFSService")) {
      /* Send Receipt */
      boolean sendSuccess =
        sendGridService.sendReceipt(handlerBid, growerId, pounds);
      if (!sendSuccess) Logger.error("Error sending bid receipts.");
    }
    
    return success.isValid() ? ok(JsonMsgUtils.successfullAccept())
        : internalServerError(JsonMsgUtils.bidNotAccepted(success.getInvalidResponseMessage()));
  }  

  public Result rejectBid(long bidId, long growerId) {
    HandlerBid handlerBid = handlerBidService.getById(bidId);
    if (handlerBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }

    BidResponseResult success = handlerBid.growerRejectBid(growerId);

    return success.isValid() ? ok(JsonMsgUtils.successfullReject())
        : internalServerError(JsonMsgUtils.bidNotRejected(success.getInvalidResponseMessage()));
  }

  public Result approveBid(long bidId, long growerId) {
    HandlerBid handlerBid = handlerBidService.getById(bidId);

    if (handlerBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }

    BidResponseResult success = 
      handlerBid.approve(growerId);

    
    return success.isValid() ? ok(JsonMsgUtils.successfullApprove())
        : internalServerError(JsonMsgUtils.bidNotApproved(success.getInvalidResponseMessage()));
  }

  public Result disapproveBid(long bidId, long growerId) {
    HandlerBid handlerBid = handlerBidService.getById(bidId);

    if (handlerBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }

    BidResponseResult success = handlerBid.disapprove(growerId);

    return success.isValid() ? ok(JsonMsgUtils.successfullDisapprove())
        : internalServerError(JsonMsgUtils.bidNotDisapproved(success.getInvalidResponseMessage()));
  }

  public Result displayPartialPage(long bidId, long growerId) {
    HandlerBid handlerBid = handlerBidService.getById(bidId);
    if (handlerBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }
    Grower grower = growerService.getById(growerId);
    if (grower == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(growerId));
    }
    return ok(views.html.partialAcceptPage.render(handlerBid, grower, MessageServiceConstants.EmailFields.getDomain()));
  }

}