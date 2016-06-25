package services.messaging.offer;

import com.google.inject.ImplementedBy;

import models.Offer;
import models.Grower;

@ImplementedBy(OfferSendGridMessageService.class)
public interface OfferMessageService {

  public boolean send(Offer offer);

  public boolean send(Offer offer, Grower grower);

  public boolean sendClosed(Offer offer);

  public boolean sendClosed(Offer offer, Grower grower);

  public boolean sendUpdated(Offer offer, String msg);

  public boolean sendUpdated(Offer offer, Grower grower, String msg);


}
