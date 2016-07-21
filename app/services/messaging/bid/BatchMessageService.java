package services.messaging.bid;

import com.google.inject.ImplementedBy;

import models.Batch;
import models.HandlerSeller;

@ImplementedBy(BatchSendGridMessageService.class)
public interface BatchMessageService {

  public boolean send(Batch batch);

  public boolean send(Batch batch, HandlerSeller handlerSeller);

  public boolean sendClosed(Batch batch);

  public boolean sendClosed(Batch batch, HandlerSeller handlerSeller);

}