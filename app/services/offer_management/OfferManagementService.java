package services.offer_management;

import java.util.HashMap;
import java.util.Map;

import models.Offer;

public interface OfferManagementService {

  public static final Map<Offer, OfferManagementService>
      offerToManageService = new HashMap<>();

  public static OfferManagementService getOfferManagementService(Offer offer) {
    return offerToManageService.get(offer);
  }

  public static boolean removeOfferManagementService(Offer offer) {
    return offerToManageService.remove(offer) != null;
  }
	
	public Boolean accept(long pounds, long growerId);
	public Boolean reject(long growerId);
	
}
