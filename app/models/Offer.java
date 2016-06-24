package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.NoSuchElementException;

//import javax.money.MonetaryAmount;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.Almond.AlmondVariety;
import models.OfferResponse.ResponseStatus;
import models.interfaces.PrettyString;

import play.data.format.Formats;
import play.data.validation.Constraints;

import services.offer_management.OfferManagementService;

@Entity
public class Offer extends BaseModel implements PrettyString {


  /* ======================================= Attributes ======================================= */

  @ManyToOne
  @Constraints.Required
  private Handler handler;

  @OneToMany(cascade = CascadeType.ALL)
    @Constraints.Required
    private Set<OfferResponse> offerResponses = new HashSet<>();

  @ManyToMany(cascade = CascadeType.ALL)
    @Constraints.Required
    private List<Grower> growers = new ArrayList<>();

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

  // TODO Change to Java 8 Date and Time
  @Formats.DateTime(pattern = "dd/MM/yyyy")
    private LocalDate paymentDate;

  @Column(columnDefinition = "TEXT")
    private String comment = "";

  private boolean offerCurrentlyOpen = true;


  /* ==================================== Static Functions ==================================== */


  public static Finder<Long, Offer> find = new Finder<Long, Offer>(Offer.class);


  /* ===================================== Implementation ===================================== */



  public Offer(Handler handler, List<Grower> allGrowers, AlmondVariety almondVariety,
      Integer almondPounds, String pricePerPound, LocalDate paymentDate, String comment) {
    super();

    this.handler = handler;

    offerResponses =
      allGrowers.stream()
      .map(grower -> new OfferResponse(grower))
      .collect(Collectors.toSet());

    this.growers = allGrowers;
    this.almondVariety = almondVariety;
    this.almondPounds = almondPounds;
    this.pricePerPound = pricePerPound;
    this.paymentDate = paymentDate;
    this.comment = comment;
  }


  /* === Attribute Accessors === */


  public Handler getHandler() {
    return handler;
  }

  @JsonIgnore
  public List<Grower> getAllGrowers() {
    return growers;
  }

  public AlmondVariety getAlmondVariety() {
    return almondVariety;
  }


  public Integer getAlmondPounds() {
    return almondPounds;
  }

  public String getPricePerPound() {
    return pricePerPound;
  }


  @JsonIgnore
  public String getAlmondPoundsString() {
    return NumberFormat.getIntegerInstance().format(almondPounds);
  }


  public LocalDate getPaymentDate() {
    return paymentDate;
  }

  public String getComment() {
    return comment;
  }

  public boolean getOfferCurrentlyOpen() {
    return offerCurrentlyOpen;
  }


  /* === Member Functions === */

  public void closeOffer() {
    offerCurrentlyOpen = false;
    OfferManagementService.removeOfferManagementService(this);
    save();
  }


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


  @JsonIgnore
  public List<ResponseStatus> getAllOfferResponseStatuses() {
    return offerResponses.stream()
      .map(offerResponse -> offerResponse.getResponseStatus())
      .collect(Collectors.toList());
  }

  public OfferResponse getGrowerOfferResponse(long growerId) {
    try {
      return offerResponses.stream()
        .filter(offerResponse -> offerResponse.getGrower().getId().equals(growerId))
        .findFirst()
        .get();
    } catch(NoSuchElementException e) {
      return null;
    }
  }


  public boolean growerAcceptOffer(Long growerId, long pounds) {
    if (!offerCurrentlyOpen) {
      // TODO Handle Late Acceptance Error
      return false;
    }


    OfferManagementService managementService
        = OfferManagementService.getOfferManagementService(this);

    if (managementService != null) {
      if (!managementService.accept(pounds, growerId)) {
      	// TODO Report Service Not Accepting Error.
      	return false;
      }
    } else {
      // TODO Possibly Log Error?
    }

    return setGrowerResponseForOffer(growerId, ResponseStatus.ACCEPTED);
  }

  public boolean growerRejectOffer(Long growerId) {
    // TODO: When this returns false the error message says could not accept. Needs to be changed.
    if (!offerCurrentlyOpen) {
      // TODO Handle Late Acceptance Error
      return false;
    }  

    OfferManagementService managementService
        = OfferManagementService.getOfferManagementService(this);

    if (managementService != null) {
      if (!managementService.reject(growerId)) { 
      // TODO Report Service Not Rejecting Error. 
      return false;
      }
    } 
    else {
      // TODO Possibly Log Error?
    }

    return setGrowerResponseForOffer(growerId, ResponseStatus.REJECTED);
  }

  public boolean growerRequestCall(Long growerId) {
    if (!offerCurrentlyOpen) {
      // TODO Handle Late Acceptance Error
      return false;
    }  

    return setGrowerResponseForOffer(growerId, ResponseStatus.REQUEST_CALL);
  }

  private boolean setGrowerResponseForOffer(Long growerId, ResponseStatus growerResponse) {
    OfferResponse growerOfferResponse = getGrowerOfferResponse(growerId);
     //TODO: Something is wrong here. 
    if (growerOfferResponse == null) {
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
