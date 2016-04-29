package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints;

@Entity
public class OfferResponse extends Model {

  public static enum ResponseStatus {
    NO_RESPONSE,
    ACCEPTED,
    REJECTED,
    REQUEST_CALL,
  }

  @Id
  @Constraints.Min(10)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /* NOTE: Bid is never explicitly set, but by being placed in a list of 
   * BidResponses in the Bid class, it is linked by Ebean. */
  @ManyToOne
  private Grower grower;

  @ManyToOne
  private Offer offer;

  private ResponseStatus responseStatus;


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

  public void setResponseStatus(ResponseStatus responseStatus) {
    this.responseStatus = responseStatus;
  }

  @Override
  public String toString() {
    return "(" + grower.getId() + ") -> (" + offer.getId() + ") " + grower.getFullName() + "\n";
  }
}
