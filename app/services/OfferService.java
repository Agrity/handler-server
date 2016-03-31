package services;

import models.Offer;

import play.mvc.Controller;


public class OfferService extends Controller {

  public static Offer getGrower(Long id) {
    return Offer.find.byId(id);
  }
}
