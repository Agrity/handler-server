package models;

import com.avaje.ebean.Model;

import java.text.NumberFormat;
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
import javax.persistence.OneToMany;

import models.Almond.AlmondVariety;
import models.GrowerOfferResponse.GrowerResponse;
import models.interfaces.PrettyString;

import play.data.format.Formats;
import play.data.validation.Constraints;

import services.GrowerService;

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

  @OneToMany(cascade = CascadeType.ALL)
  @Constraints.Required
  private List<GrowerOfferResponse> growerOfferResponses = new ArrayList<>();

  public List<Grower> getAllGrowers() {
    List<Grower> growerList = new ArrayList<>();
    for (GrowerOfferResponse growerResponses : growerOfferResponses) {
      growerList.add(growerResponses.getGrower());
    }
    return growerList;
  }

  public List<GrowerResponse> getAllGrowersResponses() {
    List<GrowerResponse> growerList = new ArrayList<>();
    for (GrowerOfferResponse growerResponses : growerOfferResponses) {
      growerList.add(growerResponses.getGrowersResponse());
    }
    return growerList;
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

  public String getAlmondPoundsString() {
    return NumberFormat.getIntegerInstance().format(almondPounds);
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

  @Column(columnDefinition = "TEXT")
  private String comment = "";

  public String getComment() {
    return comment;
  }


  public List<Grower> getAcceptedGrowers() {
    return getGrowersWithResponse(GrowerResponse.ACCEPTED);
  }

  public List<Grower> getRejectedGrowers() {
    return getGrowersWithResponse(GrowerResponse.REJECTED);
  }

  public List<Grower> getNoResponseGrowers() {
    return getGrowersWithResponse(GrowerResponse.NO_RESPONSE);
  }

  public List<Grower> getCallRequestedGrowers() {
    return getGrowersWithResponse(GrowerResponse.REQUEST_CALL);
  }

  private List<Grower> getGrowersWithResponse(GrowerResponse response) {
    List<Grower> matchedGrowers = new ArrayList<>();
    for (GrowerOfferResponse growerOfferResponse : growerOfferResponses) {
      if (growerOfferResponse.getGrowersResponse().equals(response)) {
        matchedGrowers.add(growerOfferResponse.getGrower());
      }
    }
    return matchedGrowers;
  }

  private GrowerOfferResponse getGrowerOfferResponse(Grower grower) {
    for (GrowerOfferResponse growerOfferResponse : growerOfferResponses) {
      if (growerOfferResponse.getGrower().equals(grower)) {
        return growerOfferResponse;
      }
    }
    return null;
  }

  public static Finder<Long, Offer> find = new Finder<Long, Offer>(Offer.class);

  /* ========== Member Functions ========== */

  public Offer(Handler handler, List<Grower> allGrowers, AlmondVariety almondVariety,
      Integer almondPounds, String pricePerPound, LocalDate paymentDate, String comment) {
    this.handler = handler;

    // TODO Fix to Java 8 Map Syntax
    for (Grower grower : allGrowers) {
      GrowerOfferResponse growerResponse = new GrowerOfferResponse(grower);
      growerOfferResponses.add(growerResponse);
    }

    this.almondVariety = almondVariety;
    this.almondPounds = almondPounds;
    this.pricePerPound = pricePerPound;
    this.paymentDate = paymentDate;
    this.comment = comment;
  }

  public void saveGrowerResponses() {
    for (GrowerOfferResponse growerResponse : growerOfferResponses) {
      growerResponse.save();
    }
  }

  public boolean growerAcceptOffer(Long growerId) {
    return setGrowerResponseForOffer(growerId, GrowerResponse.ACCEPTED);
  }

  public boolean growerRejectOffer(Long growerId) {
    return setGrowerResponseForOffer(growerId, GrowerResponse.REJECTED);
  }

  public boolean growerRequestCall(Long growerId) {
    return setGrowerResponseForOffer(growerId, GrowerResponse.REQUEST_CALL);
  }

  // TODO Provide better error handling
  private boolean setGrowerResponseForOffer(Long growerId, GrowerResponse growerResponse) {
    Grower grower = GrowerService.getGrower(id);
    if (grower == null)
      return false;

    GrowerOfferResponse growerOfferResponse = getGrowerOfferResponse(grower);

    if (growerOfferResponse == null) {
      return false;

    }
    growerOfferResponse.setGrowersResponse(growerResponse);
    growerOfferResponse.save();

    return true;
  }

  @Override
  public String toString() {
    return "(" + id + ") " + almondVariety;
  }

  public String toPrettyString() {
    return "(" + id + ") " + almondVariety + " [ " + almondPounds + " ] ( " + pricePerPound + " )\n"
      + "Growers: " + getAllGrowers() + "\n"
      + "\tAccepted: " + getAcceptedGrowers() + "\n"
      + "\tRejected: " + getRejectedGrowers() + "\n"
      + "\tRequest Call: " + getCallRequestedGrowers() + "\n"
      + "\tNo Response: " + getNoResponseGrowers() + "\n";
  }
}
