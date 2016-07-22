package services;

import com.google.inject.ImplementedBy;

import models.Batch;
import java.util.List;

import services.impl.EbeanBatchService;

@ImplementedBy(EbeanBatchService.class)
public interface BatchService {
  
  public List<Batch> getAll();

  public Batch getById(long id);

}