package services.impl;

import com.avaje.ebean.Model.Finder;

import models.Batch;
import services.BatchService;

import java.util.List;

import utils.SecurityUtility;

public class EbeanBatchService implements BatchService {
  private static final Finder<Long, Batch> FINDER = new Finder<>(Batch.class);
  
  @Override
  public List<Batch> getAll() {
    return FINDER.all();
  }

  @Override 
  public Batch getById(long id) {
    return FINDER.byId(id);
  }

}