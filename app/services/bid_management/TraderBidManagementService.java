package services.bid_management;

import java.util.HashMap;
import java.util.Map;

import models.TraderBid;
import models.BidResponseResult;

import java.util.List;

public interface TraderBidManagementService {

  public static final Map<TraderBid, TraderBidManagementService>
      bidToManageService = new HashMap<>();

  public static TraderBidManagementService getBidManagementService(TraderBid traderBid) {
    return bidToManageService.get(traderBid);
  }

  public static boolean removeBidManagementService(TraderBid traderBid) {
    return bidToManageService.remove(traderBid) != null;
  }

  public BidResponseResult accept(long pounds, long handlerSellerId);
  public BidResponseResult reject(long handlerSellerId);
  public BidResponseResult approve(long pounds, long handlerSellerId);
  public BidResponseResult disapprove(long handlerSellerId);
  public void addHandlerSellers(List<Long> handlerSellerIds);
  public void close();
}