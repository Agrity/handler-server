package services;

import com.google.inject.ImplementedBy;

import java.util.List;

import models.HandlerSeller;

import services.impl.EbeanHandlerSellerService;

@ImplementedBy(EbeanHandlerSellerService.class)
public interface HandlerSellerService {

  public List<HandlerSeller> getAll();

  public HandlerSeller getById(long id);

  public List<HandlerSeller> getByHandlerSeller(long handlerSellerId);

  public List<HandlerSeller> getByBid(long bidId);

  public HandlerSeller handlerSellerLookupByPhoneNum(String phoneNum);
}
