package services.impl;

import com.avaje.ebean.Model.Finder;

import java.util.List;

import models.DBConstants;
import models.Offer;

import services.OfferService;

public class EbeanOfferService implements OfferService {

  private static Finder<Long, Offer> FINDER = new Finder<>(Offer.class);

  @Override
  public List<Offer> getAll() {
    return FINDER.all();
  }

  @Override
  public Offer getById(long id) {
    return FINDER.byId(id);
  }

  public List<Offer> getByHandler(long handlerId) {
    return FINDER.where()
        .eq(DBConstants.HANDLER_ID, handlerId)
        .findList();
  }

  public List<Offer> getByGrower(long growerId) {
    return FINDER.where()
        .eq(DBConstants.GROWER_ID, growerId)
        .findList();
  }
}
