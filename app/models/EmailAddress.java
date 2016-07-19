package models;

import javax.persistence.Entity;

import play.data.validation.Constraints;

import play.Logger;

/* Wrapper class to extend model so that a list of email addresses can be saved
 * in the database. 
 *
 * TODO Find way to avoid needing this wrapper class.
 */
@Entity
public class EmailAddress extends BaseModel {

  @Constraints.Required
  private String emailAddress;

  public static Finder<Long, EmailAddress> find = new Finder<>(EmailAddress.class);


  public EmailAddress(String emailAddress) {
    super();
    this.emailAddress = emailAddress;
  }

  @Override
  public String toString() {
    // TODO
    Logger.debug("In EmailAddress model - " + emailAddress);
    return emailAddress;
  }
}
