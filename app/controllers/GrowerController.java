package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import services.GrowerService;

public class GrowerController extends Controller {

  // Annotation ensures that POST request is of type application/json. If not HTTP 400 response
  // returned.
  @BodyParser.Of(BodyParser.Json.class)
  public Result createGrower() {


    JsonNode data = request().body().asJson();
    Logger.info("Data Recieved: " + data.toString());

    if (data == null) {
      return badRequest("Expecting Some Data.\n");
    }

    return GrowerService.createGrowerResult(data);
  }
}
