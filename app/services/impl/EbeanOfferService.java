package services.impl;

import com.avaje.ebean.Model.Finder;

import java.util.List;

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
    // TODO Assert Handler Exists, or Return Null
    return FINDER.where()
        // TODO Fix this Column Name.
        .eq("handler_handler_id", handlerId)
        .findList();
  }

  public List<Offer> getByGrower(long growerId) {
    // TODO Assert Grower Exists, or Return Null
    return FINDER.where()
        .eq("growers.id", growerId)
        .findList();
  }
}
