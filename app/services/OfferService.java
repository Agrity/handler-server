package services;

import com.google.inject.ImplementedBy;

import java.util.List;

import models.Offer;

import services.impl.EbeanOfferService;

@ImplementedBy(EbeanOfferService.class)
public interface OfferService {

  public List<Offer> getAll();

  public Offer getById(long id);

  public List<Offer> getByHandler(long handlerId);

  public List<Offer> getByGrower(long growerId);

}
