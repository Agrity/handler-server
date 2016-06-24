package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import models.Grower;
import models.Handler;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import services.GrowerService;
import services.HandlerService;
import services.parsers.HandlerJsonParser;

public class HandlerController extends Controller {

  private final HandlerService handlerService;
  private final GrowerService growerService;

  private final ObjectMapper jsonMapper;

  @Inject
  public HandlerController(HandlerService handlerService, GrowerService growerService) {
    this.handlerService = handlerService;
    this.growerService = growerService;

    this.jsonMapper = new ObjectMapper();
  }

  // Annotation ensures that POST request is of type application/json. If not HTTP 400 response
  // returned.
  @BodyParser.Of(BodyParser.Json.class)
  public Result createHandler() {
    JsonNode data = request().body().asJson();

    if (data == null) {
      // TODO Change to Valid Error JSON
      return badRequest("Expecting Some Data.\n");
    }

    HandlerJsonParser parser = new HandlerJsonParser(data);

    if (!parser.isValid()) {
      // TODO Change to Valid Error JSON
      return badRequest(parser.getErrorMessage());
    }

    Handler handler = parser.formHandler();
    handler.save();

    try {
      return created(jsonMapper.writeValueAsString(handler));
    } catch (JsonProcessingException e) {
      // TODO Change to Valid Error JSON
      return internalServerError(e.toString());
    }
  }

  public Result getAllHandlers() {
    try {
      return ok(jsonMapper.writeValueAsString(handlerService.getAll()));
    } catch (JsonProcessingException e) {
      // TODO Change to Valid Error JSON
      return internalServerError(e.toString());
    }
  }

  @Security.Authenticated(Secured.class)
  public Result getHandler(long id) {
    Handler handler = handlerService.getById(id);

    if (handler == null) {
      // TODO Change to Valid Error JSON
      return notFound(ErrorMessages.handlerNotFoundMessage(id));
    }

    try {
      return ok(jsonMapper.writeValueAsString(handler));
    } catch (JsonProcessingException e) {
      // TODO Change to Valid Error JSON
      return internalServerError(e.toString());
    }
  }

  @Security.Authenticated(Secured.class)
  public Result getAllGrowers(long handlerId) {
    Handler handler = handlerService.getById(handlerId);

    if (handler == null) {
      // TODO Change to Valid Error JSON
      return notFound(ErrorMessages.handlerNotFoundMessage(handlerId));
    }

    // TODO Use Grower Service After Implemented.
    return ok("TODO Grower");
  }

  @Security.Authenticated(Secured.class)
  public Result getGrower(long handlerId, long growerId) {
    Handler handler = handlerService.getById(handlerId);
    
    if (handler == null) {
      // TODO Change to Valid Error JSON
      return notFound(ErrorMessages.handlerNotFoundMessage(handlerId));
    }

    Grower grower = growerService.getById(growerId);
    if (grower == null) {
      // TODO Change to Valid Error JSON
      return notFound(ErrorMessages.growerNotFoundMessage(growerId));
    }

    if (!handlerService.checkHandlerOwnsGrower(handler, grower)) {
      // TODO Change to Valid Error JSON
      return badRequest(ErrorMessages.handlerDoesNotOwnGrowerMessage(handler, grower));
    }

    try {
      return ok(jsonMapper.writeValueAsString(grower));
    } catch (JsonProcessingException e) {
      // TODO Change to Valid Error JSON
      return internalServerError(e.toString());
    }
  }
}
