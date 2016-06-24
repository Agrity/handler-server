package services.offer_management;

import java.util.*;

import models.Offer;
import models.OfferResponse;
import models.OfferResponse.ResponseStatus;
import models.Grower;
import java.time.Duration;

import akka.actor.Cancellable;
import scala.concurrent.duration.FiniteDuration;
import java.util.concurrent.TimeUnit;

import play.libs.Akka;

public class WaterfallService implements OfferManagementService {

  private final Offer offer;
  private final Duration delay;
  private Cancellable cancellable;
  private long poundsRemaining;
  private List<Grower> growersInLine;

  public WaterfallService(Offer offer, Duration delay) {
    this.offer = offer;
    this.delay = delay;
    this.growersInLine = new ArrayList<Grower>(offer.getAllGrowers());
    this.poundsRemaining = offer.getAlmondPounds();

    OfferManagementService.offerToManageService.put(offer, this);

    cancellable = scheduleTimer();
  }

  private Cancellable scheduleTimer() {
    // TODO Send grower 0 offer. Use poundsRemaining

    return Akka.system().scheduler().scheduleOnce(FiniteDuration.create(delay.toMillis(), TimeUnit.MILLISECONDS),
        new Runnable() {
          @Override
          public void run() {
            moveToNext();
            
          }
        }, Akka.system().dispatcher());
  }

  private void moveToNext() {
    // TODO alert grower 0 of time expired
    if (growersInLine.size() != 0) {
      cancellable.cancel();
      growersInLine.remove(0);
      
      if (growersInLine.size() != 0) {
        cancellable = scheduleTimer();
      } else {
        offer.closeOffer();
      }    
    }
    else {
      //TODO: Report error.
    }
  }

  @Override
  public Boolean accept(long pounds, long growerId) {
    
    if (growersInLine.isEmpty()) {
      return false;
    }
    if (growerId != (growersInLine.get(0)).getId()) {
      // TODO: Add Error, wrong Grower trying to accept offer.
      // For example, time already expired for previous grower trying to accept.
      return false;
      
    }

    if (!subtractFromPoundsRemaining(pounds)) {
      return false;
    }

    if (poundsRemaining == 0) {
      cancellable.cancel();
      offer.closeOffer();
    }

    else {
      moveToNext();
    }

    return true;

  }

  @Override
  public Boolean reject(long growerId) {
    if (growersInLine.isEmpty()) {
      return false;
    }
    if (growerId != (growersInLine.get(0)).getId()) {
      return false;
    }
    
    moveToNext(); 
    return true;
    
  }

  public Boolean subtractFromPoundsRemaining(long pounds) {
    if (pounds > poundsRemaining) {
      return false;
      // TODO: Error message!
      // TODO: fix this error check
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
