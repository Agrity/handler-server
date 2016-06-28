package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import models.interfaces.PrettyString;

import play.Logger;
import play.data.validation.Constraints;

import utils.SecurityUtility;

@Entity
public class Handler extends BaseModel implements PrettyString {

  @Constraints.Required
  @Column(name = DBConstants.HandlerColumns.COMPANY_NAME, nullable = false)
  private String companyName;

  @OneToMany
  @Constraints.Required
  @JsonIgnore
  private List<Grower> growersList;


  /* ===================================== Authentication ===================================== */


  // Email Address, also used as username for login.
  //
  // WARNING: Is expected to always be lowercase.
  @Column(nullable = false)
  private String emailAddress;

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

  public String getShaPassword() {
    return shaPassword;
  }


  // Used to keep track of user signed in on a given device. Will be stored here and in browser
  // when logged in, and will be erased when logged out.
  private String authToken;

  public String getAuthToken() {
    return authToken;
  }


  /* ====================================== Constructors ====================================== */


  public Handler(String companyName, String emailAddress, String password) {
    setCompanyName(companyName);
    setEmailAddress(emailAddress);
    setPassword(password);
    growersList = new ArrayList<>();
  }


  /* ==================================== Member Accessors ==================================== */


  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getCompanyName() {
    return companyName;
  }

  public List<Grower> getGrowersList() {
    return growersList;
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


  /* =================================== Memeber Functions ==================================== */


  @Override
  public String toString() {
    return "(" + id + ") " + companyName + " : " + getGrowersList().size();
  }
  
  public String toPrettyString() {
    StringBuilder builder = new StringBuilder();
    builder.append("(" + id + ") " + companyName + ":\n");

    if (growersList.isEmpty()) {
      builder.append(" [] ");

    } else {
      for (Grower grower : growersList) {
        builder.append("-- " + grower.toPrettyString());
      }
    }

    builder.append("\n");

    return builder.toString();
  }
}
