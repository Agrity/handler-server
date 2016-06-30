package models;

import javax.persistence.Entity;

import play.data.validation.Constraints;

/* Wrapper class to extend model so that a list of phone numbers can be saved
 * in the database. 
 *
 * TODO Find way to avoid needing this wrapper class.
 */
@Entity
public class PhoneNumber extends BaseModel {

  @Constraints.Required
  private String phoneNumber;

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public static Finder<Long, PhoneNumber> find = new Finder<>(PhoneNumber.class);


  public PhoneNumber(String phoneNumber) {
    super();
    this.phoneNumber = phoneNumber;
  }

  @Override
  public String toString() {
    // TODO
    return phoneNumber;
  }
}