package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import services.TraderBidService;
import services.HandlerSellerService;

import models.TraderBid;
import models.BidResponseResult;
import models.HandlerSeller;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import utils.JsonMsgUtils;
import services.messaging.MessageServiceConstants;

/* TODO: Write HTML for TraderBid partial accept page */

public class TraderBidController extends Controller {
  
  private final TraderBidService traderBidService;
  private final HandlerSellerService handlerSellerService;


  @Inject
  public TraderBidController(TraderBidService traderBidService, 
      HandlerSellerService handlerSellerService) {
    this.traderBidService = traderBidService;
    this.handlerSellerService = handlerSellerService;
  }

  /* Accept whole bid */
  public Result acceptBid(long bidId, long handlerSellerId) {
    TraderBid traderBid = traderBidService.getById(bidId);
    if (traderBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }

    BidResponseResult success = 
      traderBid.handlerSellerAcceptBid(handlerSellerId, traderBid.getAlmondPounds());
    
    return success.isValid() ? ok(JsonMsgUtils.successfullAccept())
        : internalServerError(JsonMsgUtils.bidNotAccepted(success.getInvalidResponseMessage()));
  }

  /* Partially accept bid */
  public Result acceptPartial(long bidId, long handlerSellerId, long pounds) {
    TraderBid traderBid = traderBidService.getById(bidId);
    if (traderBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }

    // TODO Change to actual pounds accepted once implemented.
    BidResponseResult success = 
      traderBid.handlerSellerAcceptBid(handlerSellerId, pounds);
    
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

  public Result displayPartialPage(long bidId, long handlerSellerId) {
    TraderBid traderBid = traderBidService.getById(bidId);
    if (traderBid == null) {
      return notFound(JsonMsgUtils.bidNotFoundMessage(bidId));
    }
    HandlerSeller handlerSeller = handlerSellerService.getById(handlerSellerId);
    if (handlerSeller == null) {
      return notFound(JsonMsgUtils.handlerSellerNotFoundMessage(handlerSellerId));
    }
    return ok("");
    //return ok(views.html.partialAcceptPage.render(traderBid, handlerSeller, MessageServiceConstants.EmailFields.getDomain()));
  }

}