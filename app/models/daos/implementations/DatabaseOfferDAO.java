package models.daos.implementations;

import com.avaje.ebean.Model.Finder;

import java.util.List;

import models.Offer;
import models.daos.OfferDAO;

public class DatabaseOfferDAO implements OfferDAO {
  private static Finder<Long, Offer> FIND = new Finder<Long, Offer>(Offer.class);

  public List<Offer> getAllOffers() {
    return FIND.all();
  }

  public Offer getOfferById(Long offerId) {
    return FIND.byId(offerId);
  }
}
