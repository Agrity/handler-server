package services.messaging.offer;

import com.google.inject.ImplementedBy;

import models.Offer;

@ImplementedBy(OfferSMSMessageService.class)
public interface OfferMessageService {

  public boolean send(Offer offer);

}
