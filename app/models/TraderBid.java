package models;

import models.interfaces.PrettyString;
import javax.persistence.Entity;

import models.Almond.AlmondVariety;

/** ============================================ TODO ======================================================
 * Add any other fields & getters/setters that we need for TraderBid (e.g. responses)
 * Add functionality (e.g. accepting/rejecting a bid)
 */

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