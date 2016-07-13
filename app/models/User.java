package models;

import play.data.validation.Constraints;
import utils.SecurityUtility;
import play.Logger;
import javax.persistence.Column;
import javax.persistence.Transient;
import javax.persistence.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;
import javax.persistence.MappedSuperclass;

import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

import models.PhoneNumber;

import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
public abstract class User extends BaseModel {

  //TODO change name to UserColumns?
	@Constraints.Required
  @Column(name = DBConstants.HandlerColumns.COMPANY_NAME, nullable = false)
  private String companyName;

  @Constraints.Required
  private String firstName;

  @Constraints.Required
  private String lastName;

	/* ===================================== Authentication ===================================== */


  // Email Address, also used as username for login.
  //
  // WARNING: Is expected to always be lowercase.
  @Column(nullable = false)
  private String emailAddress;

  @OneToMany(cascade = CascadeType.ALL)
  private List<PhoneNumber> phoneNumbers;

  // Cleartext password. Not Saved to database.
  @Transient
  @Constraints.Required
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
              String emailAddress,  List<PhoneNumber> phoneNumbers, String password) {
    setCompanyName(companyName);
    setFirstName(firstName);
    setLastName(lastName);
    setEmailAddress(emailAddress);
    setPhoneNumbers(phoneNumbers);
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

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress.toLowerCase();
  }

  public String getEmailAddress() {
    return emailAddress;
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

  public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
    this.phoneNumbers = phoneNumbers;
  }

  public List<PhoneNumber> getPhoneNumbers() {
    return phoneNumbers;
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
