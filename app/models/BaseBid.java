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
  private AlmondVariety almondVariety;

  @Constraints.Required
  private Integer almondPounds;

  @Constraints.Required
  private String pricePerPound;

  @Column(columnDefinition = "TEXT")
  private String comment = "";

  private String managementService;

  private LocalDateTime expirationTime;

  private BidStatus bidStatus = BidStatus.OPEN;


  /* ======================================= Attribute Accessors ======================================= */


  public AlmondVariety getAlmondVariety() {
    return almondVariety;
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

  public BidStatus getBidStatus() {
    return bidStatus;
  }

  public boolean bidCurrentlyOpen() {
    return bidStatus == BidStatus.OPEN;
  }


  /* ======================================= Attribute Setters ======================================= */


  public void setAlmondVariety(AlmondVariety newVariety) {
    almondVariety = newVariety;
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


  /* ======================================= Member Functions ======================================= */


  @Override
  public String toString() {
    return "(" + id + ") " + getAlmondVariety();
  }
}
