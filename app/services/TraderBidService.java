package services;

import com.google.inject.ImplementedBy;

import java.util.List;

import models.TraderBid;

import services.impl.EbeanTraderBidService;

@ImplementedBy(EbeanTraderBidService.class)
public interface TraderBidService {

  public List<TraderBid> getAll();

  public TraderBid getById(long id);

  public List<TraderBid> getByTrader(long traderId);

  public List<TraderBid> getByHandlerSeller(long handlerSellerId);
}