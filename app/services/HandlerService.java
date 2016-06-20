package services;

import com.google.inject.ImplementedBy;

import java.util.List;

import models.Grower;
import models.Handler;

import services.impl.EbeanHandlerService;

@ImplementedBy(EbeanHandlerService.class)
public interface HandlerService {

  public List<Handler> getAll();

  public Handler getById(long id);

  public Handler getByCompanyName(String companyName);

  public boolean checkHandlerOwnsGrower(Handler handler, Grower grower);
}
