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

public class TraderFCFSService implements TraderBidManagementService {


  private final TraderBid traderBid;
  
  private Cancellable cancellable;
  private long poundsRemaining;
  private List<Long> handlerSellerIdsRemaining;
  HandlerSellerService handlerSellerService = new EbeanHandlerSellerService();

  public TraderFCFSService(TraderBid traderBid, Duration timeAllowed) {
    this.traderBid = traderBid;
    this.poundsRemaining = traderBid.getAlmondPounds();

    handlerSellerIdsRemaining = getHandlerSellerIDList();
    TraderBidManagementService.bidToManageService.put(traderBid, this);

    cancellable = Akka.system().scheduler()
        .scheduleOnce(FiniteDuration.create(timeAllowed.toMinutes(), TimeUnit.MINUTES), new Runnable() {
          @Override
          public void run() {
            if(poundsRemaining == traderBid.getAlmondPounds()) {
              traderBid.closeBid(BidStatus.REJECTED);
            } else {
              traderBid.closeBid(BidStatus.PARTIAL);
            }
          }
        }, Akka.system().dispatcher());
  }

  private List<Long> getHandlerSellerIDList() {
    List<Long> handlerSellers = new ArrayList<>();
    for(HandlerSeller hs : traderBid.getAllHandlerSellers()) {
      handlerSellers.add(hs.getId());
    }
    return handlerSellers;
  }

  @Override
  public BidResponseResult accept(long pounds, long handlerSellerId) {
    
    if (!subtractFromPoundsRemaining(pounds)) {
      return BidResponseResult.getInvalidResult("Only " + poundsRemaining + " pounds remain. Can not accept bid for " + pounds + " pounds.");
    }

    handlerSellerIdsRemaining.remove((Long) handlerSellerId);

    if (poundsRemaining == 0) {
      cancellable.cancel();
      traderBid.closeBid(BidStatus.ACCEPTED);
    } else if(handlerSellerIdsRemaining.isEmpty()) {
      traderBid.closeBid(BidStatus.PARTIAL); 
      cancellable.cancel(); 
    }

    return BidResponseResult.getValidResult();
  }

  @Override
  public BidResponseResult reject(long handlerSellerId) {
    handlerSellerIdsRemaining.remove((Long) handlerSellerId);
    if(handlerSellerIdsRemaining.isEmpty()) {
      cancellable.cancel();
      if(poundsRemaining == traderBid.getAlmondPounds()) {
        traderBid.closeBid(BidStatus.REJECTED);
      } else {
        traderBid.closeBid(BidStatus.PARTIAL);
      }
    }
    return BidResponseResult.getValidResult();
  }

  @Override
  public void close() {
    cancellable.cancel();
  }

  @Override
  public BidResponseResult approve(long pounds, long handlerSellerId) {
    return BidResponseResult.getInvalidResult("Cannot approve a response in FCFS.");
  }

  @Override
  public BidResponseResult disapprove(long handlerSellerId) {
    return BidResponseResult.getInvalidResult("Cannot approve a response in FCFS.");
  }

  public Boolean subtractFromPoundsRemaining(long pounds) {
    if (pounds > poundsRemaining) {
      return false;
    } 
    else {
      poundsRemaining -= pounds;
      return true;
    }
  }
}