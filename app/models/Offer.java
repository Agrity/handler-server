package models;

import com.avaje.ebean.Model;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import models.OfferResponse.ResponseStatus;
import models.interfaces.PrettyString;
import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;
import services.GrowerService;

@Entity
public class Offer extends Model implements PrettyString {


  /* ======================================= Attributes ======================================= */


  @Id
  @Constraints.Min(10)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @Constraints.Required
  private Handler handler;

  public Handler getHandler() {
    return handler;
  }

  @OneToMany(cascade = CascadeType.ALL)
  @Constraints.Required
  private Set<OfferResponse> offerResponses = new HashSet<>();
  
  @OneToMany
  @Constraints.Required
  private Set<Grower> growers = new HashSet<>();

  // TODO Figure out Why this can't use reflection
  //@Constraints.Required
  //public MonetaryAmount price;
  
  @Constraints.Required
  private AlmondVariety almondVariety;

  /* === TODO Almond Size Here === */

  @Constraints.Required
  private Integer almondPounds;

  @Constraints.Required
  private String pricePerPound;

  public String getPricePerPound() {
    return pricePerPound;
  }
  
  // TODO Change to Java 8 Date and Time
  @Formats.DateTime(pattern = "dd/MM/yyyy")
  private LocalDate paymentDate;

  @Column(columnDefinition = "TEXT")
  private String comment = "";


  public static Finder<Long, Offer> find = new Finder<Long, Offer>(Offer.class);
  


  /* ===================================== Implementation ===================================== */

  

  public Offer(Handler handler, List<Grower> allGrowers, AlmondVariety almondVariety,
      Integer almondPounds, String pricePerPound, LocalDate paymentDate, String comment) {
    this.handler = handler;


    offerResponses =
        allGrowers.stream()
            .map(grower -> new OfferResponse(grower))
            .collect(Collectors.toSet());

    this.almondVariety = almondVariety;
    this.almondPounds = almondPounds;
    this.pricePerPound = pricePerPound;
    this.paymentDate = paymentDate;
    this.comment = comment;
  }


  /* === Attribute Accessors === */


  public Long getId() {
    return id;
  };

  public List<Grower> getAllGrowers() {
    return new ArrayList<>(growers);
  }

  public AlmondVariety getAlmondVariety() {
    return almondVariety;
  }
  

  public Integer getAlmondPounds() {
    return almondPounds;
  }

  public String getAlmondPoundsString() {
    return NumberFormat.getIntegerInstance().format(almondPounds);
  }


  public LocalDate getPaymentDate() {
    return paymentDate;
  }

  public String getComment() {
    return comment;
  }


  /* === Member Functions === */

  
  public List<Grower> getAcceptedGrowers() {
    return getGrowersWithResponse(ResponseStatus.ACCEPTED);
  }

  public List<Grower> getRejectedGrowers() {
    return getGrowersWithResponse(ResponseStatus.REJECTED);
  }

  public List<Grower> getNoResponseGrowers() {
    return getGrowersWithResponse(ResponseStatus.NO_RESPONSE);
  }

  public List<Grower> getCallRequestedGrowers() {
    return getGrowersWithResponse(ResponseStatus.REQUEST_CALL);
  }

  private List<Grower> getGrowersWithResponse(ResponseStatus response) {
    return offerResponses.stream()
        .filter(offerResponse -> offerResponse.getResponseStatus().equals(response))
        .map(offerResponse -> offerResponse.getGrower())
        .collect(Collectors.toList());
  }
  

  public List<ResponseStatus> getAllOfferResponseStatuses() {
    return offerResponses.stream()
        .map(offerResponse -> offerResponse.getResponseStatus())
        .collect(Collectors.toList());
  }

  private OfferResponse getGrowerOfferResponse(Grower grower) {
    return offerResponses.stream()
        .filter(offerResponse -> offerResponse.getGrower().equals(grower))
        .findFirst()
        .get();
  }


  public boolean growerAcceptOffer(Long growerId) {
    return setGrowerResponseForOffer(growerId, ResponseStatus.ACCEPTED);
  }

  public boolean growerRejectOffer(Long growerId) {
    return setGrowerResponseForOffer(growerId, ResponseStatus.REJECTED);
  }

  public boolean growerRequestCall(Long growerId) {
    return setGrowerResponseForOffer(growerId, ResponseStatus.REQUEST_CALL);
  }

  private boolean setGrowerResponseForOffer(Long growerId, ResponseStatus growerResponse) {
    Grower grower = GrowerService.getGrower(growerId);
    if (grower == null) {
      Logger.error("Grower with id [" + growerId + "] could not be found to respond to offer");
      return false;
    }

    OfferResponse growerOfferResponse = getGrowerOfferResponse(grower);

    if (growerOfferResponse == null) {
      Logger.error("Grower Response with grower id [" + growerId + "] could not be found to respond to offer");
      return false;

    }

    growerOfferResponse.setResponseStatus(growerResponse);
    growerOfferResponse.save();

    return true;
  }

  @Override
  public String toString() {
    return "(" + id + ") " + almondVariety;
  }

  @Override
  public String toPrettyString() {
    return "(" + id + ") " + almondVariety + " [ " + almondPounds + " ] ( " + pricePerPound + " )\n"
      + "Growers: " + getAllGrowers() + "\n"
      + "\tAccepted: " + getAcceptedGrowers() + "\n"
      + "\tRejected: " + getRejectedGrowers() + "\n"
      + "\tRequest Call: " + getCallRequestedGrowers() + "\n"
      + "\tNo Response: " + getNoResponseGrowers() + "\n";
  }
}
