package services.impl;

import com.avaje.ebean.Model.Finder;

import java.util.List;

import models.Grower;
import models.Handler;
import models.HandlerBid;
import models.EmailAddress;

import play.Logger;

import services.HandlerService;

import utils.SecurityUtility;
import javax.persistence.NonUniqueResultException;

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
    Handler handler
        = FINDER
          .where()
          .eq("emailAddress.emailAddress", emailAddress.toLowerCase())
          .findUnique();

    if (handler == null) {
      Logger.error("Handler not able to be found");
      return null;
    }

    return SecurityUtility.checkPassword(password, handler.getShaPassword())
      ? handler
      : null;
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
    try {
      return null == FINDER
        .where()
        .eq("company_name", companyName)
        .findUnique();
    } catch (NonUniqueResultException nure) {
      /* there are multiple company names in use */
      return false;
    }
  }

  @Override
  public boolean checkEmailAddressAvailable(String emailAddress) {
    try {
      return null == FINDER
        .where()
        .eq("emailAddress.emailAddress", emailAddress.toLowerCase())
        .findUnique();
    } catch (NonUniqueResultException nure) {
      /* there are multiple email addresses in use */
      return false;
    }
  }

  @Override
  public boolean checkHandlerOwnsGrower(Handler handler, Grower grower) {
    return handler.equals(grower.getHandler());
  }

  @Override
  public boolean checkHandlerOwnsBid(Handler handler, HandlerBid handlerBid) {
    return handler.equals(handlerBid.getHandler());
  }
}
