package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import models.Grower;
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

    if (data == null) {
      return badRequest("Expecting Some Data.\n");
    }

    Logger.info("Data Recieved: " + data.toString());
    return GrowerService.createGrowerResult(data);
  }

  public Result getAllGrowers() {
    return ok(GrowerService.getAllGrowers().toString());
  }

  public Result getGrower(long id) {
    Grower grower = GrowerService.getGrower(id);

    if (grower == null) {
      return notFound(ErrorMessages.growerNotFoundMessage(id));
    }

    return ok(grower.toString());
  }
}
