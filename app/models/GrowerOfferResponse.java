package models;

import com.avaje.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints;

@Entity
public class GrowerOfferResponse extends Model {

  public static enum GrowerResponse {
    NO_RESPONSE,
    ACCEPTED,
    REJECTED,
    REQUEST_CALL,
  }

  @Id
  @Constraints.Min(10)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = DBConstants.GrowerColumns.ID)
  private Long id;

  @ManyToOne(cascade = CascadeType.PERSIST)
  private Grower grower;

  private GrowerResponse growerResponse;


  public static Finder<Long, GrowerOfferResponse> find = new Finder<>(GrowerOfferResponse.class);


  /* ========== Member Functions ========== */

  /*
   * Shim constructor intended for fake/mocked growers
   */
  public GrowerOfferResponse(Grower grower) {
    super();

    this.grower = grower;
    this.growerResponse = GrowerResponse.NO_RESPONSE;
  }

  public Long getId() {
    return id;
  }

  public Grower getGrower() {
    return grower;
  }

  public GrowerResponse getGrowersResponse() {
    return growerResponse;
  }

  public void setGrowersResponse(GrowerResponse growerResponse) {
    this.growerResponse = growerResponse;
  }

  @Override
  public String toString() {
    // TODO
    return "(" + grower.getId() + ") " + grower.getFullName() + "\n";
  }
}
