package services.offer_management;

import java.time.Duration;

import models.Offer;
import models.OfferResponseResult;
import akka.actor.Cancellable;
import scala.concurrent.duration.FiniteDuration;
import java.util.concurrent.TimeUnit;

import play.libs.Akka;

public class FCFSService implements OfferManagementService {


  private final Offer offer;
  
  private Cancellable cancellable;
  private long poundsRemaining;

  public FCFSService(Offer offer, Duration timeAllowed) {
    this.offer = offer;
    this.poundsRemaining = offer.getAlmondPounds();
    // TODO: All growers need to be messaged the offer.

    OfferManagementService.offerToManageService.put(offer, this);

    cancellable = Akka.system().scheduler()
        .scheduleOnce(FiniteDuration.create(timeAllowed.toMillis(), TimeUnit.MILLISECONDS), new Runnable() {
          @Override
          public void run() {
            offer.closeOffer();
            // TODO: Alert Growers offer has been closed.
          }
        }, Akka.system().dispatcher());
  }

  @Override
  public OfferResponseResult accept(long pounds, long growerId) {
    
    if (!subtractFromPoundsRemaining(pounds)) {
      return OfferResponseResult.getInvalidResult("Only " + poundsRemaining + " pounds remain. Can not accept offer for " + pounds + " pounds.");
    }

    if (poundsRemaining == 0) {
      cancellable.cancel();
      offer.closeOffer();
    }
    // TODO: Send pounds remaining or closed update to growers who have not
    // accepted or rejected. offer.getGrowersWithNoResponse useful here.
    return OfferResponseResult.getValidResult();
  }

  @Override
  public OfferResponseResult reject(long growerId) {
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
}
