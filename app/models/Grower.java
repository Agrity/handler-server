package models;

import com.avaje.ebean.Model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints;

@Entity
public class Grower extends Model {

  @Id
  @Constraints.Min(10)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = DBConstants.GrowerColumns.ID)
  private Long id;

  public Long getId() {
    return id;
  }

  @ManyToOne(cascade = CascadeType.ALL)
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
   * <a href="https://github.com/playframework/play-mailer/blob/master/README.adoc">Plugin Link</a>
   */
  public List<String> emailAddresses;

  /**
   * TODO: Change to phone number format
   */
  public List<String> phoneNumbers;


  @Constraints.Required
  public List<Offer> recievedOffers = new ArrayList<>();


  public static Finder<Long, Grower> find = new Finder<>(Grower.class);

  public Grower() {}

  public Grower(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  @Override
  public String toString() {
    // TODO
    return "(" + id + ") " + getFullName() + " [" + handler.getCompanyName() + "]";
  }
}
