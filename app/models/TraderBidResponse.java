package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class TraderBidResponse extends BaseBidResponse {

  @ManyToOne
  private HandlerSeller handlerSeller;

  /* NOTE: Bid is never explicitly set, but by being placed in a list of 
   * BidResponses in the Bid class, it is linked by Ebean. */
  @ManyToOne
  private TraderBid traderBid;

  public static Finder<Long, TraderBidResponse> find = new Finder<>(TraderBidResponse.class);

  public TraderBidResponse(HandlerSeller handlerSeller) {
    super();

    setHandlerSeller(handlerSeller);
    setResponseStatus(ResponseStatus.NO_RESPONSE);
  }

  public HandlerSeller getHandlerSeller() {
    return handlerSeller;
  }

  public TraderBid getBid() {
    return traderBid;
  }

  public void setHandlerSeller(HandlerSeller newHandlerSeller) {
  	handlerSeller = newHandlerSeller;
  }

  public void setBid(TraderBid newTraderBid) {
  	traderBid = newTraderBid;
  }

  @Override
  public String toString() {
    return "(" + getHandlerSeller().getId() + ") -> (" + getBid().getId() + ") " + getHandlerSeller().getFullName() + "\n";
  }
}