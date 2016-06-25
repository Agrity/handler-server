package services.impl;

import com.avaje.ebean.Model.Finder;

import java.util.List;

import models.Grower;
import models.Handler;
import models.Offer;

import services.HandlerService;

import utils.SecurityUtility;

public class EbeanHandlerService implements HandlerService {

  private static final Finder<Long, Handler> FINDER = new Finder<>(Handler.class);

  @Override
  public List<Handler> getAll() {
    return FINDER.all();
  }

  @Override
  public Handler getById(long id) {
    return FINDER.byId(id);
  }

  @Override
  public Handler getByCompanyName(String companyName) {
    return FINDER
        .where()
        .eq("company_name", companyName)
        .findUnique();
  }

  @Override
  public Handler getByEmailAddressAndPassword(String emailAddress, String password) {
    return FINDER
        .where()
        .eq("email_address", emailAddress.toLowerCase())
        .eq("sha_password", SecurityUtility.getSha512(password))
        .findUnique();
  }

  @Override
  public Handler getByAuthToken(String authToken) {
    return FINDER
        .where()
        .eq("auth_token", authToken)
        .findUnique();
  }

  @Override
  public boolean checkCompanyNameAvailable(String companyName) {
    return null == FINDER
        .where()
        .eq("company_name", companyName.toLowerCase())
        .findUnique();
  }

  @Override
  public boolean checkEmailAddressAvailable(String emailAddress) {
    return null == FINDER
        .where()
        .eq("email_address", emailAddress.toLowerCase())
        .findUnique();
  }

  @Override
  public boolean checkHandlerOwnsGrower(Handler handler, Grower grower) {
    return handler.equals(grower.getHandler());
  }

  @Override
  public boolean checkHandlerOwnsOffer(Handler handler, Offer offer) {
    return handler.equals(offer.getHandler());
  }
}
