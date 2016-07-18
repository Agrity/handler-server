package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Column;

import play.data.validation.Constraints;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
@Table(name = "SELLERS")
public abstract class BaseSeller extends BaseModel {

  /* ======================================= Attributes ======================================= */

  private String firstName;

  private String lastName;

  /**
   * TODO: Change to play email format to use play-mailer plugin
   *
   * <a href="https://github.com/playframework/play-mailer/blob/master/README.adoc">Plugin Link</a>
   */
  @OneToOne
  @Column(nullable = false)
  private EmailAddress emailAddress;

  /**
   * TODO: Change to phone number format, construct own model so that can be consistant.
   */
  @OneToOne
  @Column(nullable = false)
  private PhoneNumber phoneNumber;

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
  public EmailAddress getEmailAddress() {
    return emailAddress;
  }

  public String getEmailAddressString() {
    return getEmailAddress().toString();
  }

  @JsonIgnore
  public PhoneNumber getPhoneNumber() {
    return phoneNumber;
  }

  public String getPhoneNumberString() {
    return getPhoneNumber().toString();
  }

  /* ======================================= Attribute Setters ======================================= */

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setPhoneNumber(PhoneNumber phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public void setEmailAddress(EmailAddress email) {
    emailAddress = email;
  }

  /* ======================================= Member Functions ======================================= */

  @Override
  public String toString() {
    // TODO
    return "(" + id + ") " + getFullName() + " [" + getEmailAddress().toString() + "]";
  }
}
