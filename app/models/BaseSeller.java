package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.persistence.MappedSuperclass;
import play.data.validation.Constraints;


@MappedSuperclass
public abstract class BaseSeller extends BaseModel {

  /* ======================================= Attributes ======================================= */

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

  /* ======================================= Attribute Accessors ======================================= */

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
  public List<EmailAddress> getEmailAddresses() {
    return emailAddresses;
  }

  public List<String> getEmailAddressStrings() {
    return getEmailAddresses()
      .stream()
      .map(EmailAddress::getEmailAddress)
      .collect(Collectors.toList());
  }

  @JsonIgnore
  public List<PhoneNumber> getPhoneNumbers() {
    return phoneNumbers;
  }

  public List<String> getPhoneNumsStrings() {
    return getPhoneNumbers()
      .stream()
      .map(PhoneNumber::getPhoneNumber)
      .collect(Collectors.toList());
  }

  /* ======================================= Attribute Setters ======================================= */

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
    this.phoneNumbers = this.phoneNumbers;
  }

  public void setEmailAddresses(List<EmailAddress> emails) {
    emailAddresses = emails;
  }

  /* ======================================= Member Functions ======================================= */

  @Override
  public String toString() {
    // TODO
    return "(" + id + ") " + getFullName() + " [" + getEmailAddresses().toString() + "]";
  }
}