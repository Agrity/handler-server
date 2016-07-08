package models;

import javax.persistence.MappedSuperclass;
import play.data.validation.Constraints;

import models.Almond.AlmondVariety;

@MappedSuperclass
public abstract class BaseBid extends BaseModel {

/* ======================================= Attributes ======================================= */

	@Constraints.Required
  private AlmondVariety almondVariety;

  // TODO Change to AlmondSize Size within Almond model.
  @Constraints.Required
  private String almondSize;

/* ======================================= Getters ======================================= */

  public AlmondVariety getAlmondVariety() {
    return almondVariety;
  }


  public String getAlmondSize() {
    return almondSize;
  }

/* ======================================= Setters ======================================= */

  public void setAlmondVariety(AlmondVariety newVariety) {
    almondVariety = newVariety;
  }

  public void setAlmondSize(String newSize) {
    almondSize = newSize;
  }

}