package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("TRADER_BID_RESPONSE")
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

  @JsonIgnore
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
