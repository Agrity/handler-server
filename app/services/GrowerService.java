package services;

import com.google.inject.ImplementedBy;

import java.util.List;

import models.Grower;

import services.impl.EbeanGrowerService;

@ImplementedBy(EbeanGrowerService.class)
public interface GrowerService {

  public List<Grower> getAll();

  public Grower getById(long id);

  public List<Grower> getByHandler(long handlerId);

  public List<Grower> getByOffer(long offerId);

}
