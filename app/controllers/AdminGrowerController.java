package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import controllers.security.AdminSecured;
import models.Grower;
import models.HandlerBid;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import services.GrowerService;
import services.HandlerBidService;
import services.parsers.GrowerJsonParser;

import utils.JsonMsgUtils;

@Security.Authenticated(AdminSecured.class)
public class AdminGrowerController extends Controller {

  private final GrowerService growerService;
  private final HandlerBidService handlerBidService;
  private final ObjectMapper jsonMapper;

  @Inject
  public AdminGrowerController(GrowerService growerService, HandlerBidService handlerBidService) {
    this.growerService = growerService;
    this.handlerBidService = handlerBidService;
    this.jsonMapper = new ObjectMapper();
  }

  // Annotation ensures that POST request is of type application/json. If not HTTP 400 response
  // returned.
  @BodyParser.Of(BodyParser.Json.class)
  public Result createGrower() {
    JsonNode data = request().body().asJson();

    if (data == null) {
      return badRequest(JsonMsgUtils.expectingData());
    }

    GrowerJsonParser parser = new GrowerJsonParser(data);

    if (!parser.isValid()) {
      return badRequest(JsonMsgUtils.caughtException(parser.getErrorMessage()));
    }

    Grower grower = parser.formGrower();
    grower.save();
    

    try {
      return created(jsonMapper.writeValueAsString(grower));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  public Result updateGrower(long growerId) {
    JsonNode data = request().body().asJson();

    if (data == null) {
      return badRequest(JsonMsgUtils.expectingData());
    }

    Grower grower = growerService.getById(growerId);
    if (grower == null) {
      return notFound(JsonMsgUtils.growerNotFoundMessage(growerId));
    }

    GrowerJsonParser parser = new GrowerJsonParser(data);

    if (!parser.isValid()) {
      return badRequest(JsonMsgUtils.caughtException(parser.getErrorMessage()));
    }

    for(HandlerBid handlerBid: handlerBidService.getByGrower(growerId)) {
      if(handlerBid.bidCurrentlyOpen()) {
        //Conflict response
        return status(409, JsonMsgUtils.growerInBid(growerId, handlerBid.getId()));
      }
    }

    parser.updateGrower(grower);
    grower.save();

    try {
      return created(jsonMapper.writeValueAsString(grower));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }

  }

  public Result deleteGrower(long growerId) {
    Grower grower = growerService.getById(growerId);
    
    if (grower == null) {
      return notFound(JsonMsgUtils.growerNotFoundMessage(growerId));
    }

    for(HandlerBid handlerBid: handlerBidService.getByGrower(growerId)) {
      if (handlerBid.bidCurrentlyOpen()) {
        //Conflict response
        return status(409, JsonMsgUtils.growerInBid(growerId, handlerBid.getId()));
      }
    }

    grower.delete();
    return ok(JsonMsgUtils.growerDeleted(growerId));
  }

  public Result getAllGrowers() {
    try {
      return created(jsonMapper.writeValueAsString(growerService.getAll()));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
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
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }
}
