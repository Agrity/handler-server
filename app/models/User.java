package models;

import play.data.validation.Constraints;
import utils.SecurityUtility;
import play.Logger;
import javax.persistence.Column;
import javax.persistence.Transient;
import javax.persistence.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;


@Entity
public class User extends BaseModel {

	@Constraints.Required
  @Column(name = DBConstants.HandlerColumns.COMPANY_NAME, nullable = false)
  protected String companyName;

	/* ===================================== Authentication ===================================== */


  // Email Address, also used as username for login.
  //
  // WARNING: Is expected to always be lowercase.
  @Column(nullable = false)
  protected String emailAddress;

  // Cleartext password. Not Saved to database.
  @Transient
  @Constraints.Required
  @Constraints.MinLength(6)
  @Constraints.MaxLength(256)
  @JsonIgnore
  protected String password;

  // Hashed password saved to database.
  @Column(nullable = false)
  protected String shaPassword;

  public String getShaPassword() {
    return shaPassword;
  }


  // Used to keep track of user signed in on a given device. Will be stored here and in browser
  // when logged in, and will be erased when logged out.
  private String authToken;

  public String getAuthToken() {
    return authToken;
  }

  /* ==================================== Member Accessors ==================================== */


  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getCompanyName() {
    return companyName;
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
}