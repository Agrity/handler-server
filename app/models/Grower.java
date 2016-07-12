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
public class Grower extends BaseModel implements PrettyString {

  @ManyToOne
  @Constraints.Required
  private Handler handler;

  public void setHandler(Handler handler) {
    this.handler = handler;
  }

  @Constraints.Required
  private String firstName;

  @Constraints.Required
  private String lastName;

  /**
   * TODO: Change to play email format to use play-mailer plugin
   *
   * <a href="https://github.com/playframework/play-mailer/blob/master/README.adoc">Plugin Link</a>
   */
  @OneToMany(cascade = CascadeType.ALL)
  @Constraints.Required
  public List<EmailAddress> emailAddresses;

  /**
   * TODO: Change to phone number format, construct own model so that can be consistant.
   */
  @OneToMany(cascade = CascadeType.ALL)
  @Constraints.Required
  public List<PhoneNumber> phoneNumbers;


  // TODO Remove BidResponses and Bidss from Growers if possible.
  @Constraints.Required
  @OneToMany(cascade = CascadeType.ALL)
  @JsonIgnore // Annotation here because no explicit getter
  public List<BidResponse> bidResponses = new ArrayList<>();

  @Constraints.Required
  @ManyToMany(cascade = CascadeType.ALL, mappedBy = "growers")
  @JsonIgnore // Annotation here because no explicit getter
  public List<HandlerBid> handlerBids = new ArrayList<>();


  /* ==================================== Static Functions ==================================== */


  public static Finder<Long, Grower> find = new Finder<>(Grower.class);

  public static Grower createGrower(Handler handler, String firstName, String lastName) {
    Grower grower = new Grower(handler, firstName, lastName, new ArrayList<>(), new ArrayList<>());
    grower.save();
    return grower;
  }
  


  /* ========== Member Functions ========== */

  public Grower(Handler handler, String firstName, String lastName) {
    super();

    this.handler = handler;
    this.firstName = firstName;
    this.lastName = lastName;

    this.emailAddresses = new ArrayList<>();
    this.phoneNumbers = new ArrayList<>();
  }

  public Grower(Handler handler, String firstName, String lastName,
      List<EmailAddress> emailAddresses, List<PhoneNumber> phoneNumbers) {
    super();

    this.firstName = firstName;
    this.lastName = lastName;
    this.handler = handler;
    this.emailAddresses = emailAddresses;
    this.phoneNumbers = phoneNumbers;
  }

  public Handler getHandler() {
    return handler;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }


  @JsonIgnore
  public String getFullName() {
    return firstName + " " + lastName;
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

  private List<HandlerBid> getBidsWithResponse(ResponseStatus response) {
    List<HandlerBid> matchedBids = new ArrayList<>();
    for (BidResponse growerBidResponse : bidResponses) {
      if (growerBidResponse.getResponseStatus().equals(response)) {
        matchedBids.add(growerBidResponse.getBid());
      }
    }
    return matchedBids;
  }

  @JsonIgnore
  public List<EmailAddress> getEmailAddresses() {
    return emailAddresses;

  }

  public List<String> getEmailAddressStrings() {
    return emailAddresses
      .stream()
      .map(EmailAddress::getEmailAddress)
      .collect(Collectors.toList());
  }

  @JsonIgnore
  public List<PhoneNumber> getPhoneNums() {
    return phoneNumbers;
  }

  public List<String> getPhoneNumsStrings() {
    return phoneNumbers
      .stream()
      .map(PhoneNumber::getPhoneNumber)
      .collect(Collectors.toList());
  }

  public void setFirstName(String name) {
    firstName = name;
  }

  public void setLastName(String name) {
    lastName = name;
  }

  public void setPhoneNumbers(List<PhoneNumber> numbers) {
    phoneNumbers = numbers;
  }

  public void setEmailAddresses(List<EmailAddress> emails) {
    emailAddresses = emails;
  }

  public HandlerBid bidLookupByID(Long bidID) {
    for (HandlerBid handlerBid: handlerBids) {
      if (handlerBid.getId().equals(bidID)) {
        return handlerBid;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    // TODO
    return "(" + id + ") " + getFullName() + " [" + emailAddresses.toString() + "]";
  }

  public String toPrettyString() {
    StringBuilder builder = new StringBuilder()
        .append("(" + id + ") " + getFullName());

    builder.append(" [ ");
    for (EmailAddress addr : emailAddresses) {
      builder.append(addr + ", ");
    }

    builder.append("] [ ");

    builder.append(" ]\n");

    return builder.toString();
  }

  /* ========== Static Functions ========== */
}
