package controllers;

import java.util.List;

import models.Handler;

// TODO Remove *
import play.mvc.*;

import services.ModelService;

public class Application extends Controller {

    public Result index() {

      List<Handler> currentHandlers = Handler.find.all();

      return ok(ModelService.listToPrettyString(currentHandlers));
    }
}
