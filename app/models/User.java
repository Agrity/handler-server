package models;

import play.data.validation.Constraints;
import utils.SecurityUtility;
import play.Logger;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

import javax.persistence.OneToOne;
import javax.persistence.Table;

import models.PhoneNumber;
import services.ModelService;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
@Table(name = "USERS")
public abstract class User extends BaseModel {

  //TODO change name to UserColumns?
  @Column(nullable = false)
  private String companyName;

  private String firstName;

  private String lastName;

	/* ===================================== Authentication ===================================== */


  // Email Address, also used as username for login.
  //
  // WARNING: Is expected to always be lowercase.
  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @Column(nullable = false)
  private EmailAddress emailAddress;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @Column(nullable = false)
  private PhoneNumber phoneNumber;

  // Cleartext password. Not Saved to database.
  @Transient
  @Constraints.MinLength(6)
  @Constraints.MaxLength(256)
  @JsonIgnore
  private String password;

  // Hashed password saved to database.
  @Column(nullable = false)
  private String shaPassword;

  // Used to keep track of user signed in on a given device. Will be stored here and in browser
  // when logged in, and will be erased when logged out.
  private String authToken;

  public User(String companyName, String firstName, String lastName, 
              EmailAddress emailAddress,  PhoneNumber phoneNumber, String password) {
    setCompanyName(companyName);
    setFirstName(firstName);
    setLastName(lastName);
    setEmailAddress(emailAddress);
    setPhoneNumber(phoneNumber);
    setPassword(password);
  }

  /* ==================================== Member Accessors ==================================== */


  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getFullName() {
    return getFirstName() + " " + getLastName();
  }

  public void setEmailAddress(EmailAddress emailAddress) {
    this.emailAddress = emailAddress;
  }

  public EmailAddress getEmailAddress() {
    return emailAddress;
  }

  public String getEmailAddressString() {
    return getEmailAddress().getEmailAddress();
  }

  public void setPassword(String password) {
    Logger.error("Password: " + password);
    this.password = password;
    this.shaPassword = SecurityUtility.hashPassword(password);
  }

  public String getPassword() {
    return password;
  }

  public String getShaPassword() {
    return shaPassword;
  } 

  public void setPhoneNumber(PhoneNumber phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public PhoneNumber getPhoneNumber() {
    return phoneNumber;
  }

  public String getPhoneNumberString() {
    return getPhoneNumber().getPhoneNumber();
  }

  public String getPrettyPhoneNumberString() {
    ModelService service = new ModelService();
    String phoneNum = getPhoneNumber().getPhoneNumber();
    return service.phoneNumberToPrettyString(phoneNum);
  }

  public String createToken() {
    authToken = UUID.randomUUID().toString();
    Logger.debug("Auth Token Created (" + id + "): " + authToken);
    save();
    return authToken;
  }

  public void deleteAuthToken() {
    authToken = null;
    save();
  }

   public String getAuthToken() {
    return authToken;
  }
}
