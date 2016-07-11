package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import models.BidResponse.ResponseStatus;
import models.interfaces.PrettyString;
import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;

import services.bid_management.BidManagementService;

import services.DateService;
import java.util.Date;

@Entity
public class HandlerBid extends BaseBid implements PrettyString {

  public static enum BidStatus{
    OPEN, 
    REJECTED, 
    ACCEPTED,
    PARTIAL,
  }


  /* ======================================= Attributes ======================================= */

  @ManyToOne
  @Constraints.Required
  private Handler handler;

  @OneToMany(cascade = CascadeType.ALL)
  @Constraints.Required
  private Set<BidResponse> bidResponses = new HashSet<>();

  @ManyToMany(cascade = CascadeType.ALL)
  @Constraints.Required
  private List<Grower> growers = new ArrayList<>();

  // TODO Change to AlmondSize Size within Almond model.
  @Constraints.Required
  private String almondSize;

  private LocalDate startPaymentDate;

  private LocalDate endPaymentDate;

  @Column(columnDefinition = "TEXT")
  private String comment = "";

  private String managementService;

  private BidStatus bidCurrentlyOpen = BidStatus.OPEN;

  private LocalDateTime expirationTime;


  /* ==================================== Static Functions ==================================== */


  public static Finder<Long, HandlerBid> find = new Finder<Long, HandlerBid>(HandlerBid.class);


  /* ===================================== Implementation ===================================== */



  public HandlerBid(Handler handler, List<Grower> allGrowers, AlmondVariety almondVariety,
      String almondSize, Integer almondPounds, String pricePerPound, LocalDate startPaymentDate,
      LocalDate endPaymentDate, String comment, String managementService, LocalDateTime expirationTime) {
    super();

    this.handler = handler;

    bidResponses =
      allGrowers.stream()
      .map(grower -> new BidResponse(grower))
      .collect(Collectors.toSet());

    this.growers = allGrowers;
    setAlmondVariety(almondVariety);
    this.almondSize = almondSize;
    setAlmondPounds(almondPounds);
    setPricePerPound(pricePerPound);
    this.startPaymentDate = startPaymentDate;
    this.endPaymentDate = endPaymentDate;
    this.comment = comment;
    this.managementService = managementService;
    this.expirationTime = expirationTime;
  }


  /* === Attribute Accessors === */


  public Handler getHandler() {
    return handler;
  }

  @JsonIgnore
  public List<Grower> getAllGrowers() {
    return growers;
  }

  public String getAlmondSize() {
    return almondSize;
  }

  public LocalDate getStartPaymentDate() {
    return startPaymentDate;
  }

  public LocalDate getEndPaymentDate() {
    return endPaymentDate;
  }

  public String getStartPaymentDateAsString() {
    return DateService.dateToString(startPaymentDate);
  }

  public String getEndPaymentDateAsString() {
    return DateService.dateToString(endPaymentDate);
  }

  public String getComment() {
    return comment;
  }

  public boolean getBidCurrentlyOpen() {
    return bidCurrentlyOpen == BidStatus.OPEN;
  }

  public String getManagementService() {
    return managementService;
  }

  public LocalDateTime getExpirationTime() {
    return expirationTime;
  }

  public String getExpirationTimeAsString() {
    return expirationTime.toString();
  }


  /* === Setter Functions === */

  public void setAlmondSize(String newSize) {
    almondSize = newSize;
  }

  public void setStartPaymentDate(LocalDate newStart) {
    startPaymentDate = newStart;
  }

  public void setEndPaymentDate(LocalDate newEnd) {
    endPaymentDate = newEnd;
  }


  /* === Member Functions === */

  public void closeBid(BidStatus status) {
    bidCurrentlyOpen = BidStatus.REJECTED;
    BidManagementService.removeBidManagementService(this);
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
    return bidResponses.stream()
      .filter(BidResponse -> BidResponse.getResponseStatus().equals(response))
      .map(BidResponse -> BidResponse.getGrower())
      .collect(Collectors.toList());
  }


  @JsonIgnore
  public List<ResponseStatus> getAllbidResponsestatuses() {
    return bidResponses.stream()
      .map(BidResponse -> BidResponse.getResponseStatus())
      .collect(Collectors.toList());
  }

  public BidResponse getGrowerBidResponse(long growerId) {
    try {
      return bidResponses.stream()
        .filter(BidResponse -> BidResponse.getGrower().getId().equals(growerId))
        .findFirst()
        .get();
    } catch(NoSuchElementException e) {
      return null;
    }
  }


