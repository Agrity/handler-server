import java.util.*;

import models.Offer;
import models.Grower;
import models.OfferResponse;
import models.OfferResponse.ResponseStatus;


import java.time.Duration;
import java.time.LocalDateTime;


public class WaterfallService {

	// public static enum ResponseStatus {
 //    	NO_RESPONSE,
 //    	ACCEPTED,
 //   		REJECTED,
 //   		REQUEST_CALL,
	// }

	private final Offer offer;
	private final Duration delay;

	private LocalDateTime expirationTime;
	private List<Grower> growers;

	public WaterfallService(Offer offer, Duration delay) {
		this.offer = offer;
		this.delay = delay;
		expirationTime = offer.getCreatedAt().plus(delay);
		this.growers = new ArrayList<Grower>(offer.getAllGrowers());

	}


	public boolean process() {
		LocalDateTime current = LocalDateTime.now();	
		if(current.isAfter(expirationTime)) {
			//Time has passed, remove the front and return false if the list is empty
			growers.remove(0);
			if(growers.size() == 0) 
				return false;
		} else {
			OfferResponse response = offer.getGrowerOfferResponse(growers.get(0).getId());
			ResponseStatus status = response.getResponseStatus();
			if(status == ResponseStatus.ACCEPTED) {

			} else if(status == ResponseStatus.REJECTED) {

			} else if(status == ResponseStatus.NO_RESPONSE) {

			}


		}

		return true;
		


	}



}