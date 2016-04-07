package controllers;

import java.util.List;

import models.Handler;

// TODO Remove *
import play.mvc.*;

public class Application extends Controller {

    public Result index() {

      List<Handler> currentHandlers = Handler.find.all();

      return ok(currentHandlers.toString());
    }
}
