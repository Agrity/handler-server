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

  private List<Grower> growers;

  public WaterfallService(Offer offer, Duration delay) {
    this.offer = offer;
    this.delay = delay;
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
      offer.closeOffer();
    }
  }

  public void accept() {
    cancellable.cancel();
    offer.closeOffer();
  }

  public void reject() {
    cancellable.cancel();
    moveToNext();
  }

  // NOTE: Used only for testing
  public List<Grower> getCurrentGrowers() {
    return growers;
  }

}