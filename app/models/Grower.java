package models;

import com.avaje.ebean.Model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
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
  public Long id;

  @ManyToOne(cascade = CascadeType.ALL)
  @Constraints.Required
  public Handler handler;


  @Constraints.Required
  public String firstName;

  @Constraints.Required
  public String lastName;

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

  @Override
  public String toString() {
    // TODO
    return "[ " + firstName + " " + lastName + " ]";
  }
}
