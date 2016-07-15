package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import play.data.validation.Constraints;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.NoSuchElementException;
import java.time.LocalDateTime;

import models.BaseBidResponse.ResponseStatus;
import models.Almond.AlmondVariety;
import models.interfaces.PrettyString;
import play.Logger;

import services.bid_management.BidManagementService;

/** ============================================ TODO ======================================================
 * Add any other fields & getters/setters that we need for TraderBid (e.g. responses)
 * Add functionality (e.g. accepting/rejecting a bid)
 */

@Entity
public class TraderBid extends BaseBid implements PrettyString {


/* ======================================= Attributes ======================================= */


  @ManyToOne 
  @Constraints.Required
  private Trader trader;

  @OneToMany(cascade = CascadeType.ALL)
  @Constraints.Required
  private Set<TraderBidResponse> bidResponses = new HashSet<>();

  @ManyToMany(cascade = CascadeType.ALL) 
  @Constraints.Required
  private List<HandlerSeller> handlerSellers = new ArrayList<>();


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
  }

  /* ======================================= Attribute Accessors ======================================= */


  public Trader getTrader() {
    return trader;
  }

  @JsonIgnore
  public Set<TraderBidResponse> getBidResponses() {
    return bidResponses;
  }

  @JsonIgnore
  public List<HandlerSeller> getAllHandlerSellers() {
    return handlerSellers;
  }


  /* ======================================= Member Functions ======================================= */

  /* TODO: Fix once BidManagementService is branched out for Traders and Handlers */
  public void closeBid(BidStatus status) {
    setBidStatus(BidStatus.REJECTED);
    //BidManagementService.removeBidManagementService(this);
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
      
    
    // BidManagementService managementService
    //     = BidManagementService.getBidManagementService(this);

    // if (managementService != null) {
    //   BidResponseResult bidResponseResult = managementService.accept(pounds, growerId);
    //   if (!bidResponseResult.isValid()) {
    //     return bidResponseResult;
    //   }
    // } 
    else {
      // TODO: Determine whether to log error. 
      // Logger.error("managementService returned null for HandlerBidID: " + getId());
    }

    return setHandlerSellerReponseAccept(handlerSellerId, pounds);
  }

  /* TODO: Fix once BidManagementService is branched out for Traders and Handlers */
  public BidResponseResult growerRejectBid(Long handlerSellerId) {
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
    

    // BidManagementService managementService
    //     = BidManagementService.getBidManagementService(this);

    // if (managementService != null) {
    //   BidResponseResult bidResponseResult = managementService.reject(handlerSellerId);
    //   if (!bidResponseResult.isValid()) {
    //     return bidResponseResult;
    //   }
    // } 
    else {
      // TODO: Determine whether to log error. 
      // Logger.error("managementService returned null for HandlerBidID: " + getId());
    }

    return setHandlerSellerResponseForBid(handlerSellerId, ResponseStatus.REJECTED);
  }

  public BidResponseResult growerRequestCall(Long handlerSellerId) {
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