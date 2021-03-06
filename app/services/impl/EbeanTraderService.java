package services.impl;

import com.avaje.ebean.Model.Finder;

import java.util.List;

import models.Trader;
import models.HandlerSeller;
import models.TraderBid;
import models.EmailAddress;
import models.Batch;

import play.Logger;

import services.TraderService;

import utils.SecurityUtility;
import javax.persistence.NonUniqueResultException;

public class EbeanTraderService implements TraderService {
  private static final Finder<Long, Trader> FINDER = new Finder<>(Trader.class);

  @Override
  public List<Trader> getAll() {
    return FINDER.all();
  }

  @Override
  public Trader getById(long id) {
    return FINDER.byId(id);
  }

  @Override
  public Trader getByCompanyName(String companyName) {
    return FINDER
        .where()
        .eq("company_name", companyName)
        .findUnique();
  }

  @Override
  public Trader getByEmailAddressAndPassword(String emailAddress, String password) {
    Trader trader
        = FINDER
          .where()
          .eq("emailAddress.emailAddress", emailAddress.toLowerCase())
          .findUnique();

    if (trader == null) {
      Logger.error("Trader not able to be found");
      return null;
    }

    return SecurityUtility.checkPassword(password, trader.getShaPassword())
      ? trader
      : null;
  }

  @Override
  public Trader getByAuthToken(String authToken) {
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
  public boolean checkTraderOwnsHandlerSeller(Trader trader, HandlerSeller handlerSeller) {
    return trader.equals(handlerSeller.getTrader());
  }
  
  @Override
  public boolean checkTraderOwnsBid(Trader trader, TraderBid traderBid) {
    return trader.equals(traderBid.getTrader());
  }

  @Override
  public boolean checkTraderOwnsBatch(Trader trader, Batch batch) {
    return trader.equals(batch.getTrader());
  }
} 