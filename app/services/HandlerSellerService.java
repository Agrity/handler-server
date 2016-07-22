package services;

import com.google.inject.ImplementedBy;

import java.util.List;

import models.HandlerSeller;

import services.impl.EbeanHandlerSellerService;

@ImplementedBy(EbeanHandlerSellerService.class)
public interface HandlerSellerService {

  public List<HandlerSeller> getAll();

  public HandlerSeller getById(long id);

  public List<HandlerSeller> getByTrader(long handlerSellerId);

  public List<HandlerSeller> getByBid(long bidId);

  public boolean checkCompanyNameAvailable(String companyName);

  public HandlerSeller handlerSellerLookupByPhoneNum(String phoneNum);
}
