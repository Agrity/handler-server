package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.MappedSuperclass;
import play.data.validation.Constraints;
import java.text.NumberFormat;
import javax.persistence.Column;

import models.Almond.AlmondVariety;

@MappedSuperclass
public abstract class BaseBid extends BaseModel {

/* ======================================= Attributes ======================================= */

	@Constraints.Required
  private AlmondVariety almondVariety;

  @Constraints.Required
  private Integer almondPounds;

  @Constraints.Required
  private String pricePerPound;

  @Column(columnDefinition = "TEXT")
  private String comment = "";

/* ======================================= Getters ======================================= */

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

/* ======================================= Setters ======================================= */

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

}