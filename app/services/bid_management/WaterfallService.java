package services.bid_management;

import java.util.*;

import models.HandlerBid;
import models.BaseBid.BidStatus;
import models.BidResponseResult;
import models.Grower;
import java.time.Duration;

import akka.actor.Cancellable;
import scala.concurrent.duration.FiniteDuration;
import java.util.concurrent.TimeUnit;

import services.messaging.bid.BidSendGridMessageService;
import services.messaging.bid.BidSMSMessageService;

import play.Logger;
import play.libs.Akka;

public class WaterfallService implements BidManagementService {

  private final HandlerBid handlerBid;
  private final Duration delay;
  private Cancellable cancellable;
  private long poundsRemaining;
  private List<Grower> growersInLine;

  BidSendGridMessageService emailService = new BidSendGridMessageService();
  BidSMSMessageService smsService = new BidSMSMessageService();

  public WaterfallService(HandlerBid handlerBid, Duration delay) {
    this.handlerBid = handlerBid;
    this.delay = delay;
    this.growersInLine = new ArrayList<Grower>(handlerBid.getAllGrowers());
    this.poundsRemaining = handlerBid.getAlmondPounds();

    BidManagementService.bidToManageService.put(handlerBid, this);

    cancellable = scheduleTimer();
  }

  private Cancellable scheduleTimer() {
    emailService.send(handlerBid, growersInLine.get(0));
    smsService.send(handlerBid, growersInLine.get(0));

    return Akka.system().scheduler().scheduleOnce(FiniteDuration.create(delay.toMillis(), TimeUnit.MILLISECONDS),
        new Runnable() {
          @Override
          public void run() {
            moveToNext();
            
          }
        }, Akka.system().dispatcher());
  }

  private void moveToNext() {
    emailService.sendClosed(handlerBid, growersInLine.get(0));
    smsService.sendClosed(handlerBid, growersInLine.get(0));
    if (growersInLine.size() != 0) {
      cancellable.cancel();
      growersInLine.remove(0);
      
      if (growersInLine.size() != 0) {
        emailService.send(handlerBid, growersInLine.get(0));
        smsService.send(handlerBid,growersInLine.get(0));
        cancellable = scheduleTimer();
      } else {
        if(poundsRemaining == handlerBid.getAlmondPounds()) {
          handlerBid.closeBid(BidStatus.REJECTED);
        } else {
          handlerBid.closeBid(BidStatus.PARTIAL);
        }
      }    
    }
    else {
      Logger.error("growersInLine is trying to remove a grower despite being empty in WaterfallService of bidId: " + handlerBid.getId());
    }
  }

  @Override
  public BidResponseResult accept(long pounds, long growerId) {
    
    if (growersInLine.isEmpty()) {
      return BidResponseResult.getInvalidResult("Can not accept bid because the bid has closed.");
    }
    if (growerId != (growersInLine.get(0)).getId()) {
      return BidResponseResult.getInvalidResult("Can not accept because the bid time has expired.");
      
    }

    if (!subtractFromPoundsRemaining(pounds)) {
      return BidResponseResult.getInvalidResult("Only " + poundsRemaining + " pounds remain. Can not accept bid for " + pounds + " pounds.");
    }

    if (poundsRemaining == 0) {
      cancellable.cancel();
      handlerBid.closeBid(BidStatus.ACCEPTED);
    }

    else {
      moveToNext();
    }

    return BidResponseResult.getValidResult();

  }

  @Override
  public BidResponseResult reject(long growerId) {
    if (growersInLine.isEmpty()) {
      return BidResponseResult.getInvalidResult("There is no need to reject the bid because it has already closed.");
    }
    if (growerId != (growersInLine.get(0)).getId()) {
      return BidResponseResult.getInvalidResult("Can not accept bid because the time bid has expired.");
    }
    
    moveToNext(); 
    return BidResponseResult.getValidResult();
    
  }

  public Boolean subtractFromPoundsRemaining(long pounds) {
    if (pounds > poundsRemaining) {
      return false;
    } else {
      poundsRemaining -= pounds;
      return true;
    }
  }

  // NOTE: Used only for testing
  public List<Grower> getGrowersInLine() {
    return growersInLine;
  }
}
