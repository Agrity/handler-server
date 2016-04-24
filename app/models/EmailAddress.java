package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import play.data.validation.Constraints;

/* Wrapper class to extend model so that a list of email addresses can be saved
 * in the database. 
 *
 * TODO Find way to avoid needing this wrapper class.
 */
@Entity
public class EmailAddress extends Model {

  @Id
  @Constraints.Min(10)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public Long getId() {
    return id;
  }


  @Constraints.Required
  private String emailAddress;

  public String getEmailAddress() {
    return emailAddress;
  }

  public static Finder<Long, EmailAddress> find = new Finder<>(EmailAddress.class);


  public EmailAddress(String emailAddress) {
    super();
    this.emailAddress = emailAddress;
  }

  @Override
  public String toString() {
    // TODO
    return emailAddress;
  }
}
