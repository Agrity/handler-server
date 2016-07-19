package services.messaging.bid;

import com.google.inject.ImplementedBy;

import models.BaseBid;
import models.BaseSeller;

//@ImplementedBy(HandlerBidSMSMessageService.class)
public interface BaseBidMessageService {

  public boolean send(BaseBid bid);

  public boolean send(BaseBid bid, BaseSeller seller);

  public boolean sendClosed(BaseBid bid);

  public boolean sendClosed(BaseBid bid, BaseSeller seller);

  public boolean sendUpdated(BaseBid bid, String msg);

  public boolean sendUpdated(BaseBid bid, BaseSeller seller, String msg);


}