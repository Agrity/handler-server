package models;

import javax.persistence.MappedSuperclass;
import play.data.validation.Constraints;

import models.Almond.AlmondVariety;

@MappedSuperclass
public abstract class BaseBid extends BaseModel {

	@Constraints.Required
  private AlmondVariety almondVariety;

  public AlmondVariety getAlmondVariety() {
    return almondVariety;
  }

 public void setAlmondVariety(AlmondVariety newVariety) {
    almondVariety = newVariety;
  }

}