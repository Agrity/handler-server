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
  private List<Handler> handlerList;

  /* ====================================== Constructors ====================================== */

  public Trader(String companyName, String emailAddress, String password) {
    setCompanyName(companyName);
    setEmailAddress(emailAddress);
    setPassword(password);
    handlerList = new ArrayList<>();
  }

  /* ==================================== Member Accessors ==================================== */

  public List<Handler> getHandlerList() {
    return handlerList;
  }

   /* =================================== Memeber Functions ==================================== */

  @Override
  public String toString() {
    return "(" + id + ") " + companyName + " : " + getHandlerList().size();
  }
  
  public String toPrettyString() {
    StringBuilder builder = new StringBuilder();
    builder.append("(" + id + ") " + companyName + ":\n");

    if (handlerList.isEmpty()) {
      builder.append(" [] ");

    } else {
      for (Handler handler : handlerList) {
        builder.append("-- " + handler.toPrettyString());
      }
    }

    builder.append("\n");

    return builder.toString();
  }
}