package services.bid_management;

import java.util.HashMap;
import java.util.Map;

import models.TraderBid;
import models.BidResponseResult;

public interface TraderBidManagementService {

  public static final Map<TraderBid, TraderBidManagementService>
      bidToManageService = new HashMap<>();

  public static BidManagementService getBidManagementService(TraderBid traderBid) {
    return bidToManageService.get(traderBid);
  }

  public static boolean removeBidManagementService(TraderBid traderBid) {
    return bidToManageService.remove(traderBid) != null;
  }

  public BidResponseResult accept(long pounds, long handlerSellerId);
  public BidResponseResult reject(long handlerSellerId);
}