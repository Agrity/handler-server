package models;

import javax.persistence.MappedSuperclass;
import play.data.validation.Constraints;

import models.Almond.AlmondVariety;

@MappedSuperclass
public abstract class BaseBid extends BaseModel {

/* ======================================= Attributes ======================================= */

	@Constraints.Required
  private AlmondVariety almondVariety;

  @Constraints.Required
  private Integer almondPounds;

/* ======================================= Getters ======================================= */

  public AlmondVariety getAlmondVariety() {
    return almondVariety;
  }

  public Integer getAlmondPounds() {
    return almondPounds;
  }

/* ======================================= Setters ======================================= */

  public void setAlmondVariety(AlmondVariety newVariety) {
    almondVariety = newVariety;
  }

  public void setAlmondPounds(Integer newLbs) {
    almondPounds = newLbs;
  }

}