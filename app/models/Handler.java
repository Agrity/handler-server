package models;

import com.avaje.ebean.Model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import models.interfaces.PrettyString;
import play.data.validation.Constraints;

@Entity
public class Handler extends Model implements PrettyString {

  @Id
  @Constraints.Min(10)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = DBConstants.HandlerColumns.ID)
  private Long id;

  public Long getId() {
    return id;
  }

  @Constraints.Required
  @Column(name = DBConstants.HandlerColumns.COMPANY_NAME)
  private String companyName;

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getCompanyName() {
    return companyName;
  }

  /* ============== TODO Authorization Here =============== */

  @OneToMany
  @Constraints.Required
  public List<Grower> growersList = new ArrayList<>();

  public List<Grower> getGrowersList() {
    return growersList;
  }

  @OneToMany
  @Constraints.Required
  List<Offer> offerList = new ArrayList<>();

  public static Finder<Long, Handler> find = new Finder<>(Handler.class);

  public Handler() {

  }

  public Handler(String companyName) {
    this.companyName = companyName;
  }

  @Override
  public String toString() {
    return "(" + id + ") " + companyName + " : " + growersList.size();
  }
  
  public String toPrettyString() {
    StringBuilder builder = new StringBuilder();
    builder.append("(" + id + ") " + companyName + ":");

    if (growersList.isEmpty()) {
      builder.append(" [] ");

    } else {
      for (Grower grower : growersList) {
        builder.append("\n-- " + grower.toString());
      }
    }

    builder.append("\n");

    return builder.toString();
  }
}
