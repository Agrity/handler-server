package services;
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

public class WaterfallService {

	private final Offer offer;
	private final Duration delay;
  private Cancellable cancellable;

//	private LocalDateTime expirationTime;
  private List<Grower> growers;

  public WaterfallService(Offer offer, Duration delay) {
    this.offer = offer;
    this.delay = delay;
//		expirationTime = offer.getCreatedAt().plus(delay);
    this.growers = new ArrayList<Grower>(offer.getAllGrowers());

    cancellable = scheduleTimer();
  }

  private Cancellable scheduleTimer() {
    //alert farmer 0 of offer
    return Akka.system().scheduler().scheduleOnce(FiniteDuration.create(delay.toMillis(), TimeUnit.MILLISECONDS), 
      new Runnable() {
        @Override
        public void run() {
          process();
        }
      }, Akka.system().dispatcher());
  }

  private void process() {
    OfferResponse response = offer.getGrowerOfferResponse(growers.get(0).getId());
    ResponseStatus status = response.getResponseStatus();

    if(status == ResponseStatus.ACCEPTED) {
      accept();
    } else {
      moveToNext();
    }

  }

  private void moveToNext() {
    // TODO alert farmer 0 of time expired
    growers.remove(0);
    if(growers.size() != 0) {
      cancellable = scheduleTimer();
    } else {
      //offer.close();
    }
  }

  public void accept() {
    cancellable.cancel();
    //offer.close();
  }

  public void reject() {
    
    cancellable.cancel();
    moveToNext();
  }

  public List<Grower> getCurrentGrowers() {
    return growers;
  }



 //  public boolean process() {
	// 	LocalDateTime current = LocalDateTime.now();	
	// 	if(current.isAfter(expirationTime)) {
	// 		// Time has passed, remove the front and return false if the list is empty
	// 		return moveToNext();
	// 	} 

	// 	OfferResponse response = offer.getGrowerOfferResponse(growers.get(0).getId());
	// 	ResponseStatus status = response.getResponseStatus();

	// 	switch(status) {
	// 		//set response status for grower here?
	// 		case ACCEPTED:
	// 		 return false;
	// 		case REJECTED:
	// 		 return moveToNext();
	// 		case REQUEST_CALL:
	// 			//return something?
	// 		default:
	// 	}
	// 	return true;

	// }

	// private boolean moveToNext() {
	// 	//Send message to 0 that their time has expired
	// 	growers.remove(0);
	// 	if(growers.size() == 0) return false;

	// 	expirationTime = LocalDateTime.now().plus(delay);
	// 	//Send message to new 0 for the offer
	// 	return true;
	// }




}