package services.offer_management;


import java.util.*;

import models.Offer;
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
  private Integer poundsRemaining;
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
    //TODO Send grower 0 offer.
    
  	return Akka.system().scheduler().scheduleOnce(
        FiniteDuration.create(
            delay.toMillis(),
            TimeUnit.MILLISECONDS), 
            new Runnable() {
              @Override
              public void run() {
              	moveToNext();
              }
            },
            Akka.system().dispatcher());
  }

  private void moveToNext() {
    // TODO alert grower 0 of time expired
    growersInLine.remove(0);
    if(growersInLine.size() != 0) {
      cancellable = scheduleTimer();
    } else {
      offer.closeOffer();
    }
  }

  @Override
  public void accept(Integer pounds) {
  	
  	subtractFromPoundsRemaining(pounds);
  	cancellable.cancel();
  	
  	if(poundsRemaining == 0) {
      offer.closeOffer();
  	}
  	
  	else {
  	  moveToNext();
  	}
  	
  }

  @Override
  public void reject() {
    cancellable.cancel();
    moveToNext();
  }
  
  public void subtractFromPoundsRemaining(Integer pounds) {
		if(pounds > poundsRemaining) {
			// ERROR
			// TODO: fix this error check
		}
		else { 
			poundsRemaining -= pounds; 
		}
	}

  // NOTE: Used only for testing
  public List<Grower> getGrowersInLine() {
    return growersInLine;
  }
  
  

}
