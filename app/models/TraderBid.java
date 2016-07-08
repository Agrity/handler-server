package models;

import models.interfaces.PrettyString;
import javax.persistence.Entity;

import models.Almond.AlmondVariety;

@Entity
public class TraderBid extends BaseBid implements PrettyString {

  public TraderBid(AlmondVariety almondVariety, Integer almondPounds, 
  	  String pricePerPound, String comment) {
    super();

    setAlmondVariety(almondVariety);
    setAlmondPounds(almondPounds);
    setPricePerPound(pricePerPound);
    setComment(comment);
  }

  @Override
  public String toPrettyString() {
    return "(" + id + ") " + getAlmondVariety() + " [ " + getAlmondPounds() + " ] ( " + getPricePerPound() + " )\n";
  }
}