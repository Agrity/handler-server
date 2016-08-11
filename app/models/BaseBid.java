package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.data.validation.Constraints;
import java.text.NumberFormat;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import models.Almond.AlmondVariety;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
@Table(name = "BIDS")
public abstract class BaseBid extends BaseModel {


  public static enum BidStatus{
    OPEN, 
    REJECTED, 
    ACCEPTED,
    PARTIAL,
  }


  /* ======================================= Attributes ======================================= */


	@Constraints.Required
  private String almondVariety;

  @Constraints.Required
  private Integer almondPounds;

  @Constraints.Required
  private String pricePerPound;

  @Column(columnDefinition = "TEXT")
  private String comment = "";

  private String managementService;

  private LocalDateTime expirationTime;

  private BidStatus bidStatus = BidStatus.OPEN;

  private Integer poundsRemaining;

  // TODO Change to AlmondSize Size within Almond model.
  @Constraints.Required
  private String almondSize;


  /* ======================================= Attribute Accessors ======================================= */


  public String getAlmondVariety() {
    return almondVariety;
  }

  public String getAlmondSize() {
    return almondSize;
  }

  public Integer getAlmondPounds() {
    return almondPounds;
  }

   @JsonIgnore
  public String getAlmondPoundsString() {
    return NumberFormat.getIntegerInstance().format(getAlmondPounds());
  }

  public String getPricePerPound() {
    return pricePerPound;
  }

  public String getComment() {
    return comment;
  }

  public String getManagementService() {
    return managementService;
  }

  public LocalDateTime getExpirationTime() {
    return expirationTime;
  }

  public String getExpirationTimeAsString() {
    return expirationTime.toString();
  }

  @JsonIgnore
  public String getPrettyExpirationTime() {
    String exp 
      = expirationTime.getMonthValue() + "/" + expirationTime.getDayOfMonth() + " ";
    int hour = expirationTime.getHour();
    String ampm = " AM";
    if(hour == 0) {
      hour = 12;
    } else if (hour == 12) {
      ampm = " PM";
    } else if (hour > 12) {
      hour -= 12;
      ampm = " PM";
    }
    return exp + hour + ":" + expirationTime.getMinute() + ampm;
  }

  public BidStatus getBidStatus() {
    return bidStatus;
  }

  public boolean bidCurrentlyOpen() {
    return bidStatus == BidStatus.OPEN;
  }

  public Integer getPoundsRemaining() {
    return poundsRemaining;
  }


  /* ======================================= Attribute Setters ======================================= */


  public void setAlmondVariety(String newVariety) {
    almondVariety = newVariety;
  }

  public void setAlmondSize(String newSize) {
    almondSize = newSize;
  }

  public void setAlmondPounds(Integer newLbs) {
    almondPounds = newLbs;
  }

   public void setPricePerPound(String newPpp) {
    pricePerPound = newPpp;
  }

  public void setComment(String newComment) {
    comment = newComment;
  }

  public void setManagementService(String newManagementService) {
    managementService = newManagementService;
  }

  public void setExpirationTime(LocalDateTime newExpirationTime) {
    expirationTime = newExpirationTime;
  }

  public void setBidStatus(BidStatus newStatus) {
    bidStatus = newStatus;
  }

  public void setPoundsRemaining(Integer newAmount) {
    poundsRemaining = newAmount;
  }


  /* ======================================= Member Functions ======================================= */


  @Override
  public String toString() {
    return "(" + id + ") " + getAlmondVariety();
  }
}
