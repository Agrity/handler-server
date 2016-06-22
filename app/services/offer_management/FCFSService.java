package services.offer_management;

import java.time.Duration;

import models.Offer;

import akka.actor.Cancellable; 
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;
import play.libs.Akka; 

public class FCFSService implements OfferManagementService {
	
  private final Offer offer; 
  private Cancellable cancellable;
  private Integer poundsRemaining; 
	
  public FCFSService(Offer offer, Duration timeAllowed) {
    this.offer = offer;
    this.poundsRemaining = offer.getAlmondPounds();
    // All growers need to be messaged the offer.

    OfferManagementService.offerToManageService.put(offer, this);

    cancellable
        = Akka
          .system()
          .scheduler()
          .scheduleOnce(
              FiniteDuration
                  .create(
                      timeAllowed.toMillis(),
                      TimeUnit.MILLISECONDS), 
                      new Runnable() { 
                        @Override
                        public void run() { 
                            offer.closeOffer();
                            // Alert Growers offer has been closed.   
                        }
                      },
                      Akka.system().dispatcher()); 
    }
  
  	@Override
    public void accept(Integer pounds) { 
    	subtractFromPoundsRemaining(pounds);
    	//Update growers who have not accepted or rejected. offer.getGrowersWithNoResponse useful here.     	
    	if(poundsRemaining == 0) { 	
    		cancellable.cancel();
    		offer.closeOffer(); 
    	}
    }
  	
  	@Override 
  	public void reject() {
  	  // Do Nothing
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
  	
  	public Integer getPoundsRemaining() {
  		return poundsRemaining; 
  	} 	  
	}
	
