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
import services.messaging.bid.HandlerBidSendGridMessageService;
import services.messaging.bid.TwilioMessageService;
import services.impl.EbeanGrowerService;
import services.GrowerService;

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

  private static final HandlerBidSendGridMessageService sendGridService = new HandlerBidSendGridMessageService();

  private static final TwilioMessageService smsService = new TwilioMessageService();

  private static final GrowerService growerService = new EbeanGrowerService();


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
      if (getPoundsRemaining().equals(getAlmondPounds())) {
        setBidStatus(BidStatus.REJECTED);
      } else {
        setBidStatus(BidStatus.PARTIAL);
      }

      HandlerBidManagementService.removeBidManagementService(this);
      save();

    } else {
      // TODO: Determine whether to log error.
      Logger.error("management service does not exist for this bid");
      return;
    }  
  }

  public BidResponseResult approve(long growerId) {

    if (getManagementService().equals("services.bid_management.HandlerFCFSService")) {
      return BidResponseResult.getInvalidResult("Cannot approve bid in FCFS service.");
    }

    HandlerBidResponse response = getBidResponse(growerId);

    if (response == null) {
      Logger.error("Response returned null for growerId: " + growerId + " and handlerID: " + getId());
      return BidResponseResult.getInvalidResult("Cannot approve bid."); // TODO: What to tell grower when this inexplicable error happens.
    }

    response.refresh();
    if (response.getResponseStatus() != ResponseStatus.PENDING) {
      return BidResponseResult.getInvalidResult("Cannot approve bid because response status is not pending.");
    }

    long pounds = response.getPoundsAccepted();


    HandlerBidManagementService managementService
        = HandlerBidManagementService.getBidManagementService(this);

    if (managementService != null) {
      BidResponseResult bidResponseResult = managementService.approve(pounds, growerId);
      if (!bidResponseResult.isValid()) {
        /* managementService is NOT STFC */
        return bidResponseResult;
      }
    }
    
  if (getPoundsRemaining() < pounds) {
      return BidResponseResult.getInvalidResult("Cannot approve bid for :" 
        + pounds + "lbs. Only " + getPoundsRemaining() + "lbs remaining.");
    }

    setPoundsRemaining(getPoundsRemaining() - (int)pounds);
    response.setResponseStatus(ResponseStatus.ACCEPTED);


    if (getPoundsRemaining() == 0) {
      setBidStatus(BidStatus.ACCEPTED);
    } else if (managementService == null) {
      /* timer is up */
      setBidStatus(BidStatus.PARTIAL); 
    }

    sendApproved(growerId, pounds);
    save();

    return BidResponseResult.getValidResult();
  }

  public BidResponseResult disapprove(long growerId) {

    if (getManagementService().equals("services.bid_management.HandlerFCFSService")) {
      return BidResponseResult.getInvalidResult("Cannot approve bid in FCFS service.");
    }

    HandlerBidResponse response = getBidResponse(growerId);

    if (response == null) {
      Logger.error("Response returned null for growerId: " + growerId + " and handlerID: " + getId());
      return BidResponseResult.getInvalidResult("Cannot approve bid."); // TODO: What to tell grower when this inexplicable error happens.
    }

    response.refresh();
    if (response.getResponseStatus() != ResponseStatus.PENDING) {
      return BidResponseResult.getInvalidResult("Cannot disapprove bid because response status is not pending.");
    }

    HandlerBidManagementService managementService
        = HandlerBidManagementService.getBidManagementService(this);

    if (managementService != null) {
      BidResponseResult bidResponseResult = managementService.disapprove(growerId);
      if (!bidResponseResult.isValid()) {
        /* managementService is NOT STFC */
        return bidResponseResult;
      }
    }

    response.setResponseStatus(ResponseStatus.DISAPPROVED);
    sendDisapproved(growerId);

    if ((managementService == null) && (getPendingGrowers().size() == 0)) {
      /* timer is up and all growers have been approved/disapproved */
      if (getPoundsRemaining() == getAlmondPounds()) {
        setBidStatus(BidStatus.REJECTED);
      } else if (getPoundsRemaining() > 0) {
        setBidStatus(BidStatus.PARTIAL);
      }
    }

    save();

    return BidResponseResult.getValidResult();
}

  public void addGrowers(List<Grower> addedGrowers) {
    List<Long> addedIds = new ArrayList<>();
    for(Grower grower : addedGrowers) {
      if(growers.contains(grower)) {
        Logger.error("ERROR ERROR======");
        return;
        //repeat, log error/return false?
      } 
      addedIds.add(grower.getId());
    }

    growers.addAll(addedGrowers);
    HandlerBidManagementService managementService 
      = HandlerBidManagementService.getBidManagementService(this);

    if(managementService == null) {
      Logger.error("management service does not exist for this bid");
      return;
    }

    bidResponses.addAll(
      addedGrowers.stream()
      .map(grower -> new HandlerBidResponse(grower))
      .collect(Collectors.toSet()));

    managementService.addGrowers(addedIds);
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

  public List<Grower> getPendingGrowers() {
    return getGrowersWithResponse(ResponseStatus.PENDING);
  }

  public List<Grower> getDisapprovedGrowers() {
    return getGrowersWithResponse(ResponseStatus.DISAPPROVED);
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

    boolean fcfs = true;

    if (getManagementService().equals("services.bid_management.HandlerFCFSService")) {
      /* Pounds remaining edited if FCFS */
      setPoundsRemaining(getPoundsRemaining() - (int)pounds);
    } else {
      /* Pounds remaining not edited unless approved for STFC */
      fcfs = false;
    }

    save();

    return setGrowerResponseAccept(growerId, pounds, fcfs);
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

  private BidResponseResult setGrowerResponseAccept(Long growerId, long poundsAccepted, boolean fcfs) {
    HandlerBidResponse response = getBidResponse(growerId);
    if (response == null) {
      Logger.error("Response returned null for growerId: " + growerId + " and HandlerBidID: " + getId());
      return BidResponseResult.getInvalidResult("Cannot accept bid."); // TODO: What to tell grower when this inexplicable error happens.

    }

    /* Set pounds accepted in response even if STFC so we can get pounds on approval */
    response.setPoundsAccepted(poundsAccepted);

    if (fcfs) {
      response.setResponseStatus(ResponseStatus.ACCEPTED);
    } else {
      response.setResponseStatus(ResponseStatus.PENDING);
    }

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

    private boolean sendApproved(long growerId, long pounds) {
    Grower grower = growerService.getById(growerId);

    String msg = "Congratulations " + grower.getFullName() + ",\n" 
      + "Your bid (ID " + getId() + ") has been approved by " + getHandler().getCompanyName()
      + ". Check your email for a receipt of this transaction.";



    return sendGridService.sendReceipt(this, growerId, pounds)
      && smsService.sendMessage(grower.getPhoneNumberString(), msg);
  }

  private boolean sendDisapproved(long growerId) {
    Grower grower = growerService.getById(growerId);

    String msg = "Sorry " + grower.getFullName() + ",\n"
      + "Your bid (ID " + getId() + ") from " + getHandler().getCompanyName() 
      + " for " + getAlmondPounds() + "lbs, " + getAlmondVariety() + ", " + getAlmondSize()
      + ", " + getPricePerPound() + "/lb has been disapproved.";

    /* TODO: Send email on disapproval as well */  
    return smsService.sendMessage(grower.getPhoneNumberString(), msg); 
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
