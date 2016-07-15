package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import play.Logger;

@MappedSuperclass
public abstract class BaseBidResponse extends BaseModel {

  public static enum ResponseStatus {
    NO_RESPONSE,
    ACCEPTED,
    REJECTED,
    REQUEST_CALL,
  }

  private ResponseStatus responseStatus;


  /* ===================================== Implementation ===================================== */


  /* === Attribute Accessors === */

  public Long getId() {
    return id;
  }

  public ResponseStatus getResponseStatus() {
    return responseStatus;
  }

  public void setResponseStatus(ResponseStatus responseStatus) {
    this.responseStatus = responseStatus;
  }
}
