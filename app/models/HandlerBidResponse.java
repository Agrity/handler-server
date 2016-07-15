package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class HandlerBidResponse extends BaseBidResponse {

  @ManyToOne
  private Grower grower;

  /* NOTE: Bid is never explicitly set, but by being placed in a list of 
   * BidResponses in the Bid class, it is linked by Ebean. */
  @ManyToOne
  private HandlerBid handlerBid;

  public static Finder<Long, HandlerBidResponse> find = new Finder<>(HandlerBidResponse.class);

  public HandlerBidResponse(Grower grower) {
    super();

    setGrower(grower);
    setResponseStatus(ResponseStatus.NO_RESPONSE);
  }

  public Grower getGrower() {
    return grower;
  }

  public HandlerBid getBid() {
    return handlerBid;
  }

  public void setGrower(Grower newGrower) {
  	grower = newGrower;
  }

  public void setBid(HandlerBid newHandlerBid) {
  	handlerBid = newHandlerBid;
  }

  @Override
  public String toString() {
    return "(" + getGrower().getId() + ") -> (" + getBid().getId() + ") " + getGrower().getFullName() + "\n";
  }
}