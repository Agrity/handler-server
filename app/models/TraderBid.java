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
import models.Almond.AlmondVariety;
import models.Batch;
import models.interfaces.PrettyString;

import services.bid_management.TraderBidManagementService;

import play.Logger;

/** ============================================ TODO ======================================================
 * Add any other fields & getters/setters that we need for TraderBid (e.g. responses)
 * Add functionality (e.g. accepting/rejecting a bid)
 */

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


  /* ===================================== Implementation ===================================== */


  public TraderBid(Trader trader, List<HandlerSeller> allHandlerSellers, AlmondVariety almondVariety,
      Integer almondPounds, String pricePerPound, String comment, String managementService,
      LocalDateTime expirationTime) {
    super();

    bidResponses =
      allHandlerSellers.stream()
      .map(grower -> new TraderBidResponse(grower))
      .collect(Collectors.toSet());

    this.trader = trader;
    this.handlerSellers = allHandlerSellers;
    setAlmondVariety(almondVariety);
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

  /* TODO: Fix once BidManagementService is branched out for Traders and Handlers */
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

    setPoundsRemaining(getPoundsRemaining() - (int)pounds);
    save();

    return setHandlerSellerReponseAccept(handlerSellerId, pounds);
  }

  /* TODO: Fix once BidManagementService is branched out for Traders and Handlers */
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

  private BidResponseResult setHandlerSellerReponseAccept(Long handlerSellerId, long poundsAccepted) {
    TraderBidResponse response = getBidResponse(handlerSellerId);
    if (response == null) {
      Logger.error("Response returned null for handlerSellerId: " + handlerSellerId + " and TraderBidID: " + getId());
      return BidResponseResult.getInvalidResult("Cannot accept bid."); // TODO: What to tell grower when this inexplicable error happens.

    }

    response.setPoundsAccepted(poundsAccepted);
    response.setResponseStatus(ResponseStatus.ACCEPTED);
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

  @Override
  /* ==== TODO ==== */
  public String toPrettyString() {
    return "(" + id + ") " + getAlmondVariety() + " [ " + getAlmondPounds() + " ] ( " + getPricePerPound() + " )\n";
  }
}
