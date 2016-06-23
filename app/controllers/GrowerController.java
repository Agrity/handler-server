package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import models.Grower;

import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import services.GrowerService;
import services.parsers.GrowerJsonParser;

public class GrowerController extends Controller {

  private final GrowerService growerService;

  private final ObjectMapper jsonMapper;

  @Inject
  public GrowerController(GrowerService growerService) {
    this.growerService = growerService;

    this.jsonMapper = new ObjectMapper();
  }

  // Annotation ensures that POST request is of type application/json. If not HTTP 400 response
  // returned.
  @BodyParser.Of(BodyParser.Json.class)
  public Result createGrower() {
    JsonNode data = request().body().asJson();

    if (data == null) {
      // TODO Change to Valid Error JSON
      return badRequest("Expecting Some Data.\n");
    }

    GrowerJsonParser parser = new GrowerJsonParser(data);

    if (!parser.isValid()) {
      // TODO Change to Valid Error JSON
      return badRequest(parser.getErrorMessage());
    }

    Grower grower = parser.formGrower();
    grower.save();
    

    try {
      return created(jsonMapper.writeValueAsString(grower));
    } catch (JsonProcessingException e) {
      // TODO Change to Valid Error JSON
      return internalServerError(e.toString());
    }

  }

  public Result getAllGrowers() {
    try {
      return created(jsonMapper.writeValueAsString(growerService.getAll()));
    } catch (JsonProcessingException e) {
      // TODO Change to Valid Error JSON
      return internalServerError(e.toString());
    }
  }

  public Result getGrower(long id) {
    Grower grower = growerService.getById(id);

    if (grower == null) {
      return notFound(JsonMsgUtils.growerNotFoundMessage(id));
    }

    try {
      return created(jsonMapper.writeValueAsString(grower));
    } catch (JsonProcessingException e) {
      // TODO Change to Valid Error JSON
      return internalServerError(e.toString());
    }
  }
}
