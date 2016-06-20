package services.impl;

import com.avaje.ebean.Model.Finder;

import java.util.List;

import models.Grower;
import models.Handler;

import services.HandlerService;

public class EbeanHandlerService implements HandlerService {

  private static Finder<Long, Handler> FINDER = new Finder<>(Handler.class);

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
  public boolean checkHandlerOwnsGrower(Handler handler, Grower grower) {
    return handler.equals(grower.getHandler());
  }
}
