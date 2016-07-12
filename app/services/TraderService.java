package services;

import com.google.inject.ImplementedBy;

import java.util.List;

import models.Trader;
import models.Handler;
import models.Offer;

import services.impl.EbeanTraderService;

@ImplementedBy(EbeanTraderService.class)
public interface TraderService {

  public List<Trader> getAll();

  public Trader getById(long id);

  public Trader getByCompanyName(String companyName);

  public Trader getByEmailAddressAndPassword(String emailAddress, String password);

  public Trader getByAuthToken(String authToken);

  public boolean checkCompanyNameAvailable(String companyName);

  public boolean checkEmailAddressAvailable(String emailAddress);

  //TODO: add in checkTraderOwnsHandler and checkTraderOwnsOffer
}