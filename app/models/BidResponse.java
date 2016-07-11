package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;


@Entity
public class BidResponse extends BaseModel {

  public static enum ResponseStatus {
    NO_RESPONSE,
    ACCEPTED,
    REJECTED,
    REQUEST_CALL,
  }

  /* NOTE: Bid is never explicitly set, but by being placed in a list of 
   * BidResponses in the Bid class, it is linked by Ebean. */
  @ManyToOne
  private Grower grower;

  @ManyToOne
  private HandlerBid bid;

  private ResponseStatus responseStatus;


  public static Finder<Long, BidResponse> find = new Finder<>(BidResponse.class);


  /* ===================================== Implementation ===================================== */

  public BidResponse(Grower grower) {
    super();

    this.grower = grower;
    this.responseStatus = ResponseStatus.NO_RESPONSE;
  }


  /* === Attribute Accessors === */


  public Long getId() {
    return id;
  }

  public Grower getGrower() {
    return grower;
  }

  public HandlerBid getBid() {
    return bid;
  }

  public ResponseStatus getResponseStatus() {
    return responseStatus;
  }

  public void setResponseStatus(ResponseStatus responseStatus) {
    this.responseStatus = responseStatus;
  }

  @Override
  public String toString() {
    return "(" + grower.getId() + ") -> (" + bid.getId() + ") " + grower.getFullName() + "\n";
  }
}
