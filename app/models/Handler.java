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

import play.data.validation.Constraints;

@Entity
public class Handler extends Model {

  @Id
  @Constraints.Min(10)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = Constants.DB.Columns.HANDLER_ID)
  public Long id;

  @Constraints.Required
  public String companyName;

  /* ============== TODO Authorization Here =============== */

  @OneToMany
  @Constraints.Required
  public List<Grower> growersList = new ArrayList<>();

  @OneToMany
  @Constraints.Required
  List<Offer> offerList = new ArrayList<>();

  public static Finder<Long, Handler> find = new Finder<>(Handler.class);

  public Handler() {

  }

  @Override
  public String toString() {
    // TODO
    return "[ " + companyName + " : " + growersList.size() + " ]";
  }
}
