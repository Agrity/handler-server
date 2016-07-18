package services.impl;

import com.avaje.ebean.Model.Finder;
import java.util.List;

import models.HandlerSeller;
import models.PhoneNumber;

import services.HandlerSellerService;
import play.Logger;

public class EbeanHandlerSellerService implements HandlerSellerService {

  private static Finder<Long, HandlerSeller> FINDER = new Finder<>(HandlerSeller.class);

  @Override
  public List<HandlerSeller> getAll() {
    return FINDER.all();
  }

  @Override
  public HandlerSeller getById(long id) {
    return FINDER.byId(id);
  }

  @Override
  public List<HandlerSeller> getByTrader(long traderId) {
    // TODO Assert Handler Exists, or Return Null
    return FINDER.where()
        // TODO Fix this Column Name.
        .eq("trader_id", traderId)
        .findList();
  }

  @Override
  public List<HandlerSeller> getByBid(long bidId) {
    // TODO Assert Grower Exists, or Return Null
    return FINDER.where()
        .eq("bids.id", bidId)
        .findList();
  }

  public HandlerSeller handlerSellerLookupByPhoneNum(String phoneNum) {
    List<HandlerSeller> handlerSellers = getAll(); 
    for (HandlerSeller handlerSeller: handlerSellers) {
      for (String curPhoneNum: handlerSeller.getPhoneNumsStrings()) {
        Logger.info("These are the curPhoneNums being looked up: " + curPhoneNum
                 + "   " + phoneNum + "\n\n");
        if (curPhoneNum.equals(phoneNum)) {
          return handlerSeller;
        }
      }
    }
    return null;
  }
}