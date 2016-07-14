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

import models.BaseBidResponse.ResponseStatus;
import models.interfaces.PrettyString;

import play.data.validation.Constraints;

@Entity
public class Grower extends BaseSeller implements PrettyString {

  @ManyToOne
  @Constraints.Required
  private Handler handler;

  // TODO Remove BidResponses and Bidss from Growers if possible.
  @Constraints.Required
  @OneToMany(cascade = CascadeType.ALL)
  @JsonIgnore // Annotation here because no explicit getter
  public List<HandlerBidResponse> bidResponses = new ArrayList<>();

  @Constraints.Required
  @ManyToMany(cascade = CascadeType.ALL, mappedBy = "growers")
  @JsonIgnore // Annotation here because no explicit getter
  public List<HandlerBid> handlerBids = new ArrayList<>();


  /* ==================================== Static Functions ==================================== */


  public static Finder<Long, Grower> find = new Finder<>(Grower.class);


  public Grower(Handler handler, String firstName, String lastName,
      List<EmailAddress> emailAddresses, List<PhoneNumber> phoneNumbers) {
    super();

    setFirstName(firstName);
    setLastName(lastName);
    this.handler = handler;
    setEmailAddresses(emailAddresses);
    setPhoneNumbers(phoneNumbers);
  }

  public Handler getHandler() {
    return handler;
  }

  public void setHandler(Handler handler) {
    this.handler = handler;
  }

  @JsonIgnore
  public List<HandlerBid> getAcceptedBids() {
    return getBidsWithResponse(ResponseStatus.ACCEPTED);
  }
  
  @JsonIgnore
  public List<HandlerBid> getRejectedBids() {
    return getBidsWithResponse(ResponseStatus.REJECTED);
  }

  @JsonIgnore
  public List<HandlerBid> getCallRequestedBids() {
    return getBidsWithResponse(ResponseStatus.REQUEST_CALL);
  }

  @JsonIgnore
  public List<HandlerBid> getNoResponseBids() {
    return getBidsWithResponse(ResponseStatus.NO_RESPONSE);
  }

  @JsonIgnore
  public List<HandlerBidResponse> getBidResponses() {
    return bidResponses;
  }

  private List<HandlerBid> getBidsWithResponse(ResponseStatus response) {
    List<HandlerBid> matchedBids = new ArrayList<>();
    for (HandlerBidResponse bidResponse : getBidResponses()) {
      if (bidResponse.getResponseStatus().equals(response)) {
        matchedBids.add(bidResponse.getBid());
      }
    }
    return matchedBids;
  }

  @JsonIgnore
  public List<HandlerBid> getHandlerBids() {
    return handlerBids;
  }

  public HandlerBid bidLookupByID(Long bidID) {
    for (HandlerBid handlerBid: getHandlerBids()) {
      if (handlerBid.getId().equals(bidID)) {
        return handlerBid;
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
