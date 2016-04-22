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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import models.Almond.AlmondVariety;
import models.interfaces.PrettyString;

import play.data.format.Formats;
import play.data.validation.Constraints;

import services.OfferService;

@Entity
public class Offer extends Model implements PrettyString {

  @Id
  @Constraints.Min(10)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = DBConstants.OfferColumns.ID)
  private Long id;

  public Long getId() {
    return id;
  };

  @ManyToOne(cascade = CascadeType.ALL)
  @Constraints.Required
  private Handler handler;

  public Handler getHandler() {
    return handler;
  }

  @Constraints.Required
  @ManyToMany
  private List<Grower> allGrowers = new ArrayList<Grower>();

  public List<Grower> getAllGrowers() {
    return allGrowers;
  }

  @Constraints.Required
  private AlmondVariety almondVariety;

  public AlmondVariety getAlmondVariety() {
    return almondVariety;
  }
  
  /* ============== TODO Almond Size Here =============== */

  @Constraints.Required
  private Integer almondPounds;

  public Integer getAlmondPounds() {
    return almondPounds;
  }

  // TODO Figure out Why this can't use reflection
  //@Constraints.Required
  //public MonetaryAmount price;
  
  @Constraints.Required
  private String pricePerPound;

  public String getPricePerPound() {
    return pricePerPound;
  }
  
  // TODO Change to Java 8 Date and Time
  @Formats.DateTime(pattern = "dd/MM/yyyy")
  private LocalDate paymentDate;

  public LocalDate getPaymentDate() {
    return paymentDate;
  }

  public String comments = "";

  @Constraints.Required
  @ManyToMany
  private List<Grower> acceptedGrowers = new ArrayList<Grower>();

  public List<Grower> getAcceptedGrowers() {
    return acceptedGrowers;
  }

  @Constraints.Required
  @ManyToMany
  private List<Grower> rejectedGrowers = new ArrayList<Grower>();

  public List<Grower> getRejectedGrowers() {
    return rejectedGrowers;
  }

  @Constraints.Required
  @ManyToMany
  private List<Grower> noResponseGrowers = new ArrayList<Grower>();

  public List<Grower> getNoResponseGrowers() {
    return noResponseGrowers;
  }

  public static Finder<Long, Offer> find = new Finder<Long, Offer>(Offer.class);

  /* ========== Member Functions ========== */

  public Offer(Handler handler, List<Grower> allGrowers, AlmondVariety almondVariety,
      Integer almondPounds, String pricePerPound, LocalDate paymentDate) {
    this.handler = handler;
    this.allGrowers = allGrowers;
    this.noResponseGrowers = allGrowers;
    this.almondVariety = almondVariety;
    this.almondPounds = almondPounds;
    this.pricePerPound = pricePerPound;
    this.paymentDate = paymentDate;
  }

  //public boolean acceptOffer(Long id) {
  //  Grower grower = Grower.getOffer(id);

  //  if (order == null)
  //    return null;

  //  


  //}

  @Override
  public String toString() {
    return "(" + id + ") " + almondVariety;
  }

  public String toPrettyString() {
    return "(" + id + ") " + almondVariety + " [ " + almondPounds + " ] ( " + pricePerPound + " )\n"
      + "Growers: " + allGrowers.size() + "\n"
      + "\tAccepted: " + acceptedGrowers.toString() + "\n"
      + "\tRejected: " + rejectedGrowers.toString() + "\n"
      + "\tNo Response: " + noResponseGrowers.toString() + "\n";
  }
}
