package services.offer_management;


import java.util.*;

import models.Offer;
import models.Grower;
import models.OfferResponse;
import models.OfferResponse.ResponseStatus;


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

  private List<Grower> growers;

  public WaterfallService(Offer offer, Duration delay) {
    this.offer = offer;
    this.delay = delay;
    this.growers = new ArrayList<Grower>(offer.getAllGrowers());
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
             // TODO Notify Grower 0 the offer has closed. 
             // TODO Careful, probably needs to be done before moving to next.
              	moveToNext();
              }
            },
            Akka.system().dispatcher());
  }

  /* private void process() {
 
   	
  	OfferResponse response = offer.getGrowerOfferResponse(growers.get(0).getId());
    ResponseStatus status = response.getResponseStatus();

    if(status == ResponseStatus.ACCEPTED) {
      accept();
    } else {
      moveToNext();
    }
  }
  */

  private void moveToNext() {
    // TODO alert farmer 0 of time expired
    growers.remove(0);
    if(growers.size() != 0) {
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
  public List<Grower> getCurrentGrowers() {
    return growers;
  }
  
  

}
