package models;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
@Table(name = "BID_RESPONSES")
public abstract class BaseBidResponse extends BaseModel {

  public static enum ResponseStatus {
    NO_RESPONSE,
    ACCEPTED,
    REJECTED,
    REQUEST_CALL,
  }

  private ResponseStatus responseStatus;

  private long poundsAccepted;


  /* ===================================== Implementation ===================================== */


  /* === Attribute Accessors === */

  public Long getId() {
    return id;
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
}
