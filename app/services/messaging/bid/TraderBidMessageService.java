package services.messaging.bid;

import com.google.inject.ImplementedBy;

import models.TraderBid;
import models.HandlerSeller;

@ImplementedBy(TraderBidSendGridMessageService.class)
public interface TraderBidMessageService {

  public boolean send(TraderBid traderBid);

  public boolean send(TraderBid traderBid, HandlerSeller handlerSeller);

  public boolean sendClosed(TraderBid traderBid);

  public boolean sendClosed(TraderBid traderBid, HandlerSeller handlerSeller);

  public boolean sendUpdated(TraderBid traderBid, String msg);

  public boolean sendUpdated(TraderBid traderBid, HandlerSeller handlerSeller, String msg);


}