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
import services.messaging.bid.BidSendGridMessageService;
import services.messaging.bid.BidSMSMessageService;

import play.libs.Akka;

public class FCFSService implements BidManagementService {


  private final HandlerBid handlerBid;
  
  private Cancellable cancellable;
  private long poundsRemaining;
  private List<Long> growerIdsRemaining;
  BidSendGridMessageService emailService = new BidSendGridMessageService();
  BidSMSMessageService smsService = new BidSMSMessageService();
  GrowerService growerService = new EbeanGrowerService();

  public FCFSService(HandlerBid handlerBid, Duration timeAllowed) {
    this.handlerBid = handlerBid;
    this.poundsRemaining = handlerBid.getAlmondPounds();

    growerIdsRemaining = getGrowerIDList();

    emailService.send(handlerBid);
    smsService.send(handlerBid);

    BidManagementService.bidToManageService.put(handlerBid, this);

    cancellable = Akka.system().scheduler()
        .scheduleOnce(FiniteDuration.create(timeAllowed.toMillis(), TimeUnit.MILLISECONDS), new Runnable() {
          @Override
          public void run() {
            if(poundsRemaining == handlerBid.getAlmondPounds()) {
              handlerBid.closeBid(BidStatus.REJECTED);
            } else {
              handlerBid.closeBid(BidStatus.PARTIAL);
            }
            emailService.sendClosed(handlerBid);
            smsService.sendClosed(handlerBid);
          }
        }, Akka.system().dispatcher());
  }

  private List<Long> getGrowerIDList() {
    List<Long> growers = new ArrayList<>();
    for(Grower g : handlerBid.getAllGrowers()) {
      growers.add(g.getId());
    }
    return growers;
  }

  @Override
  public BidResponseResult accept(long pounds, long growerId) {
    
    if (!subtractFromPoundsRemaining(pounds)) {
      return BidResponseResult.getInvalidResult("Only " + poundsRemaining + " pounds remain. Can not accept bid for " + pounds + " pounds.");
    }

    growerIdsRemaining.remove((Long) growerId);

    if (poundsRemaining == 0) {
      cancellable.cancel();
      handlerBid.closeBid(BidStatus.ACCEPTED);
      sendClosedToRemaining();
    } else if(growerIdsRemaining.isEmpty()) {
        handlerBid.closeBid(BidStatus.PARTIAL); 
        cancellable.cancel(); 
    } else {
      sendUpdatedToRemaining();
    }

    return BidResponseResult.getValidResult();
  }

  @Override
  public BidResponseResult reject(long growerId) {
    growerIdsRemaining.remove((Long) growerId);
    if(growerIdsRemaining.isEmpty()) {
      cancellable.cancel();
      if(poundsRemaining == handlerBid.getAlmondPounds()) {
        handlerBid.closeBid(BidStatus.REJECTED);
      } else {
        handlerBid.closeBid(BidStatus.PARTIAL);
      }
    }
    return BidResponseResult.getValidResult();
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

  private void sendClosedToRemaining() {
    for(Long growerId: growerIdsRemaining) {
      Grower g = growerService.getById(growerId);
      emailService.sendClosed(handlerBid, g);
      smsService.sendClosed(handlerBid, g);  
    }
  }

  private void sendUpdatedToRemaining() {
    for(Long growerId: growerIdsRemaining) {
      Grower g = growerService.getById(growerId);
      emailService.sendUpdated(handlerBid, g, formatUpdateMessage());
      smsService.sendUpdated(handlerBid, g, formatUpdateMessage());  
    }
  }

  private String formatUpdateMessage(){
    return "Your bid number " + Long.toString(handlerBid.getId()) + " has been updated. \n"
        + "\tBid number " + Long.toString(handlerBid.getId()) + " now contains the following specs: \n"
        + "\t\tAlmond type: " + handlerBid.getAlmondVariety() +"\n\t\tPrice per pound: " 
        + handlerBid.getPricePerPound() + "\n\t\tPOUNDS REMAINING: " 
        + Long.toString(poundsRemaining);
  }
}
