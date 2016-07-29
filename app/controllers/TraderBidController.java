package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import play.Configuration;

import services.TraderBidService;
import services.HandlerSellerService;
import services.BatchService;

import models.TraderBid;
import models.BidResponseResult;
import models.HandlerSeller;
import models.Batch;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import utils.JsonMsgUtils;
import services.messaging.MessageServiceConstants;
import services.messaging.bid.BatchSendGridMessageService;

import play.Logger;

public class TraderBidController extends Controller {
  
  private final TraderBidService traderBidService;
  private final HandlerSellerService handlerSellerService;
  private final BatchService batchService;
  private static final BatchSendGridMessageService sendGridService = new BatchSendGridMessageService();


  @Inject
  public TraderBidController(TraderBidService traderBidService, 
      HandlerSellerService handlerSellerService,
      BatchService batchService,
      Configuration config) {
    this.traderBidService = traderBidService;
    this.handlerSellerService = handlerSellerService;
    this.batchService = batchService;
    MessageServiceConstants.EmailFields.setDomain(
      config.getString(
        MessageServiceConstants.DOMAIN_KEY
      )
    );
  }

  /* Accept whole bid */
  public Result acceptBid(long bidId, long handlerSellerId) {
    TraderBid traderBid = traderBidService.getById(bidId);
    if (traderBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }

    BidResponseResult success = 
      traderBid.handlerSellerAcceptBid(handlerSellerId, traderBid.getAlmondPounds());

    if (success.isValid()) {
      boolean sendSuccess = sendGridService.sendReceipt(traderBid, handlerSellerId, traderBid.getAlmondPounds());
      if (!sendSuccess) Logger.error("Error sending bid receipts.");
    }
    
    return success.isValid() ? ok(JsonMsgUtils.successfullAccept())
        : internalServerError(JsonMsgUtils.bidNotAccepted(success.getInvalidResponseMessage()));
  }

  /* Partially accept bid */
  public Result acceptPartial(long bidId, long handlerSellerId, long pounds) {
    TraderBid traderBid = traderBidService.getById(bidId);
    if (traderBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }

    BidResponseResult success = 
      traderBid.handlerSellerAcceptBid(handlerSellerId, pounds);

    if (success.isValid()) {
      boolean sendSuccess = sendGridService.sendReceipt(traderBid, handlerSellerId, pounds);
      if (!sendSuccess) Logger.error("Error sending bid receipts.");
    }
    
    return success.isValid() ? ok(JsonMsgUtils.successfullAccept())
        : internalServerError(JsonMsgUtils.bidNotAccepted(success.getInvalidResponseMessage()));
  }  

  public Result rejectBid(long bidId, long handlerSellerId) {
    TraderBid traderBid = traderBidService.getById(bidId);
    if (traderBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }

    BidResponseResult success = traderBid.handlerSellerRejectBid(handlerSellerId);

    return success.isValid() ? ok(JsonMsgUtils.successfullReject())
        : internalServerError(JsonMsgUtils.bidNotRejected(success.getInvalidResponseMessage()));
  }

  public Result displayBatchPage(long batchId, long handlerSellerId) {
    Batch batch = batchService.getById(batchId);
    if (batch == null) {
      return notFound(JsonMsgUtils.batchNotFoundMessage(batchId));
    }
    HandlerSeller handlerSeller = handlerSellerService.getById(handlerSellerId);
    if (handlerSeller == null) {
      return notFound(JsonMsgUtils.handlerSellerNotFoundMessage(handlerSellerId));
    }
    return ok(views.html.viewBatches.render(batch, handlerSeller, MessageServiceConstants.EmailFields.getDomain()));
  }

}