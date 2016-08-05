package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.data.validation.Constraints;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;

import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.time.LocalDateTime;

import models.BaseBidResponse.ResponseStatus;
import models.BaseBid.BidStatus;
import models.Almond.AlmondVariety;
import models.Batch;
import models.interfaces.PrettyString;
import services.bid_management.TraderBidManagementService;

import services.messaging.bid.BatchSendGridMessageService;
import services.messaging.bid.TwilioMessageService;
import services.impl.EbeanHandlerSellerService;
import services.HandlerSellerService;

import play.Logger;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("TRADER_BID")
public class TraderBid extends BaseBid implements PrettyString {


/* ======================================= Attributes ======================================= */


  @ManyToOne
  @Constraints.Required
  private Trader trader;

  @OneToMany(cascade = CascadeType.ALL)
  @Constraints.Required
  private Set<TraderBidResponse> bidResponses = new HashSet<>();

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name="TRADER_BIDS_HANDLER_SELLERS")
  @Constraints.Required
  private List<HandlerSeller> handlerSellers = new ArrayList<>();

  @ManyToOne()
  private Batch batch;


  /* ==================================== Static Functions ==================================== */


  public static Finder<Long, TraderBid> find = new Finder<Long, TraderBid>(TraderBid.class);

  private static final BatchSendGridMessageService sendGridService = new BatchSendGridMessageService();

  private static final TwilioMessageService smsService = new TwilioMessageService();

  private static final HandlerSellerService handlerSellerService = new EbeanHandlerSellerService();


  /* ===================================== Implementation ===================================== */


  public TraderBid(Trader trader, List<HandlerSeller> allHandlerSellers, AlmondVariety almondVariety, 
      String almondSize, Integer almondPounds, String pricePerPound, String comment, String managementService,
      LocalDateTime expirationTime) {
    super();

    bidResponses =
      allHandlerSellers.stream()
      .map(grower -> new TraderBidResponse(grower))
      .collect(Collectors.toSet());

    this.trader = trader;
    this.handlerSellers = allHandlerSellers;
    setAlmondVariety(almondVariety);
    setAlmondSize(almondSize);
    setAlmondPounds(almondPounds);
    setPricePerPound(pricePerPound);
    setComment(comment);
    setManagementService(managementService);
    setExpirationTime(expirationTime);
    setPoundsRemaining(almondPounds);
  }

  public void setBatch(Batch batch) {
    this.batch = batch;
  }

  /* ======================================= Attribute Accessors ======================================= */


  public Trader getTrader() {
    return trader;
  }

  public Set<TraderBidResponse> getBidResponses() {
    return bidResponses;
  }

  @JsonIgnore
  public List<HandlerSeller> getAllHandlerSellers() {
    return handlerSellers;
  }


  /* ======================================= Member Functions ======================================= */

  public void closeBid(BidStatus status) {
    setBidStatus(status);
    TraderBidManagementService.removeBidManagementService(this);
    save();
  }

  public void manualCloseBid() {
    TraderBidManagementService managementService
        = TraderBidManagementService.getBidManagementService(this);

    if (managementService != null) {
      managementService.close();
      if (getPoundsRemaining().equals(getAlmondPounds())) {
        setBidStatus(BidStatus.REJECTED);
      } else {
        setBidStatus(BidStatus.PARTIAL);
      }

      TraderBidManagementService.removeBidManagementService(this);
      save();
      
    } else {
      // TODO: Determine whether to log error.
      Logger.error("management service does not exist for this bid");
      return;
    }
  }

  public BidResponseResult approve(long handlerSellerId) {

    if (getManagementService().equals("services.bid_management.TraderFCFSService")) {
      return BidResponseResult.getInvalidResult("Cannot approve bid in FCFS service.");
    }

    TraderBidResponse response = getBidResponse(handlerSellerId);

    if (response == null) {
      Logger.error("Response returned null for handlerSellerId: " + handlerSellerId + " and TraderBidID: " + getId());
      return BidResponseResult.getInvalidResult("Cannot approve bid."); // TODO: What to tell grower when this inexplicable error happens.
    }

    response.refresh();
    if (response.getResponseStatus() != ResponseStatus.PENDING) {
      return BidResponseResult.getInvalidResult("Cannot approve bid because response status is not pending.");
    }

    long pounds = response.getPoundsAccepted();


    TraderBidManagementService managementService
        = TraderBidManagementService.getBidManagementService(this);

    if (managementService != null) {
      BidResponseResult bidResponseResult = managementService.approve(pounds, handlerSellerId);
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
    } else {
      setBidStatus(BidStatus.PARTIAL); 
    }

    sendApproved(handlerSellerId, pounds);

    save();
    return BidResponseResult.getValidResult();
  }

  public BidResponseResult disapprove(long handlerSellerId) {

    if (getManagementService().equals("services.bid_management.TraderFCFSService")) {
      return BidResponseResult.getInvalidResult("Cannot approve bid in FCFS service.");
    }

    TraderBidResponse response = getBidResponse(handlerSellerId);

    if (response == null) {
      Logger.error("Response returned null for handlerSellerId: " + handlerSellerId + " and TraderBidID: " + getId());
      return BidResponseResult.getInvalidResult("Cannot approve bid."); // TODO: What to tell grower when this inexplicable error happens.
    }

    response.refresh();
    if (response.getResponseStatus() != ResponseStatus.PENDING) {
      return BidResponseResult.getInvalidResult("Cannot disapprove bid because response status is not pending.");
    }

    TraderBidManagementService managementService
        = TraderBidManagementService.getBidManagementService(this);

    if (managementService != null) {
      BidResponseResult bidResponseResult = managementService.disapprove(handlerSellerId);
      if (!bidResponseResult.isValid()) {
        /* managementService is NOT STFC */
        return bidResponseResult;
      }
    }

    response.setResponseStatus(ResponseStatus.DISAPPROVED);

    sendDisapproved(handlerSellerId);

    save();
    return BidResponseResult.getValidResult();
  }

  public List<HandlerSeller> getAcceptedHandlerSellers() {
    return getHandlerSellersWithResponse(ResponseStatus.ACCEPTED);
  }

  public List<HandlerSeller> getRejectedHandlerSellers() {
    return getHandlerSellersWithResponse(ResponseStatus.REJECTED);
  }

  public List<HandlerSeller> getNoResponseHandlerSellers() {
    return getHandlerSellersWithResponse(ResponseStatus.NO_RESPONSE);
  }

  public List<HandlerSeller> getCallRequestedHandlerSellers() {
    return getHandlerSellersWithResponse(ResponseStatus.REQUEST_CALL);
  }

  public List<HandlerSeller> getPendingHandlerSellers() {
    return getHandlerSellersWithResponse(ResponseStatus.PENDING);
  }

  public List<HandlerSeller> getDisapprovedHandlerSellers() {
    return getHandlerSellersWithResponse(ResponseStatus.DISAPPROVED);
  }

  private List<HandlerSeller> getHandlerSellersWithResponse(ResponseStatus response) {
    return getBidResponses().stream()
      .filter(bidResponse -> bidResponse.getResponseStatus().equals(response))
      .map(bidResponse -> bidResponse.getHandlerSeller())
      .collect(Collectors.toList());
  }

  @JsonIgnore
  public List<ResponseStatus> getAllBidResponseStatuses() {
    return getBidResponses().stream()
      .map(bidResponse -> bidResponse.getResponseStatus())
      .collect(Collectors.toList());
  }

  public TraderBidResponse getBidResponse(long handlerSellerId) {
    try {
      return getBidResponses().stream()
        .filter(bidResponse -> bidResponse.getHandlerSeller().getId().equals(handlerSellerId))
        .findFirst()
        .get();
    } catch(NoSuchElementException e) {
      return null;
    }
  }

  public BidResponseResult handlerSellerAcceptBid(Long handlerSellerId, long pounds) {
    if (!bidCurrentlyOpen()) {
      return BidResponseResult.getInvalidResult("Cannot accept bid because it has already closed.");
    }

    TraderBidResponse response = getBidResponse(handlerSellerId);

    if (response == null) {
      Logger.error("Response returned null for handlerSellerId: " + handlerSellerId + " and TraderBidID: " + getId());
      return BidResponseResult.getInvalidResult("Cannot accept bid."); // TODO: What to tell grower when this inexplicable error happens.
    }

    response.refresh();
    if (response.getResponseStatus() != ResponseStatus.NO_RESPONSE
        && response.getResponseStatus() != ResponseStatus.REQUEST_CALL) {
      return BidResponseResult.getInvalidResult("Cannot accept bid because handler has already responded.");
    }


    TraderBidManagementService managementService
        = TraderBidManagementService.getBidManagementService(this);

    if (managementService != null) {
      BidResponseResult bidResponseResult = managementService.accept(pounds, handlerSellerId);
      if (!bidResponseResult.isValid()) {
        return bidResponseResult;
      }
    }
    else {
      // TODO: Determine whether to log error.
      // Logger.error("managementService returned null for HandlerBidID: " + getId());
    }

    boolean fcfs = true;

    if (getManagementService().equals("services.bid_management.TraderFCFSService")) {
      /* Pounds remaining edited if FCFS */
      setPoundsRemaining(getPoundsRemaining() - (int)pounds);
    } else {
      /* Pounds remaining not edited unless approved for STFC */
      fcfs = false;
    }

    save();

    return setHandlerSellerReponseAccept(handlerSellerId, pounds, fcfs);

  }

  public BidResponseResult handlerSellerRejectBid(Long handlerSellerId) {
    if (!bidCurrentlyOpen()) {
      return BidResponseResult.getInvalidResult("There is no need to reject the bid because it has closed.");
    }

    TraderBidResponse response = getBidResponse(handlerSellerId);

    if (response == null) {
      Logger.error("Response returned null for handlerSellerId: " + handlerSellerId + " and TraderBidID: " + getId());
      return BidResponseResult.getInvalidResult("Cannot reject the bid."); // TODO: What to tell grower when this inexplicable error happens.
    }

    response.refresh();
    if (response.getResponseStatus() != ResponseStatus.NO_RESPONSE
        && response.getResponseStatus() != ResponseStatus.REQUEST_CALL) {
      return BidResponseResult.getInvalidResult("Cannot accept bid because handler has already responded.");
    }


    TraderBidManagementService managementService
        = TraderBidManagementService.getBidManagementService(this);

    if (managementService != null) {
      BidResponseResult bidResponseResult = managementService.reject(handlerSellerId);
      if (!bidResponseResult.isValid()) {
        return bidResponseResult;
      }
    }
    else {
      // TODO: Determine whether to log error.
      // Logger.error("managementService returned null for HandlerBidID: " + getId());
    }

    return setHandlerSellerResponseForBid(handlerSellerId, ResponseStatus.REJECTED);
  }

  public BidResponseResult handlerSellerRequestCall(Long handlerSellerId) {
    if (!bidCurrentlyOpen()) {
      return BidResponseResult.getInvalidResult("Can not request call because the bid has already closed.");
    }

    return setHandlerSellerResponseForBid(handlerSellerId, ResponseStatus.REQUEST_CALL);
  }

  private BidResponseResult setHandlerSellerReponseAccept(Long handlerSellerId, long poundsAccepted, boolean fcfs) {
    TraderBidResponse response = getBidResponse(handlerSellerId);
    if (response == null) {
      Logger.error("Response returned null for handlerSellerId: " + handlerSellerId + " and TraderBidID: " + getId());
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

  private BidResponseResult setHandlerSellerResponseForBid(Long handlerSellerId, ResponseStatus responseStatus) {
    TraderBidResponse response = getBidResponse(handlerSellerId);
    if (response == null) {
      Logger.error("Response returned null for handlerSellerId: " + handlerSellerId + " and TraderBidID: " + getId());
      return BidResponseResult.getInvalidResult("Cannot accept bid."); // TODO: What to tell grower when this inexplicable error happens.

    }

    response.setResponseStatus(responseStatus);
    response.save();

    return BidResponseResult.getValidResult();
  }

  private boolean sendApproved(long handlerSellerId, long pounds) {
    HandlerSeller handlerSeller = handlerSellerService.getById(handlerSellerId);

    String msg = "Congratulations " + handlerSeller.getFullName() + ",\n" 
      + "Your bid (ID " + getId() + ") has been approved by " + getTrader().getCompanyName()
      + ". Check your email for a receipt of this transaction.";



    return sendGridService.sendReceipt(this, handlerSellerId, pounds)
      && smsService.sendMessage(handlerSeller.getPhoneNumberString(), msg);

  }

  private boolean sendDisapproved(long handlerSellerId) {
    HandlerSeller handlerSeller = handlerSellerService.getById(handlerSellerId);

    String msg = "Sorry " + handlerSeller.getFullName() + ",\n"
      + "Your bid (ID " + getId() + ") from " + getTrader().getCompanyName() 
      + "for " + getAlmondPounds() + "mt, " + getAlmondVariety() + ", " + getAlmondVariety()
      + " has been disapproved.";

    /* TODO: Send email on disapproval as well */  
    return smsService.sendMessage(handlerSeller.getPhoneNumberString(), msg);
    
  }

  @Override
  /* ==== TODO ==== */
  public String toPrettyString() {
    return "(" + id + ") " + getAlmondVariety() + " [ " + getAlmondPounds() + " ] ( " + getPricePerPound() + " )\n";
  }
}
