package services;

import com.google.inject.ImplementedBy;

import java.util.List;

import models.HandlerBid;

import services.impl.EbeanHandlerBidService;

@ImplementedBy(EbeanHandlerBidService.class)
public interface HandlerBidService {

  public List<HandlerBid> getAll();

  public HandlerBid getById(long id);

  public List<HandlerBid> getByHandler(long handlerId);

  public List<HandlerBid> getByGrower(long growerId);

}
