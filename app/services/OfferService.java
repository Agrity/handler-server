package services;

import java.util.List;

import models.Offer;

public interface OfferService {

  public List<Offer> getAll();

  public Offer getById(long id);

  public List<Offer> getByHandler(long handlerId);

  public List<Offer> getByGrower(long growerId);

}
