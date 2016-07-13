package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.BidResponse.ResponseStatus;
import models.interfaces.PrettyString;

import play.data.validation.Constraints;

@Entity
public class HandlerSeller extends BaseSeller implements PrettyString {

  /* ===== TODO ==== 
   * finish once BidResponses are extended to 2 different types
   */

  @ManyToOne
  @Constraints.Required
  private Trader trader;

  @Constraints.Required
  @OneToMany(cascade = CascadeType.ALL)
  @JsonIgnore // Annotation here because no explicit getter
  public List<BidResponse> bidResponses = new ArrayList<>();

  @Constraints.Required
  @ManyToMany(cascade = CascadeType.ALL, mappedBy = "growers")
  @JsonIgnore // Annotation here because no explicit getter
  public List<TraderBid> traderBids = new ArrayList<>();


  /* ==================================== Static Functions ==================================== */


  public static Finder<Long, HandlerSeller> find = new Finder<>(HandlerSeller.class);


  public HandlerSeller(Trader trader, String firstName, String lastName,
      List<EmailAddress> emailAddresses, List<PhoneNumber> phoneNumbers) {
    super();

    setFirstName(firstName);
    setLastName(lastName);
    this.trader = trader;
    setEmailAddresses(emailAddresses);
    setPhoneNumbers(phoneNumbers);
  }

  public Trader getTrader() {
    return trader;
  }

  public void setTrader(Trader trader) {
    this.trader = trader;
  }

  @JsonIgnore
  public List<TraderBid> getAcceptedBids() {
    return getBidsWithResponse(ResponseStatus.ACCEPTED);
  }
  
  @JsonIgnore
  public List<TraderBid> getRejectedBids() {
    return getBidsWithResponse(ResponseStatus.REJECTED);
  }

  @JsonIgnore
  public List<TraderBid> getCallRequestedBids() {
    return getBidsWithResponse(ResponseStatus.REQUEST_CALL);
  }

  @JsonIgnore
  public List<TraderBid> getNoResponseBids() {
    return getBidsWithResponse(ResponseStatus.NO_RESPONSE);
  }

  @JsonIgnore
  public List<BidResponse> getBidResponses() {
    return bidResponses;
  }

  private List<TraderBid> getBidsWithResponse(ResponseStatus response) {
    List<TraderBid> matchedBids = new ArrayList<>();
    // for (BidResponse bidResponse : getBidResponses()) {
    //   if (bidResponse.getResponseStatus().equals(response)) {
    //     matchedBids.add(bidResponse.getBid());
    //   }
    // }
     return matchedBids;
  }

  @JsonIgnore
  public List<TraderBid> getTraderBids() {
    return traderBids;
  }

  public TraderBid bidLookupByID(Long bidID) {
    for (TraderBid traderBid: getTraderBids()) {
      if (traderBid.getId().equals(bidID)) {
        return traderBid;
      }
    }
    return null;
  }

  public String toPrettyString() {
    StringBuilder builder = new StringBuilder()
        .append("(" + id + ") " + getFullName());

    builder.append(" [ ");
    for (EmailAddress addr : getEmailAddresses()) {
      builder.append(addr + ", ");
    }

    builder.append("] [ ");

    builder.append(" ]\n");

    return builder.toString();
  }
}
