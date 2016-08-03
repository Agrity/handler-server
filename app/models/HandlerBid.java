package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.Almond.AlmondVariety;
import models.BaseBidResponse.ResponseStatus;
import models.BaseBid.BidStatus;
import models.interfaces.PrettyString;

import play.Logger;
import play.data.validation.Constraints;

import services.bid_management.HandlerBidManagementService;

import services.DateService;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("HANDLER_BID")
public class HandlerBid extends BaseBid implements PrettyString {


  /* ======================================= Attributes ======================================= */


  @ManyToOne
  @Constraints.Required
  private Handler handler;

  @OneToMany(cascade = CascadeType.ALL)
  @Constraints.Required
  private Set<HandlerBidResponse> bidResponses = new HashSet<>();

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name="HANDLER_BIDS_GROWERS")
  @Constraints.Required
  private List<Grower> growers = new ArrayList<>();

  private LocalDate startPaymentDate;

  private LocalDate endPaymentDate;


  /* ==================================== Static Functions ==================================== */


  public static Finder<Long, HandlerBid> find = new Finder<Long, HandlerBid>(HandlerBid.class);


  /* ===================================== Implementation ===================================== */



  public HandlerBid(Handler handler, List<Grower> allGrowers, AlmondVariety almondVariety,
      String almondSize, Integer almondPounds, String pricePerPound, LocalDate startPaymentDate,
      LocalDate endPaymentDate, String comment, String managementService, LocalDateTime expirationTime) {
    super();

    bidResponses =
      allGrowers.stream()
      .map(grower -> new HandlerBidResponse(grower))
      .collect(Collectors.toSet());

    this.handler = handler;
    this.growers = allGrowers;
    setAlmondVariety(almondVariety);
    setAlmondSize(almondSize);
    setAlmondPounds(almondPounds);
    setPricePerPound(pricePerPound);
    this.startPaymentDate = startPaymentDate;
    this.endPaymentDate = endPaymentDate;
    setComment(comment);
    setManagementService(managementService);
    setExpirationTime(expirationTime);
    setPoundsRemaining(almondPounds);
  }


  /* === Attribute Accessors === */


  public Handler getHandler() {
    return handler;
  }

  @JsonIgnore
  public List<Grower> getAllGrowers() {
    return growers;
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

  public Set<HandlerBidResponse> getBidResponses() {
    return bidResponses;
  }


  /* === Setter Functions === */


  public void setStartPaymentDate(LocalDate newStart) {
    startPaymentDate = newStart;
  }

  public void setEndPaymentDate(LocalDate newEnd) {
    endPaymentDate = newEnd;
  }


  /* === Member Functions === */


  public void closeBid(BidStatus status) {
    setBidStatus(status);
    HandlerBidManagementService.removeBidManagementService(this);
    save();
  }

  public void manualCloseBid() {
    HandlerBidManagementService managementService
        = HandlerBidManagementService.getBidManagementService(this);

    if (managementService != null) {
      managementService.close();
    }
    else {
      // TODO: Determine whether to log error.
      // Logger.error("managementService returned null for HandlerBidID: " + getId());
    }
    
    HandlerBidManagementService.removeBidManagementService(this);
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
    return getBidResponses().stream()
      .filter(bidResponse -> bidResponse.getResponseStatus().equals(response))
      .map(bidResponse -> bidResponse.getGrower())
      .collect(Collectors.toList());
  }

  @JsonIgnore
  public List<ResponseStatus> getAllBidResponseStatuses() {
    return getBidResponses().stream()
      .map(bidResponse -> bidResponse.getResponseStatus())
      .collect(Collectors.toList());
  }

  public HandlerBidResponse getBidResponse(long growerId) {
    try {
      return getBidResponses().stream()
        .filter(bidResponse -> bidResponse.getGrower().getId().equals(growerId))
        .findFirst()
        .get();
    } catch(NoSuchElementException e) {
      return null;
    }
  }

  public BidResponseResult growerAcceptBid(Long growerId, long pounds) {
    if (!bidCurrentlyOpen()) {
      return BidResponseResult.getInvalidResult("Cannot accept bid becase it has already closed.");
    }

    HandlerBidResponse response = getBidResponse(growerId);

    if (response == null) {
      Logger.error("Response returned null for growerId: " + growerId + " and HandlerBidID: " + getId());
      return BidResponseResult.getInvalidResult("Cannot accept bid."); // TODO: What to tell grower when this inexplicable error happens.
    }

    response.refresh();
    if (response.getResponseStatus() != ResponseStatus.NO_RESPONSE
        && response.getResponseStatus() != ResponseStatus.REQUEST_CALL) {
      return BidResponseResult.getInvalidResult("Cannot accept bid because grower has already responded.");
    }


    HandlerBidManagementService managementService
        = HandlerBidManagementService.getBidManagementService(this);

    if (managementService != null) {
      BidResponseResult bidResponseResult = managementService.accept(pounds, growerId);
      if (!bidResponseResult.isValid()) {
        return bidResponseResult;
      }
    }
    else {
      // TODO: Determine whether to log error.
      // Logger.error("managementService returned null for HandlerBidID: " + getId());
    }

    setPoundsRemaining(getPoundsRemaining() - (int)pounds);
    save();

    return setGrowerResponseAccept(growerId, pounds);
  }

  public BidResponseResult growerRejectBid(Long growerId) {
    if (!bidCurrentlyOpen()) {
      return BidResponseResult.getInvalidResult("There is no need to reject the bid because it has closed.");
    }

    HandlerBidResponse response = getBidResponse(growerId);

    if (response == null) {
      Logger.error("Response returned null for growerId: " + growerId + " and HandlerBidID: " + getId());
      return BidResponseResult.getInvalidResult("Cannot reject the bid."); // TODO: What to tell grower when this inexplicable error happens.
    }

    response.refresh();
    if (response.getResponseStatus() != ResponseStatus.NO_RESPONSE
        && response.getResponseStatus() != ResponseStatus.REQUEST_CALL) {
      return BidResponseResult.getInvalidResult("Cannot accept bid because grower has already responded.");
    }


    HandlerBidManagementService managementService
        = HandlerBidManagementService.getBidManagementService(this);

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
    if (!bidCurrentlyOpen()) {
      return BidResponseResult.getInvalidResult("Can not request call because the bid has already closed.");
    }

    return setGrowerResponseForBid(growerId, ResponseStatus.REQUEST_CALL);
  }

  private BidResponseResult setGrowerResponseAccept(Long growerId, long poundsAccepted) {
    HandlerBidResponse response = getBidResponse(growerId);
    if (response == null) {
      Logger.error("Response returned null for growerId: " + growerId + " and HandlerBidID: " + getId());
      return BidResponseResult.getInvalidResult("Cannot accept bid."); // TODO: What to tell grower when this inexplicable error happens.

    }

    response.setPoundsAccepted(poundsAccepted);
    response.setResponseStatus(ResponseStatus.ACCEPTED);
    response.save();

    return BidResponseResult.getValidResult();
  }

  private BidResponseResult setGrowerResponseForBid(Long growerId, ResponseStatus growerResponse) {
    HandlerBidResponse response = getBidResponse(growerId);
    if (response == null) {
      Logger.error("Response returned null for growerId: " + growerId + " and HandlerBidID: " + getId());
      return BidResponseResult.getInvalidResult("Cannot accept bid."); // TODO: What to tell grower when this inexplicable error happens.

    }

    response.setResponseStatus(growerResponse);
    response.save();

    return BidResponseResult.getValidResult();
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
