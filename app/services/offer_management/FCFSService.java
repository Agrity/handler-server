package services.offer_management;

import java.time.Duration;

import models.Offer;
import models.OfferResponse.ResponseStatus;
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
    public Boolean accept(long pounds, long growerId) { 
    	
  		if(offer.getGrowerOfferResponse(growerId).getResponseStatus() != ResponseStatus.NO_RESPONSE) {
  			return false; 
  			//TODO: Add Error. Grower already responded. 
  		}
  		
  		if(!subtractFromPoundsRemaining(pounds)) {
    		return false;
    	}
    	else{
    	
    		if(poundsRemaining == 0) { 	
    			cancellable.cancel();
    			offer.closeOffer(); 
    		}
    	  
    		//TODO: Send pounds remaining or closed update to growers who have not accepted or rejected. offer.getGrowersWithNoResponse useful here.     		
    		return true;
    	}
    }
  	
  	@Override 
  	public Boolean reject(long growerId) {
  	  // Do Nothing
  		if(offer.getGrowerOfferResponse(growerId).getResponseStatus() != ResponseStatus.NO_RESPONSE) {
  			return false; 
  			//TODO: Add Error. Grower already responded. 
  		} else {
  		return true;
  		}
  	}
  	
  	public Boolean subtractFromPoundsRemaining(long pounds) {
  		if(pounds > poundsRemaining) {
  			return false;
  			// TODO: Error message!
  			// TODO: fix this error check
  		}
  		else { 
  			poundsRemaining -= pounds; 
  			return true; 
  		}
  	}	  
	}
	
