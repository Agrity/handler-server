package models;

import com.avaje.ebean.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//import javax.money.MonetaryAmount;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import models.Almond.AlmondVariety;
import models.interfaces.PrettyString;

import play.data.format.Formats;
import play.data.validation.Constraints;

@Entity
public class Offer extends Model implements PrettyString {

  @Id
  @Constraints.Min(10)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = DBConstants.OfferColumns.ID)
  public Long id;

  @ManyToOne(cascade = CascadeType.ALL)
  @Constraints.Required
  public Handler handler;

  @Constraints.Required
  public List<Grower> allGrowers = new ArrayList<Grower>();

  @Constraints.Required
  public Almond.AlmondVariety almondVariety;
  
  /* ============== TODO Almond Size Here =============== */

  @Constraints.Required
  public Integer almondPounds;

  // TODO Figure out Why this can't use reflection
  //@Constraints.Required
  //public MonetaryAmount price;
  

  // TODO Change to Java 8 Date and Time
  @Formats.DateTime(pattern = "dd/MM/yyyy")
  public LocalDate paymentDate;

  public String comments = "";

  @Constraints.Required
  public List<Grower> acceptedGrowers = new ArrayList<Grower>();

  @Constraints.Required
  public List<Grower> rejectedGrowers = new ArrayList<Grower>();

  @Constraints.Required
  public List<Grower> noResponseGrowers = new ArrayList<Grower>();

  public static Finder<Long, Offer> find = new Finder<Long, Offer>(Offer.class);

  public Offer(Handler handler, List<Grower> allGrowers, AlmondVariety almondVariety,
      Integer almondPounds, LocalDate paymentDate) {
    this.handler = handler;
    this.allGrowers = allGrowers;
    this.noResponseGrowers = allGrowers;
    this.almondVariety = almondVariety;
    this.almondPounds = almondPounds;
    this.paymentDate = paymentDate;
  }

  @Override
  public String toString() {
    return "(" + id + ") " + almondVariety;
  }

  public String toPrettyString() {
    return "(" + id + ") " + almondVariety + "[ " + almondPounds + "]\n";
  }
}
