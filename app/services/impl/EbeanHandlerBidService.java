package services.impl;

import com.avaje.ebean.Model.Finder;

import java.util.List;

import models.HandlerBid;

import services.HandlerBidService;

public class EbeanHandlerBidService implements HandlerBidService {

  private static Finder<Long, HandlerBid> FINDER = new Finder<>(HandlerBid.class);

  @Override
  public List<HandlerBid> getAll() {
    return FINDER.all();
  }

  @Override
  public HandlerBid getById(long id) {
    return FINDER.byId(id);
  }

  public List<HandlerBid> getByHandler(long handlerId) {
    // TODO Assert Handler Exists, or Return Null
    return FINDER.where()
        // TODO Fix this Column Name.
        .eq("handler_id", handlerId)
        .findList();
  }

  public List<HandlerBid> getByGrower(long growerId) {
    // TODO Assert Grower Exists, or Return Null
    return FINDER.where()
        .eq("growers.id", growerId)
        .findList();
  }
}
