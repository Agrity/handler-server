package services.offer_management;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import models.Grower;
import models.Offer;

import akka.actor.Cancellable; 
import scala.concurrent.duration.FiniteDuration;
import java.util.concurrent.TimeUnit;
import play.libs.Akka; 

public class FCFSService implements OfferManagementService {
	
  private final Offer offer; 
  private final LocalDateTime expireTime;
  private Cancellable cancellable;
	
  public FCFSService(Offer offer, Duration timeAllowed) {
    this.offer = offer;
    this.expireTime = (LocalDateTime.now()).plus(timeAllowed);  	 
    // All growers need to be messaged the offer. 

    cancellable = Akka.system().scheduler().scheduleOnce(FiniteDuration.create(timeAllowed.toMillis(), TimeUnit.MILLISECONDS), 
      new Runnable() { 
    	@Override
    	public void run() { 
          process();
    	  }
      }, Akka.system().dispatcher()); 
    }
  
  	@Override
    public void accept() { 
    	cancellable.cancel(); 
    	offer.closeOffer(); 
    }
  	
  	@Override 
  	public void reject() {
  	  // Do Nothing
  	}
  
	  public void process() { 
	  	offer.closeOffer();  
	  	//TODO Alert other growers that offer has been closed. 	  
	  }
	}
	
