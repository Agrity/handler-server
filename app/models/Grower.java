package models;

import com.avaje.ebean.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.GrowerOfferResponse.GrowerResponse;
import models.interfaces.PrettyString;

import play.data.validation.Constraints;

@Entity
public class Grower extends Model implements PrettyString {

  @Id
  @Constraints.Min(10)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = DBConstants.GrowerColumns.ID)
  private Long id;

  public Long getId() {
    return id;
  }

  @ManyToOne(cascade = CascadeType.PERSIST)
  @Constraints.Required
  private Handler handler;

  public Handler getHandler() {
    return handler;
  }

  public void setHandler(Handler handler) {
    this.handler = handler;
  }

  @Constraints.Required
  private String firstName;

  @Constraints.Required
  private String lastName;

  public String getFullName() {
    return firstName + " " + lastName;
  }

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
  @Constraints.Required
  public List<String> phoneNumbers;


  @Constraints.Required
  @OneToMany(cascade = CascadeType.ALL)
  public List<GrowerOfferResponse> offerResponses = new ArrayList<>();


  public static Finder<Long, Grower> find = new Finder<>(Grower.class);


  /* ========== Member Functions ========== */

  /*
   * Shim constructor intended for fake/mocked growers
   */
  public Grower(String firstName, String lastName) {
    super();

    this.firstName = firstName;
    this.lastName = lastName;
    this.handler = null;
    this.emailAddresses = new ArrayList<>();
    this.phoneNumbers = new ArrayList<>();
  }

  public Grower(Handler handler, String firstName, String lastName,
      List<EmailAddress> emailAddresses, List<String> phoneNumbers) {
    super();

    this.firstName = firstName;
    this.lastName = lastName;
    this.handler = handler;
    this.emailAddresses = emailAddresses;
    this.phoneNumbers = phoneNumbers;
  }


  public String getPrefferedContact() {
    return emailAddresses.isEmpty() ? null : emailAddresses.get(0).toString();
  }


  public List<Offer> getAcceptedOffers() {
    return getOffersWithResponse(GrowerResponse.ACCEPTED);
  }
  public List<Offer> getRejectedOffers() {
    return getOffersWithResponse(GrowerResponse.REJECTED);
  }
  public List<Offer> getCallRequestedOffers() {
    return getOffersWithResponse(GrowerResponse.REQUEST_CALL);
  }
  public List<Offer> getNoResponseOffers() {
    return getOffersWithResponse(GrowerResponse.NO_RESPONSE);
  }

  private List<Offer> getOffersWithResponse(GrowerResponse response) {
    List<Offer> matchedOffers = new ArrayList<>();
    for (GrowerOfferResponse growerOfferResponse : offerResponses) {
      if (growerOfferResponse.getGrowersResponse().equals(response)) {
        matchedOffers.add(growerOfferResponse.getOffer());
      }
    }
    return matchedOffers;
  }

  public List<String> getEmailAddressStrings() {
    return emailAddresses
      .stream()
      .map(EmailAddress::getEmailAddress)
      .collect(Collectors.toList());
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
