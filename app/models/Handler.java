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
public class Handler extends User implements PrettyString {

  @OneToMany
  @Constraints.Required
  @JsonIgnore
  private List<Grower> growersList;

  /* ====================================== Constructors ====================================== */


  public Handler(String companyName, String emailAddress, String password) {
    setCompanyName(companyName);
    setEmailAddress(emailAddress);
    setPassword(password);
    growersList = new ArrayList<>();
  }

  /* ==================================== Member Accessors ==================================== */

  public List<Grower> getGrowersList() {
    return growersList;
  }

  /* =================================== Memeber Functions ==================================== */

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress.toLowerCase();
  }

  public String getEmailAddress() {
    return emailAddress;
  }

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
