package models;

import com.avaje.ebean.Model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.OfferResponse.ResponseStatus;
import models.interfaces.PrettyString;

import play.data.validation.Constraints;

@Entity
public class Grower extends Model implements PrettyString {

  public static final Comparator<Grower> ID_COMPARATOR
      = new Comparator<Grower>() {
          @Override
          public int compare(Grower g1, Grower g2) {
            return Long.compare(g1.getId(), g2.getId());
          }
  };

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
  public List<OfferResponse> offerResponses = new ArrayList<>();

  @Constraints.Required
  @ManyToMany
  public List<Offer> offers = new ArrayList<>();


  /* ==================================== Static Functions ==================================== */


  public static Finder<Long, Grower> find = new Finder<>(Grower.class);

  public static Grower createGrower(Handler handler, String firstName, String lastName) {
    Grower grower = new Grower(handler, firstName, lastName, new ArrayList<>(), new ArrayList<>());
    grower.save();
    return grower;
  }
  


  /* ========== Member Functions ========== */

  /*
   * Shim constructor intended for fake/mocked growers
   * TODO REMOVE!
   */
  public Grower(long id, String firstName, String lastName) {
    super();

    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.handler = null;
    this.emailAddresses = new ArrayList<>();
    this.phoneNumbers = new ArrayList<>();
  }

  public Grower(Handler handler, String firstName, String lastName) {
    super();

    this.handler = handler;
    this.firstName = firstName;
    this.lastName = lastName;

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
    return getOffersWithResponse(ResponseStatus.ACCEPTED);
  }
  public List<Offer> getRejectedOffers() {
    return getOffersWithResponse(ResponseStatus.REJECTED);
  }
  public List<Offer> getCallRequestedOffers() {
    return getOffersWithResponse(ResponseStatus.REQUEST_CALL);
  }
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
