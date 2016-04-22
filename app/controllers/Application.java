package controllers;

// TODO Remove *
import play.mvc.*;

public class Application extends Controller {

    public Result index() {
      return ok(views.html.main.render("Test Title", null));
    }
}
