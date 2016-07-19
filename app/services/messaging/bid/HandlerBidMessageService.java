package services.messaging.bid;

import com.google.inject.ImplementedBy;

import models.HandlerBid;
import models.Grower;

//@ImplementedBy(HandlerBidSMSMessageService.class)
public interface HandlerBidMessageService {

  public boolean send(HandlerBid handlerBid);

  public boolean send(HandlerBid handlerBid, Grower grower);

  public boolean sendClosed(HandlerBid handlerBid);

  public boolean sendClosed(HandlerBid handlerBid, Grower grower);

  public boolean sendUpdated(HandlerBid handlerBid, String msg);

  public boolean sendUpdated(HandlerBid handlerBid, Grower grower, String msg);


}