package models;

import com.avaje.ebean.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import javax.money.MonetaryAmount;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.format.Formats;
import play.data.validation.Constraints;

@Entity
public class Offer extends Model {

  @Id
  @Constraints.Min(10)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @ManyToOne(cascade = CascadeType.ALL)
  @Constraints.Required
  public Handler handler;

  @Constraints.Required
  public List<Grower> allGrowers = new ArrayList<Grower>();

  @Constraints.Required
  public Almond.AlmondVariety variety;
  
  /* ============== TODO Almond Size Here =============== */

  @Constraints.Required
  public Integer quantity;

  // TODO Figure out Why this can't use reflection
  //@Constraints.Required
  //public MonetaryAmount price;

  @Formats.DateTime(pattern="dd/MM/yyyy")
  public Date paymentDate;

  public String comments = "";

  @Constraints.Required
  public List<Grower> acceptedGrowers = new ArrayList<Grower>();

  @Constraints.Required
  public List<Grower> rejectedGrowers = new ArrayList<Grower>();

  @Constraints.Required
  public List<Grower> noResponseGrowers = new ArrayList<Grower>();

  public static Finder<Long, Offer> find = new Finder<Long, Offer>(Offer.class);

  public Offer(String firstName, String lastName) {

  }

  @Override
  public String toString() {
    // TODO
    return null;
  }
}
