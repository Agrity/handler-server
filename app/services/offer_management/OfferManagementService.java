package services.offer_management;

import java.util.HashMap;
import java.util.Map;

import models.Offer;

public interface OfferManagementService {

  public static final Map<Offer, OfferManagementService>
      offerToManageService = new HashMap<>();
	
	public void accept(Integer pounds);
	public void reject();
	
}
