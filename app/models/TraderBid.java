package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import play.data.validation.Constraints;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.CascadeType;

import java.util.List;
import java.util.ArrayList;

import models.interfaces.PrettyString;
import models.Almond.AlmondVariety;
import java.time.LocalDateTime;

/** ============================================ TODO ======================================================
 * Add any other fields & getters/setters that we need for TraderBid (e.g. responses)
 * Add functionality (e.g. accepting/rejecting a bid)
 */

@Entity
public class TraderBid extends BaseBid implements PrettyString {


/* ======================================= Attributes ======================================= */


  @ManyToOne 
  @Constraints.Required
  private Trader trader;

  @ManyToMany(cascade = CascadeType.ALL) 
  @Constraints.Required
  private List<Handler> handlers = new ArrayList<>();


  /* ==================================== Static Functions ==================================== */


  public static Finder<Long, TraderBid> find = new Finder<Long, TraderBid>(TraderBid.class);


  /* ===================================== Implementation ===================================== */


  public TraderBid(Trader trader, List<Handler> allHandlers, AlmondVariety almondVariety, 
      Integer almondPounds, String pricePerPound, String comment, String managementService,
      LocalDateTime expirationTime) {
    super();

    this.trader = trader;
    this.handlers = allHandlers;
    setAlmondVariety(almondVariety);
    setAlmondPounds(almondPounds);
    setPricePerPound(pricePerPound);
    setComment(comment);
    setManagementService(managementService);
    setExpirationTime(expirationTime);
  }

  /* ======================================= Attribute Accessors ======================================= */


  public Trader getTrader() {
    return trader;
  }

  @JsonIgnore
  public List<Handler> getAllHandlers() {
    return handlers;
  }


  /* ======================================= Member Functions ======================================= */


  @Override
  /* ==== TODO ==== */
  public String toPrettyString() {
    return "(" + id + ") " + getAlmondVariety() + " [ " + getAlmondPounds() + " ] ( " + getPricePerPound() + " )\n";
  }
}