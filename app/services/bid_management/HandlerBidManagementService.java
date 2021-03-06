package services.bid_management;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import models.HandlerBid;
import models.BidResponseResult;

public interface HandlerBidManagementService {

  public static final Map<HandlerBid, HandlerBidManagementService>
      bidToManageService = new HashMap<>();

  public static HandlerBidManagementService getBidManagementService(HandlerBid handlerBid) {
    return bidToManageService.get(handlerBid);
  }

  public static boolean removeBidManagementService(HandlerBid handlerBid) {
    return bidToManageService.remove(handlerBid) != null;
  }

	public BidResponseResult accept(long pounds, long growerId);
	public BidResponseResult reject(long growerId);
  public BidResponseResult approve(long pounds, long growerId);
  public BidResponseResult disapprove(long growerId);
  public void addGrowers(List<Long> growerIds);
  public void close();
}
