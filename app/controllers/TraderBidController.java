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
import java.util.Collections;
import services.messaging.MessageServiceConstants;
import services.messaging.bid.BatchSendGridMessageService;

import utils.ResponseHeaders;

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
    ResponseHeaders.addResponseHeaders(response());

    TraderBid traderBid = traderBidService.getById(bidId);
    if (traderBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }

    BidResponseResult success = 
      traderBid.handlerSellerAcceptBid(handlerSellerId, traderBid.getAlmondPounds());

    if (success.isValid() && traderBid.getManagementService()
        .equals("services.bid_management.TraderFCFSService")) {
      /* Send Receipt */
      boolean sendSuccess = 
        sendGridService.sendReceipt(traderBid, handlerSellerId, traderBid.getAlmondPounds());
      if (!sendSuccess) Logger.error("Error sending bid receipts.");
    }
    
    return success.isValid() ? ok(JsonMsgUtils.successfullAccept())
        : internalServerError(JsonMsgUtils.bidNotAccepted(success.getInvalidResponseMessage()));
  }

  /* Partially accept bid */
  public Result acceptPartial(long bidId, long handlerSellerId, long pounds) {
    ResponseHeaders.addResponseHeaders(response());

    TraderBid traderBid = traderBidService.getById(bidId);
    if (traderBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }

    int lbsRemaining = traderBid.getPoundsRemaining();
    Long min =(long) (0.1 * lbsRemaining);

    if(pounds < min) {
      return badRequest(JsonMsgUtils.tooLittleAccepted(min, true));
    }
    if(lbsRemaining - pounds < min && pounds != lbsRemaining) {
      return badRequest(JsonMsgUtils.tooLittleRemaining(lbsRemaining - min, true));
    }

    BidResponseResult success = 
      traderBid.handlerSellerAcceptBid(handlerSellerId, pounds);

    if (success.isValid() && traderBid.getManagementService()
        .equals("services.bid_management.TraderFCFSService")) {
      /* Send Receipt */
      boolean sendSuccess =
        sendGridService.sendReceipt(traderBid, handlerSellerId, pounds);
      if (!sendSuccess) Logger.error("Error sending bid receipts.");
    }
    
    return success.isValid() ? ok(JsonMsgUtils.successfullAccept())
        : internalServerError(JsonMsgUtils.bidNotAccepted(success.getInvalidResponseMessage()));
  }  

  public Result rejectBid(long bidId, long handlerSellerId) {
    ResponseHeaders.addResponseHeaders(response());

    TraderBid traderBid = traderBidService.getById(bidId);
    if (traderBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }

    BidResponseResult success = traderBid.handlerSellerRejectBid(handlerSellerId);

    return success.isValid() ? ok(JsonMsgUtils.successfullReject())
        : internalServerError(JsonMsgUtils.bidNotRejected(success.getInvalidResponseMessage()));
  }

  public Result approveBid(long bidId, long handlerSellerId, long pounds) {
    ResponseHeaders.addResponseHeaders(response());

    TraderBid traderBid = traderBidService.getById(bidId);

    if (traderBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }

    BidResponseResult success = 
      traderBid.approve(handlerSellerId, pounds);

    
    return success.isValid() ? ok(JsonMsgUtils.successfullApprove())
        : internalServerError(JsonMsgUtils.bidNotApproved(success.getInvalidResponseMessage()));
  }  

  public Result disapproveBid(long bidId, long handlerSellerId) {
    ResponseHeaders.addResponseHeaders(response());

    TraderBid traderBid = traderBidService.getById(bidId);

    if (traderBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }

    BidResponseResult success = traderBid.disapprove(handlerSellerId);

    return success.isValid() ? ok(JsonMsgUtils.successfullDisapprove())
        : internalServerError(JsonMsgUtils.bidNotDisapproved(success.getInvalidResponseMessage()));
  }

  public Result displayBatchPage(long batchId, long handlerSellerId) {
    ResponseHeaders.addResponseHeaders(response());

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

  public Result displaySingleBidPage(long bidId, long handlerSellerId) {
    ResponseHeaders.addResponseHeaders(response());

    TraderBid traderBid = traderBidService.getById(bidId);
    if(traderBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }
    HandlerSeller handlerSeller = handlerSellerService.getById(handlerSellerId);
    if (handlerSeller == null) {
      return notFound(JsonMsgUtils.handlerSellerNotFoundMessage(handlerSellerId));
    }
    return ok(views.html.viewBatches.render(
      new Batch(traderBid.getTrader(), Collections.singletonList(traderBid)), 
      handlerSeller, 
      MessageServiceConstants.EmailFields.getDomain()));
  }

  public Result displayMobilePage(long batchId, long handlerSellerId) {
    ResponseHeaders.addResponseHeaders(response());
    
    Batch batch = batchService.getById(batchId);
    if (batch == null) {
      return notFound(JsonMsgUtils.batchNotFoundMessage(batchId));
    }
    HandlerSeller handlerSeller = handlerSellerService.getById(handlerSellerId);
    if (handlerSeller == null) {
      return notFound(JsonMsgUtils.handlerSellerNotFoundMessage(handlerSellerId));
    }
    return ok(views.html.viewBatchesPhone.render(batch, handlerSeller, MessageServiceConstants.EmailFields.getDomain()));
  }
}