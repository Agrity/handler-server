package services.impl;

import com.avaje.ebean.Model.Finder;
import java.util.List;

import models.Grower;
import models.PhoneNumber;

import services.GrowerService;
import play.Logger;

public class EbeanGrowerService implements GrowerService {

  private static Finder<Long, Grower> FINDER = new Finder<>(Grower.class);

  @Override
  public List<Grower> getAll() {
    return FINDER.all();
  }

  @Override
  public Grower getById(long id) {
    return FINDER.byId(id);
  }

  @Override
  public List<Grower> getByHandler(long handlerId) {
    // TODO Assert Handler Exists, or Return Null
    return FINDER.where()
        // TODO Fix this Column Name.
        .eq("handler_id", handlerId)
        .findList();
  }

  @Override
  public List<Grower> getByOffer(long offerId) {
    // TODO Assert Grower Exists, or Return Null
    return FINDER.where()
        .eq("offers.id", offerId)
        .findList();
  }

  public Grower growerLookupByPhoneNum(String phoneNum) {
    List<Grower> growers = getAll(); 
    for (Grower grower: growers) {
      for (PhoneNumber curPhoneNum: grower.getPhoneNums()) {
        Logger.info("These are the curPhoneNums being looked up: " + curPhoneNum.getPhoneNumber()
                 + "   " + phoneNum + "\n\n");
        if (curPhoneNum.getPhoneNumber().equals(phoneNum)) {
          return grower;
        }
      }
    }
    return null;
  }
}
