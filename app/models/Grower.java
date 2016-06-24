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

import models.OfferResponse.ResponseStatus;
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
  //@Constraints.Required
  public List<String> phoneNumbers;


  // TODO Remove OfferResponses and Offers from Growers if possible.
  @Constraints.Required
  @OneToMany(cascade = CascadeType.ALL)
  @JsonIgnore // Annotation here because no explicit getter
  public List<OfferResponse> offerResponses = new ArrayList<>();

  @Constraints.Required
  @ManyToMany(cascade = CascadeType.ALL, mappedBy = "growers")
  @JsonIgnore // Annotation here because no explicit getter
  public List<Offer> offers = new ArrayList<>();


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
    //this.phoneNumbers = new ArrayList<>();
  }

  public Grower(Handler handler, String firstName, String lastName,
      List<EmailAddress> emailAddresses, List<String> phoneNumbers) {
    super();

    this.firstName = firstName;
    this.lastName = lastName;
    this.handler = handler;
    this.emailAddresses = emailAddresses;

    // TODO Fix When Phonenumbers Functional
     this.phoneNumbers = phoneNumbers;
    //this.phoneNumbers = new ArrayList<>();
  }

  public Handler getHandler() {
    return handler;
  }

  public String getFullName() {
    return firstName + " " + lastName;
  }

  public String getPrefferedContact() {
    return emailAddresses.isEmpty() ? null : emailAddresses.get(0).toString();
  }

  @JsonIgnore
  public List<Offer> getAcceptedOffers() {
    return getOffersWithResponse(ResponseStatus.ACCEPTED);
  }
  
  @JsonIgnore
  public List<Offer> getRejectedOffers() {
    return getOffersWithResponse(ResponseStatus.REJECTED);
  }

  @JsonIgnore
  public List<Offer> getCallRequestedOffers() {
    return getOffersWithResponse(ResponseStatus.REQUEST_CALL);
  }

  @JsonIgnore
  public List<Offer> getNoResponseOffers() {
    return getOffersWithResponse(ResponseStatus.NO_RESPONSE);
  }

  private List<Offer> getOffersWithResponse(ResponseStatus response) {
    List<Offer> matchedOffers = new ArrayList<>();
    for (OfferResponse growerOfferResponse : offerResponses) {
      if (growerOfferResponse.getResponseStatus().equals(response)) {
        matchedOffers.add(growerOfferResponse.getOffer());
      }
    }
    return matchedOffers;
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

  public List<String> getPhoneNumbers() {
    return phoneNumbers;
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

    // TODO Uncomment after transitioned Phone Numbers into their own model.
    //for (String number : phoneNumbers) {
    //  builder.append(number + ", ");
    //}
    builder.append(" ]\n");

    return builder.toString();
  }

  /* ========== Static Functions ========== */
}
