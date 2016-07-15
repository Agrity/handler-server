package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.interfaces.PrettyString;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;

import play.Logger;
import play.data.validation.Constraints;
import javax.persistence.Entity;

@Entity
public class Trader extends User implements PrettyString {

  @ManyToMany
  @Constraints.Required
  @JsonIgnore
  private List<HandlerSeller> handlerSellerList;

  /* ====================================== Constructors ====================================== */

  public Trader(String companyName, String firstName, String lastName, 
                String emailAddress, List<PhoneNumber> phoneNumbers, String password) {
    super(companyName, firstName, lastName, emailAddress, phoneNumbers, password);
    handlerSellerList = new ArrayList<>();
  }

  /* ==================================== Member Accessors ==================================== */

  public List<HandlerSeller> getHandlerSellerList() {
    return handlerSellerList;
  }

   /* =================================== Memeber Functions ==================================== */

  @Override
  public String toString() {
    return "(" + id + ") " + getCompanyName() + " : " + getHandlerSellerList().size();
  }
  
  public String toPrettyString() {
    StringBuilder builder = new StringBuilder();
    builder.append("(" + id + ") " + getCompanyName() + ":\n");

    if (handlerSellerList.isEmpty()) {
      builder.append(" [] ");

    } else {
      for (HandlerSeller handlerSeller : getHandlerSellerList()) {
        builder.append("-- " + handlerSeller.toPrettyString());
      }
    }

    builder.append("\n");

    return builder.toString();
  }
}