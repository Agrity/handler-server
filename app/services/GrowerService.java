package services;

import models.Grower;

import play.mvc.Controller;


public class GrowerService extends Controller {

  public static Grower getGrower(Long id) {
    return Grower.find.byId(id);
  }
}
