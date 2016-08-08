package services.bid_management;

import java.time.Duration;

import java.util.*;

import models.HandlerBid;
import models.BaseBid.BidStatus;
import models.BidResponseResult;
import models.Grower;

import akka.actor.Cancellable;
import scala.concurrent.duration.FiniteDuration;
import java.util.concurrent.TimeUnit;

import services.impl.EbeanGrowerService;
import services.GrowerService;

import play.libs.Akka;

public class HandlerSTFCService implements HandlerBidManagementService {


  private final HandlerBid handlerBid;
  
  private Cancellable cancellable;
  private long poundsRemaining;
  private List<Long> growerIdsRemaining;
  GrowerService growerService = new EbeanGrowerService();

  public HandlerSTFCService(HandlerBid handlerBid, Duration timeAllowed) {
    this.handlerBid = handlerBid;
    this.poundsRemaining = handlerBid.getAlmondPounds();

    growerIdsRemaining = getGrowerIDList();
    HandlerBidManagementService.bidToManageService.put(handlerBid, this);

    cancellable = Akka.system().scheduler()
        .scheduleOnce(FiniteDuration.create(timeAllowed.toMinutes(), TimeUnit.MINUTES), new Runnable() {
          @Override
          public void run() {
            if (poundsRemaining == handlerBid.getAlmondPounds()) {
              handlerBid.closeBid(BidStatus.REJECTED);
            } else {
              handlerBid.closeBid(BidStatus.PARTIAL);
            }
          }
        }, Akka.system().dispatcher());
  }

  private List<Long> getGrowerIDList() {
    List<Long> growers = new ArrayList<>();
    for (Grower grower : handlerBid.getAllGrowers()) {
      growers.add(grower.getId());
    }
    return growers;
  }

  @Override
  public void addGrowers(List<Long> growerIds) {
    growerIdsRemaining.addAll(growerIds);
  }

  @Override
  public BidResponseResult accept(long pounds, long growerId) {
    if (!checkPoundsRemaining(pounds)) {
      return BidResponseResult.getInvalidResult("Only " + poundsRemaining
        + " pounds remain. Can not accept bid for " + pounds + " pounds.");
    }

    growerIdsRemaining.remove((Long) growerId);

    if (growerIdsRemaining.isEmpty()) {
      if (poundsRemaining == handlerBid.getAlmondPounds()) {
        handlerBid.closeBid(BidStatus.REJECTED); 
      } else {
        handlerBid.closeBid(BidStatus.PARTIAL); 
      }
      cancellable.cancel();
    }

    return BidResponseResult.getValidResult();
  }

  @Override
  public BidResponseResult reject(long growerId) {
    growerIdsRemaining.remove((Long) growerId);

    if (growerIdsRemaining.isEmpty()) {
      if (poundsRemaining == handlerBid.getAlmondPounds()) {
        handlerBid.closeBid(BidStatus.REJECTED); 
      } else {
        handlerBid.closeBid(BidStatus.PARTIAL); 
      }
      cancellable.cancel();
    }

    return BidResponseResult.getValidResult();
  }

  @Override
  public void close() {
    cancellable.cancel();
  }

  @Override
  public BidResponseResult approve(long pounds, long growerId) {
    if (!checkPoundsRemaining(pounds)) {
      return BidResponseResult.getInvalidResult("Only " + poundsRemaining
        + " pounds remain. Can not approve bid for " + pounds + " pounds.");
    }

    poundsRemaining -= pounds;

    if (poundsRemaining == 0) {
      handlerBid.closeBid(BidStatus.ACCEPTED); 
      cancellable.cancel();
    }

    return BidResponseResult.getValidResult();
  }

  @Override
  public BidResponseResult disapprove(long growerrId) {
    return BidResponseResult.getValidResult();
  }

  private boolean checkPoundsRemaining(long pounds) {
    if (pounds > poundsRemaining) {
      return false;
    } 
    return true;
  }
}