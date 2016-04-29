package models.daos;

import java.util.List;

import models.Offer;

public interface OfferDAO {
  public List<Offer> getAllOffers();

  public Offer getOfferById(Long offerId);
}
