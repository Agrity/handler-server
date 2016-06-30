package services.offer_management;

import java.util.*;

import models.Offer;
import models.OfferResponseResult;
import models.Grower;
import java.time.Duration;

import akka.actor.Cancellable;
import scala.concurrent.duration.FiniteDuration;
import java.util.concurrent.TimeUnit;

import services.messaging.offer.OfferSendGridMessageService;
import services.messaging.offer.OfferSMSMessageService;

import play.Logger;
import play.libs.Akka;

public class WaterfallService implements OfferManagementService {

  private final Offer offer;
  private final Duration delay;
  private Cancellable cancellable;
  private long poundsRemaining;
  private List<Grower> growersInLine;

  OfferSendGridMessageService emailService = new OfferSendGridMessageService();
  OfferSMSMessageService smsService = new OfferSMSMessageService();

  public WaterfallService(Offer offer, Duration delay) {
    this.offer = offer;
    this.delay = delay;
    this.growersInLine = new ArrayList<Grower>(offer.getAllGrowers());
    this.poundsRemaining = offer.getAlmondPounds();

    OfferManagementService.offerToManageService.put(offer, this);

    cancellable = scheduleTimer();
  }

  private Cancellable scheduleTimer() {
    emailService.send(offer, growersInLine.get(0));
    smsService.send(offer, growersInLine.get(0));

    return Akka.system().scheduler().scheduleOnce(FiniteDuration.create(delay.toMillis(), TimeUnit.MILLISECONDS),
        new Runnable() {
          @Override
          public void run() {
            moveToNext();
            
          }
        }, Akka.system().dispatcher());
  }

  private void moveToNext() {
    emailService.sendClosed(offer, growersInLine.get(0));
    smsService.sendClosed(offer, growersInLine.get(0));
    if (growersInLine.size() != 0) {
      cancellable.cancel();
      growersInLine.remove(0);
      
      if (growersInLine.size() != 0) {
        emailService.send(offer, growersInLine.get(0));
        smsService.send(offer,growersInLine.get(0));
        cancellable = scheduleTimer();
      } else {
        offer.closeOffer();
      }    
    }
    else {
      Logger.error("growersInLine is trying to remove a grower despite being empty in WaterfallService of offerId: " + offer.getId());
    }
  }

  @Override
  public OfferResponseResult accept(long pounds, long growerId) {
    
    if (growersInLine.isEmpty()) {
      return OfferResponseResult.getInvalidResult("Can not accept offer because the offer has closed.");
    }
    if (growerId != (growersInLine.get(0)).getId()) {
      return OfferResponseResult.getInvalidResult("Can not accept because the offer time has expired.");
      
    }

    if (!subtractFromPoundsRemaining(pounds)) {
      return OfferResponseResult.getInvalidResult("Only " + poundsRemaining + " pounds remain. Can not accept offer for " + pounds + " pounds.");
    }

    if (poundsRemaining == 0) {
      cancellable.cancel();
      offer.closeOffer();
    }

    else {
      moveToNext();
    }

    return OfferResponseResult.getValidResult();

  }

  @Override
  public OfferResponseResult reject(long growerId) {
    if (growersInLine.isEmpty()) {
      return OfferResponseResult.getInvalidResult("There is no need to reject the offer because it has already closed.");
    }
    if (growerId != (growersInLine.get(0)).getId()) {
      return OfferResponseResult.getInvalidResult("Can not accept offer because the time offer has expired.");
    }
    
    moveToNext(); 
    return OfferResponseResult.getValidResult();
    
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
