package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Column;

import models.BaseBidResponse.ResponseStatus;
import models.interfaces.PrettyString;

import play.data.validation.Constraints;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("HANDLER_SELLER")
public class HandlerSeller extends BaseSeller implements PrettyString {

  @ManyToOne
  private Trader trader;

  @OneToMany(cascade = CascadeType.ALL)
  @JsonIgnore // Annotation here because no explicit getter
  public List<TraderBidResponse> bidResponses = new ArrayList<>();

  @ManyToMany(cascade = CascadeType.ALL, mappedBy = "handlerSellers")
  @JsonIgnore // Annotation here because no explicit getter
  public List<TraderBid> traderBids = new ArrayList<>();

  @Column(name = "seller_company_name")
  private String companyName;


  /* ==================================== Static Functions ==================================== */


  public static Finder<Long, HandlerSeller> find = new Finder<>(HandlerSeller.class);


  public HandlerSeller(Trader trader, String firstName, String lastName,
      String companyName, EmailAddress emailAddress, PhoneNumber phoneNumber) {
    super();

    setFirstName(firstName);
    setLastName(lastName);
    this.companyName = companyName;
    this.trader = trader;
    setEmailAddress(emailAddress);
    setPhoneNumber(phoneNumber);
  }

  public Trader getTrader() {
    return trader;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setTrader(Trader trader) {
    this.trader = trader;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
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
  public List<TraderBidResponse> getBidResponses() {
    return bidResponses;
  }

  private List<TraderBid> getBidsWithResponse(ResponseStatus response) {
    List<TraderBid> matchedBids = new ArrayList<>();
    for (TraderBidResponse bidResponse : getBidResponses()) {
      if (bidResponse.getResponseStatus().equals(response)) {
        matchedBids.add(bidResponse.getBid());
      }
    }
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

    builder.append(getEmailAddress() + ", ");

    builder.append("] [ ");

    builder.append(" ]\n");

    return builder.toString();
  }
}