  public BidResponseResult growerAcceptBid(Long growerId, long pounds) {
    if (bidCurrentlyOpen != BidStatus.OPEN) {
      return BidResponseResult.getInvalidResult("Cannot accept HandlerBid because the HandlerBid has already closed.");
    }
      
    BidResponse growerResponse = getGrowerBidResponse(growerId);

    if (growerResponse == null) {
      Logger.error("growerResponse returned null for growerId: " + growerId + " and HandlerBidID: " + getId());
      return BidResponseResult.getInvalidResult("Cannot accept HandlerBid."); // TODO: What to tell grower when this inexplicable error happens.
    }
    
    growerResponse.refresh();
    if (growerResponse.getResponseStatus() != ResponseStatus.NO_RESPONSE
        && growerResponse.getResponseStatus() != ResponseStatus.REQUEST_CALL) {
      return BidResponseResult.getInvalidResult("Cannot accept HandlerBid because grower has already responded to HandlerBid.");
    }  
      
    
    BidManagementService managementService
        = BidManagementService.getBidManagementService(this);

    if (managementService != null) {
      BidResponseResult BidResponseResult = managementService.accept(pounds, growerId);
      if (!BidResponseResult.isValid()) {
        return BidResponseResult;
      }
    } 
    else {
      // TODO: Determine whether to log error. 
      // Logger.error("managementService returned null for HandlerBidID: " + getId());
    }

    return setGrowerResponseForBid(growerId, ResponseStatus.ACCEPTED);
  }

  public BidResponseResult growerRejectBid(Long growerId) {
    if (bidCurrentlyOpen != BidStatus.OPEN) {
      return BidResponseResult.getInvalidResult("There is no need to reject the HandlerBid because the HandlerBid has closed.");
    } 
    
    BidResponse growerResponse = getGrowerBidResponse(growerId);

    if (growerResponse == null) {
      Logger.error("growerResponse returned null for growerId: " + growerId + " and HandlerBidID: " + getId());
      return BidResponseResult.getInvalidResult("Cannot reject the HandlerBid."); // TODO: What to tell grower when this inexplicable error happens.
    }
    
    growerResponse.refresh();
    if (growerResponse.getResponseStatus() != ResponseStatus.NO_RESPONSE
        && growerResponse.getResponseStatus() != ResponseStatus.REQUEST_CALL) {
      return BidResponseResult.getInvalidResult("Cannot accept HandlerBid because grower has already responded to HandlerBid.");
    }
    

    BidManagementService managementService
        = BidManagementService.getBidManagementService(this);

    if (managementService != null) {
      BidResponseResult bidResponseResult = managementService.reject(growerId);
      if (!bidResponseResult.isValid()) {
        return bidResponseResult;
      }
    } 
    else {
      // TODO: Determine whether to log error. 
      // Logger.error("managementService returned null for HandlerBidID: " + getId());
    }

    return setGrowerResponseForBid(growerId, ResponseStatus.REJECTED);
  }

  public BidResponseResult growerRequestCall(Long growerId) {
    if (bidCurrentlyOpen != BidStatus.OPEN) {
      return BidResponseResult.getInvalidResult("Can not request call because the HandlerBid has already closed.");
    }  

    return setGrowerResponseForBid(growerId, ResponseStatus.REQUEST_CALL);
  }

  private BidResponseResult setGrowerResponseForBid(Long growerId, ResponseStatus growerResponse) {
    BidResponse growerBidResponse = getGrowerBidResponse(growerId);
    if (growerBidResponse == null) {
      Logger.error("growerResponse returned null for growerId: " + growerId + " and HandlerBidID: " + getId());
      return BidResponseResult.getInvalidResult("Cannot accept HandlerBid."); // TODO: What to tell grower when this inexplicable error happens.

    }
    
    growerBidResponse.setResponseStatus(growerResponse);
    growerBidResponse.save();
    
    return BidResponseResult.getValidResult(); 
  }

  @Override
  public String toString() {
    return "(" + id + ") " + getAlmondVariety();
  }

  @Override
  public String toPrettyString() {
    return "(" + id + ") " + getAlmondVariety() + " [ " + getAlmondPounds() + " ] ( " + getPricePerPound() + " )\n"
      + "Growers: " + getAllGrowers() + "\n"
      + "\tAccepted: " + getAcceptedGrowers() + "\n"
      + "\tRejected: " + getRejectedGrowers() + "\n"
      + "\tRequest Call: " + getCallRequestedGrowers() + "\n"
      + "\tNo Response: " + getNoResponseGrowers() + "\n";
  }
}
