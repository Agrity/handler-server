package services.impl;

import com.avaje.ebean.Model.Finder;

import java.util.List;

import models.TraderBid;

import services.TraderBidService;

public class EbeanTraderBidService implements TraderBidService {

  private static Finder<Long, TraderBid> FINDER = new Finder<>(TraderBid.class);

  @Override
  public List<TraderBid> getAll() {
    return FINDER.all();
  }

  @Override
  public TraderBid getById(long id) {
    return FINDER.byId(id);
  }

  public List<TraderBid> getByTrader(long traderId) {
    // TODO Assert Trader Exists, or Return Null
    return FINDER.where()
        // TODO Fix this Column Name.
        .eq("trader_id", traderId)
        .findList();
  }

  public List<TraderBid> getByHandlerSeller(long handlerSellerId) {
    // TODO Assert HandlerSeller Exists, or Return Null
    // TODO Fix this column name?
    return FINDER.where()
        .eq("handlerSellers.id", handlerSellerId)
        .findList();
  }
}
