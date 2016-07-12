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


  public Handler(String companyName, String firstName, String lastName, 
                 String emailAddress, List<PhoneNumber> phoneNumbers, String password) {
    super(companyName, firstName, lastName, emailAddress, phoneNumbers, password);
    growersList = new ArrayList<>();
  }

  /* ==================================== Member Accessors ==================================== */

  public List<Grower> getGrowersList() {
    return growersList;
  }

  /* =================================== Memeber Functions ==================================== */

  @Override
  public String toString() {
    return "(" + id + ") " + getCompanyName() + " : " + getGrowersList().size();
  }
  
  public String toPrettyString() {
    StringBuilder builder = new StringBuilder();
    builder.append("(" + id + ") " + getCompanyName() + ":\n");

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
