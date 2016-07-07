package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import controllers.security.AdminSecured;
import models.Grower;
import models.Offer;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import services.GrowerService;
import services.OfferService;
import services.parsers.GrowerJsonParser;

import utils.JsonMsgUtils;

@Security.Authenticated(AdminSecured.class)
public class AdminGrowerController extends Controller {

  private final GrowerService growerService;
  private final OfferService offerService;
  private final ObjectMapper jsonMapper;

  @Inject
  public AdminGrowerController(GrowerService growerService, OfferService offerService) {
    this.growerService = growerService;
    this.offerService = offerService;
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

    for(Offer off: offerService.getByGrower(growerId)) {
      if(off.getOfferCurrentlyOpen()) {
        //Conflict response
        return status(409, JsonMsgUtils.growerInOffer(growerId, off.getId()));
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

    for(Offer off: offerService.getByGrower(growerId)) {
      if(off.getOfferCurrentlyOpen()) {
        //Conflict response
        return status(409, JsonMsgUtils.growerInOffer(growerId, off.getId()));
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
