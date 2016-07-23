package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import utils.ResponseHeaders;

public class Application extends Controller {

  public Result home() {
    return ok("Home.");
  }

  public Result options(String path) {
    ResponseHeaders.addResponseHeaders(response());
    return ok("");
  }

  public Result reset() {
    return redirect("/");
  }
}
