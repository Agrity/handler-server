package services.offer_management;

import java.time.Duration;

import java.util.*;

import models.Offer;
import models.Offer.OfferStatus;
import models.OfferResponseResult;
import models.Grower;

import akka.actor.Cancellable;
import scala.concurrent.duration.FiniteDuration;
import java.util.concurrent.TimeUnit;

import services.messaging.offer.OfferSendGridMessageService;
import services.messaging.offer.OfferSMSMessageService;

import play.libs.Akka;

public class FCFSService implements OfferManagementService {


  private final Offer offer;
  
  private Cancellable cancellable;
  private long poundsRemaining;
  private List<Long> growerIDs;
  OfferSendGridMessageService emailService = new OfferSendGridMessageService();
  OfferSMSMessageService smsService = new OfferSMSMessageService();

  public FCFSService(Offer offer, Duration timeAllowed) {
    this.offer = offer;
    this.poundsRemaining = offer.getAlmondPounds();

    growerIDs = getGrowerIDList();

    emailService.send(offer);
    smsService.send(offer);

    OfferManagementService.offerToManageService.put(offer, this);

    cancellable = Akka.system().scheduler()
        .scheduleOnce(FiniteDuration.create(timeAllowed.toMillis(), TimeUnit.MILLISECONDS), new Runnable() {
          @Override
          public void run() {
            if(poundsRemaining == offer.getAlmondPounds()) {
              offer.closeOffer(OfferStatus.REJECTED);
            } else {
              offer.closeOffer(OfferStatus.PARTIAL);
            }
            emailService.sendClosed(offer);
            smsService.sendClosed(offer);
          }
        }, Akka.system().dispatcher());
  }

  private List<Long> getGrowerIDList() {
    List<Long> growers = new ArrayList<>();
    for(Grower g : offer.getAllGrowers()) {
      growers.add(g.getId());
    }
    return growers;
  }

  @Override
  public OfferResponseResult accept(long pounds, long growerId) {
    
    if (!subtractFromPoundsRemaining(pounds)) {
      return OfferResponseResult.getInvalidResult("Only " + poundsRemaining + " pounds remain. Can not accept offer for " + pounds + " pounds.");
    }

    growerIDs.remove((Long) growerId);

    if (poundsRemaining == 0) {
      cancellable.cancel();
      offer.closeOffer(OfferStatus.ACCEPTED);
      sendClosedToRemaining();
    } else if(growerIDs.isEmpty()) {
        offer.closeOffer(OfferStatus.PARTIAL); 
        cancellable.cancel(); 
    } else {
      sendUpdatedToRemaining();
    }

    return OfferResponseResult.getValidResult();
  }

  @Override
  public OfferResponseResult reject(long growerId) {
    growerIDs.remove((Long) growerId);
    if(growerIDs.isEmpty()) {
      cancellable.cancel();
      if(poundsRemaining == offer.getAlmondPounds()) {
        offer.closeOffer(OfferStatus.REJECTED);
      } else {
        offer.closeOffer(OfferStatus.PARTIAL);
      }
    }
    return OfferResponseResult.getValidResult();
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
    for (Grower g : offer.getNoResponseGrowers()) {
      emailService.sendClosed(offer, g);
      smsService.sendClosed(offer, g);
    }
    for (Grower g : offer.getCallRequestedGrowers()) {
      emailService.sendClosed(offer, g);
      smsService.sendClosed(offer, g);
    }
  }

  private void sendUpdatedToRemaining() {
    for (Grower g : offer.getNoResponseGrowers()) {
      emailService.sendUpdated(offer, g, formatUpdateMessage());
      smsService.sendUpdated(offer, g, formatUpdateMessage());
    }
    for (Grower g : offer.getCallRequestedGrowers()) {
      emailService.sendUpdated(offer, g, formatUpdateMessage());
      smsService.sendUpdated(offer, g, formatUpdateMessage());
    }
  }

  private String formatUpdateMessage(){
    return "Your offer number " + Long.toString(offer.getId()) + " has been updated. \n"
        + "\tOffer number " + Long.toString(offer.getId()) + " now contains the following specs: \n"
        + "\t\tAlmond type: " + offer.getAlmondVariety() +"\n\t\tPrice per pound: " 
        + offer.getPricePerPound() + "\n\t\tPOUNDS REMAINING: " 
        + Long.toString(poundsRemaining);
  }
}
