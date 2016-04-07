package controllers;

import models.Grower;
import models.Handler;

public class ErrorMessages {
  public static String handlerNotFoundMessage(Long id) {
    return "Handler with id '" + id + "' could not be found\n";
  }

  public static String growerNotFoundMessage(Long id) {
    return "Grower with id '" + id + "' could not be found\n";
  }

  public static String handlerDoesNotOwnGrowerMessage(Handler handler, Grower grower) {
    return "Handler " + handler.getCompanyName() + " does not own Grower " + grower.getFullName()
        + ".\n";
  }
}
