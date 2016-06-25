package services.offer_management;

import java.time.Duration;

import models.Offer;
import models.OfferResponse;
import models.OfferResponse.ResponseStatus;
import models.Grower;
import akka.actor.Cancellable;
import scala.concurrent.duration.FiniteDuration;
import java.util.concurrent.TimeUnit;

import services.messaging.offer.OfferSendGridMessageService;

import play.libs.Akka;

public class FCFSService implements OfferManagementService {


  private final Offer offer;
  
  private Cancellable cancellable;
  private long poundsRemaining;
  OfferSendGridMessageService emailService = new OfferSendGridMessageService();

  public FCFSService(Offer offer, Duration timeAllowed) {
    this.offer = offer;
    this.poundsRemaining = offer.getAlmondPounds();

    emailService.send(offer);

    OfferManagementService.offerToManageService.put(offer, this);

    cancellable = Akka.system().scheduler()
        .scheduleOnce(FiniteDuration.create(timeAllowed.toMillis(), TimeUnit.MILLISECONDS), new Runnable() {
          @Override
          public void run() {
            offer.closeOffer();
            emailService.sendClosed(offer);
          }
        }, Akka.system().dispatcher());
  }

  @Override
  public Boolean accept(long pounds, long growerId) {
    
    if (!subtractFromPoundsRemaining(pounds)) {
      return false;
    }

    if (poundsRemaining == 0) {
      cancellable.cancel();
      offer.closeOffer();
      sendClosedToRemaining();
      return true;
    }

    sendUpdatedToRemaining();
    return true;
  }

  @Override
  public Boolean reject(long growerId) {
    return true;
  }

  public Boolean subtractFromPoundsRemaining(long pounds) {
    if (pounds > poundsRemaining) {
      return false;
      // TODO: Error message!
      // TODO: fix this error check
    } 
    else {
      poundsRemaining -= pounds;
      return true;
    }
  }

  private void sendClosedToRemaining() {
    for (Grower g : offer.getNoResponseGrowers()) {
      emailService.sendClosed(offer, g);
    }
    for (Grower g : offer.getCallRequestedGrowers()) {
      emailService.sendClosed(offer, g);
    }
  }

  private void sendUpdatedToRemaining() {
    for (Grower g : offer.getNoResponseGrowers()) {
      emailService.sendUpdated(offer, g, formatUpdateMessage());
    }
    for (Grower g : offer.getCallRequestedGrowers()) {
      emailService.sendUpdated(offer, g, formatUpdateMessage());
    }
  }

  private String formatUpdateMessage(){
    return "Your offer number " + Long.toString(offer.getId()) + " has been updated. \n"
        + "\tOffer number " + Long.toString(offer.getId()) + "now contains the following specs: \n"
        + "\t\tAlmond type: " + offer.getAlmondVariety() +"\n\t\tPrice per pound: " 
        + offer.getPricePerPound() + "\n\t\tPOUNDS REMAINING: " 
        + Long.toString(poundsRemaining);
  }
}
