package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import models.Grower;
import models.Handler;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import services.GrowerService;
import services.HandlerService;

public class HandlerController extends Controller {

  // Annotation ensures that POST request is of type application/json. If not HTTP 400 response
  // returned.
  @BodyParser.Of(BodyParser.Json.class)
  public Result createHandler() {

    JsonNode data = request().body().asJson();

    if (data == null) {
      return badRequest("Expecting Some Data.\n");
    }

    return HandlerService.createHandlerResult(data);
  }

  public Result getAllHandlers() {
    return ok(HandlerService.getAllHandlers().toString());
  }

  public Result getHandler(long id) {
    Handler handler = HandlerService.getHandler(id);

    if (handler == null) {
      return notFound(ErrorMessages.handlerNotFoundMessage(id));
    }

    return ok(handler.toString());
  }

  public Result addGrower(long id) {
    Handler handler = HandlerService.getHandler(id);
    if (handler == null) {
      return notFound(ErrorMessages.handlerNotFoundMessage(id));
    }

    JsonNode data = request().body().asJson();

    if (data == null) {
      return badRequest("Expecting Some Data.\n");
    }

    // TODO Create grower and return created
    return ok("Data Found. Grower Not Created.\n" + data.toString() + "\n");
  }

  public Result getAllGrowers(long id) {
    Handler handler = Handler.find.byId(id);
    if (handler == null) {
      return notFound(ErrorMessages.handlerNotFoundMessage(id));
    }

    return ok(Helpers.fetchList(handler.getGrowersList()).toString());
  }

  public Result getGrower(long id, long growerId) {
    Handler handler = Handler.find.byId(id);
    if (handler == null) {
      return notFound(ErrorMessages.handlerNotFoundMessage(id));
    }

    Grower grower = GrowerService.getGrower(growerId);
    if (grower == null) {
      return notFound(ErrorMessages.growerNotFoundMessage(growerId));
    }

    if (!HandlerService.checkHandlerOwnsGrower(handler, grower)) {
      return badRequest(ErrorMessages.handlerDoesNotOwnGrowerMessage(handler, grower));
    }

    return ok(grower.toString());
  }
}
