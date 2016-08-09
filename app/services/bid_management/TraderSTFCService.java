package services.bid_management;

import java.time.Duration;

import java.util.*;

import models.TraderBid;
import models.BaseBid.BidStatus;
import models.BidResponseResult;
import models.HandlerSeller;

import akka.actor.Cancellable;
import scala.concurrent.duration.FiniteDuration;
import java.util.concurrent.TimeUnit;

import services.impl.EbeanHandlerSellerService;
import services.HandlerSellerService;

import play.libs.Akka;

public class TraderSTFCService implements TraderBidManagementService {


  private final TraderBid traderBid;
  
  private Cancellable cancellable;
  private long poundsRemaining;
  private List<Long> handlerSellerIdsRemaining;
  HandlerSellerService handlerSellerService = new EbeanHandlerSellerService();

  public TraderSTFCService(TraderBid traderBid, Duration timeAllowed) {
    this.traderBid = traderBid;
    this.poundsRemaining = traderBid.getAlmondPounds();

    handlerSellerIdsRemaining = getHandlerSellerIDList();
    TraderBidManagementService.bidToManageService.put(traderBid, this);

    cancellable = Akka.system().scheduler()
        .scheduleOnce(FiniteDuration.create(timeAllowed.toMinutes(), TimeUnit.MINUTES), new Runnable() {
          @Override
          public void run() {
            if (poundsRemaining == traderBid.getAlmondPounds()) {
              traderBid.closeBid(BidStatus.REJECTED);
            } else {
              traderBid.closeBid(BidStatus.PARTIAL);
            }
          }
        }, Akka.system().dispatcher());
  }

  private List<Long> getHandlerSellerIDList() {
    List<Long> handlerSellers = new ArrayList<>();
    for (HandlerSeller hs : traderBid.getAllHandlerSellers()) {
      handlerSellers.add(hs.getId());
    }
    return handlerSellers;
  }

  @Override
  public void addHandlerSellers(List<Long> handlerSellerIds) {
    handlerSellerIdsRemaining.addAll(handlerSellerIds);
  }

  @Override
  public BidResponseResult accept(long pounds, long handlerSellerId) {
    if (!checkPoundsRemaining(pounds)) {
      return BidResponseResult.getInvalidResult("Only " + poundsRemaining
        + " pounds remain. Can not accept bid for " + pounds + " pounds.");
    }

    handlerSellerIdsRemaining.remove((Long) handlerSellerId);

    return BidResponseResult.getValidResult();
  }

  @Override
  public BidResponseResult reject(long handlerSellerId) {
    handlerSellerIdsRemaining.remove((Long) handlerSellerId);
    return BidResponseResult.getValidResult();
  }

  @Override
  public void close() {
    cancellable.cancel();
  }

  @Override
  public BidResponseResult approve(long pounds, long handlerSellerId) {
    if (!checkPoundsRemaining(pounds)) {
      return BidResponseResult.getInvalidResult("Only " + poundsRemaining
        + " pounds remain. Can not approve bid for " + pounds + " pounds.");
    }

    poundsRemaining -= pounds;

    if (poundsRemaining == 0) {
      traderBid.closeBid(BidStatus.ACCEPTED); 
      cancellable.cancel();
    }

    return BidResponseResult.getValidResult();
  }

  @Override
  public BidResponseResult disapprove(long handlerSellerId) {
    return BidResponseResult.getValidResult();
  }

  private boolean checkPoundsRemaining(long pounds) {
    if (pounds > poundsRemaining) {
      return false;
    } 
    return true;
  }
}