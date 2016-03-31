package services;

import models.Handler;

import play.mvc.Controller;


public class HandlerService extends Controller {

  public static Handler getHandler(Long id) {
    return Handler.find.byId(id);
  }

}
