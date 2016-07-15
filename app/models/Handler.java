package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

import models.interfaces.PrettyString;

import play.data.validation.Constraints;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("HANDLER")
public class Handler extends User implements PrettyString {

  @OneToMany
  @Constraints.Required
  @JsonIgnore
  private List<Grower> growersList;

  /* ====================================== Constructors ====================================== */


  public Handler(String companyName, String firstName, String lastName, 
                 String emailAddress, PhoneNumber phoneNumber, String password) {
    super(companyName, firstName, lastName, emailAddress, phoneNumber, password);
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
      for (Grower grower : getGrowersList()) {
        builder.append("-- " + grower.toPrettyString());
      }
    }

    builder.append("\n");

    return builder.toString();
  }
}
