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
import services.messaging.bid.BidSendGridMessageService;
import services.messaging.bid.BidSMSMessageService;

import play.libs.Akka;

public class TraderFCFSService implements BidManagementService {


  private final TraderBid traderBid;
  
  private Cancellable cancellable;
  private long poundsRemaining;
  private List<Long> handlerSellerIdsRemaining;
  BidSendGridMessageService emailService = new BidSendGridMessageService();
  BidSMSMessageService smsService = new BidSMSMessageService();
  HandlerSellerService handlerSellerService = new EbeanHandlerSellerService();

  public FCFSService(TraderBid traderBid, Duration timeAllowed) {
    this.traderBid = traderBid;
    this.poundsRemaining = traderBid.getAlmondPounds();

    handlerSellerIdsRemaining = getHandlerSellerIDList();

    emailService.send(traderBid);
    smsService.send(traderBid);

    BidManagementService.bidToManageService.put(traderBid, this);

    cancellable = Akka.system().scheduler()
        .scheduleOnce(FiniteDuration.create(timeAllowed.toMillis(), TimeUnit.MILLISECONDS), new Runnable() {
          @Override
          public void run() {
            if(poundsRemaining == traderBid.getAlmondPounds()) {
              traderBid.closeBid(BidStatus.REJECTED);
            } else {
              traderBid.closeBid(BidStatus.PARTIAL);
            }
            emailService.sendClosed(traderBid);
            smsService.sendClosed(traderBid);
          }
        }, Akka.system().dispatcher());
  }

  private List<Long> getHandlerSellerIDList() {
    List<Long> handlerSellers = new ArrayList<>();
    for(HandlerSeller g : traderBid.getAllHandlerSellers()) {
      handlerSellers.add(g.getId());
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
      sendClosedToRemaining();
    } else if(handlerSellerIdsRemaining.isEmpty()) {
        traderBid.closeBid(BidStatus.PARTIAL); 
        cancellable.cancel(); 
    } else {
      sendUpdatedToRemaining();
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
    for(Long handlerSellerId: handlerSellerIdsRemaining) {
      HandlerSeller g = handlerSellerService.getById(handlerSellerId);
      emailService.sendClosed(traderBid, g);
      smsService.sendClosed(traderBid, g);  
    }
  }

  private void sendUpdatedToRemaining() {
    for(Long handlerSellerId: handlerSellerIdsRemaining) {
      HandlerSeller g = handlerSellerService.getById(handlerSellerId);
      emailService.sendUpdated(traderBid, g, formatUpdateMessage());
      smsService.sendUpdated(traderBid, g, formatUpdateMessage());  
    }
  }

  private String formatUpdateMessage(){
    return "Your bid number " + Long.toString(traderBid.getId()) + " has been updated. \n"
        + "\tBid number " + Long.toString(traderBid.getId()) + " now contains the following specs: \n"
        + "\t\tAlmond type: " + traderBid.getAlmondVariety() +"\n\t\tPrice per pound: " 
        + traderBid.getPricePerPound() + "\n\t\tPOUNDS REMAINING: " 
        + Long.toString(poundsRemaining);
  }
}