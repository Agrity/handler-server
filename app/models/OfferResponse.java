package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;


@Entity
public class OfferResponse extends BaseModel {

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
  private Offer offer;

  private ResponseStatus responseStatus;

  private long poundsAccepted;

  public static Finder<Long, OfferResponse> find = new Finder<>(OfferResponse.class);


  /* ===================================== Implementation ===================================== */

  public OfferResponse(Grower grower) {
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

  public Offer getOffer() {
    return offer;
  }

  public ResponseStatus getResponseStatus() {
    return responseStatus;
  }

  public long getPoundsAccepted() {
    return poundsAccepted;
  }

  public void setPoundsAccepted(long poundsAccepted) {
    this.poundsAccepted = poundsAccepted; 
  }

  public void setResponseStatus(ResponseStatus responseStatus) {
    this.responseStatus = responseStatus;
  }

  @Override
  public String toString() {
    return "(" + grower.getId() + ") -> (" + offer.getId() + ") " + grower.getFullName() + "\n";
  }
}
