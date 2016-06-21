package services.offer_management;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import models.Grower;
import models.Offer;

public class FCFSService implements OfferManagementService {
	
  private final Offer offer; 
  private final LocalDateTime expireTime;
	
  public FCFSService(Offer offer, Duration timeAllowed) {
    this.offer = offer;
    this.expireTime = (LocalDateTime.now()).plus(timeAllowed);  	 
    // All growers need to be messaged the offer. 
		
  }

  @Override
  public boolean process() {
    List<Grower> acceptedGrowers = offer.getAcceptedGrowers();
		
    if (acceptedGrowers.size() > 1) {
    // ^if statement to address case where multiple growers have accepted since process() was last called.
			
      Grower firstGrowerToRespond = acceptedGrowers.get(0); 
      LocalDateTime fastestResponse = (offer.getGrowerOfferResponse((acceptedGrowers.get(0)).getId())).getUpdatedAt(); 
      // ^firstGrowertoRespond and fastestResponse initialized to first grower and first grower response. 
		    
      LocalDateTime curGrowerResponseTime;
		    
      for (Grower grower : acceptedGrowers) {
        curGrowerResponseTime = (offer.getGrowerOfferResponse(grower.getId())).getUpdatedAt(); 
				
        if (curGrowerResponseTime.isBefore(fastestResponse)) { 
          fastestResponse = curGrowerResponseTime;
          firstGrowerToRespond = grower;
        }
      }
      // TODO Fix errors caused by this. 
      // TODO Offer responses that are not fastestResponse need to be addressed. 
      // TODO Growers who were not firstGrowerToRespond need to be alerted that their accept did not work.
      
    }

    if (acceptedGrowers.size() >= 1 || LocalDateTime.now().isAfter(expireTime)) { 
      offer.closeOffer(); 
        return false; 
    	//TODO Alert other growers that offer has been closed. 	
    }
    
    else {
      return true; 
	}
    
  }
}

