package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.interfaces.PrettyString;
import models.Batch;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Constraints;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("TRADER")
public class Trader extends User implements PrettyString {

  @ManyToMany
  @Constraints.Required
  @JsonIgnore
  private List<HandlerSeller> handlerSellers;

  //TODO fix mapping error so that this doesn't need to be in here
  @OneToMany(mappedBy="trader")
  private List<Batch> batches;

  /* ====================================== Constructors ====================================== */

  public Trader(String companyName, String firstName, String lastName, 
                EmailAddress emailAddress, PhoneNumber phoneNumber, String password) {
    super(companyName, firstName, lastName, emailAddress, phoneNumber, password);
    handlerSellers = new ArrayList<>();

    batches = new ArrayList<>();
  }

  public void setBatch(Batch batch) {
    batches.add(batch);
  }

  /* ==================================== Member Accessors ==================================== */

  public List<HandlerSeller> getHandlerSellers() {
    return handlerSellers;
  }

   /* =================================== Memeber Functions ==================================== */

  @Override
  public String toString() {
    return "(" + id + ") " + getCompanyName() + " : " + getHandlerSellers().size();
  }
  
  public String toPrettyString() {
    StringBuilder builder = new StringBuilder();
    builder.append("(" + id + ") " + getCompanyName() + ":\n");

    if (handlerSellers.isEmpty()) {
      builder.append(" [] ");

    } else {
      for (HandlerSeller handlerSeller : getHandlerSellers()) {
        builder.append("-- " + handlerSeller.toPrettyString());
      }
    }

    builder.append("\n");

    return builder.toString();
  }
}
