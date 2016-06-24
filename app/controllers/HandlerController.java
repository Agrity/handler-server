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

import controllers.security.HandlerSecured;

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
      return badRequest(JsonMsgUtils.expectingData());
    }

    HandlerJsonParser parser = new HandlerJsonParser(data);

    if (!parser.isValid()) {
      return badRequest(JsonMsgUtils.caughtException(parser.getErrorMessage()));
    }

    Handler handler = parser.formHandler();
    handler.save();

    try {
      return created(jsonMapper.writeValueAsString(handler));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  public Result getAllHandlers() {
    try {
      return ok(jsonMapper.writeValueAsString(handlerService.getAll()));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  @Security.Authenticated(HandlerSecured.class)
  public Result getHandler(long id) {
    Handler handler = handlerService.getById(id);

    if (handler == null) {
      return notFound(JsonMsgUtils.handlerNotFoundMessage(id));
    }

    try {
      return ok(jsonMapper.writeValueAsString(handler));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  @Security.Authenticated(HandlerSecured.class)
  public Result getAllGrowers(long handlerId) {
    Handler handler = handlerService.getById(handlerId);

    if (handler == null) {
      return notFound(JsonMsgUtils.handlerNotFoundMessage(handlerId));
    }

    try {
      return ok(jsonMapper.writeValueAsString(growerService.getByHandler(handlerId)));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  @Security.Authenticated(HandlerSecured.class)
  public Result getGrower(long handlerId, long growerId) {
    Handler handler = handlerService.getById(handlerId);
    
    if (handler == null) {
      return notFound(JsonMsgUtils.handlerNotFoundMessage(handlerId));
    }

    Grower grower = growerService.getById(growerId);
    if (grower == null) {
      return notFound(JsonMsgUtils.growerNotFoundMessage(growerId));
    }

    if (!handlerService.checkHandlerOwnsGrower(handler, grower)) {
      return badRequest(JsonMsgUtils.handlerDoesNotOwnGrowerMessage(handler, grower));
    }

    try {
      return ok(jsonMapper.writeValueAsString(grower));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }
}
