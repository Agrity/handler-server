package services.messaging.offer;

import com.google.inject.ImplementedBy;

import models.Offer;
import models.Grower;

@ImplementedBy(OfferSendGridMessageService.class)
public interface OfferMessageService {

  public boolean send(Offer offer);

  public boolean sendToOne(Offer offer, Grower grower);

  // public boolean sendClosed(Offer offer);

  // public boolean sendClosed(Offer offer, Long growerId);

  // public boolean sendUpdated(Offer offer);

  // public boolean sendUpdated(Offer offer, Long growerId);


}
