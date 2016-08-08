package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import java.util.List;

import controllers.security.AdminSecured;
import models.Grower;
import models.HandlerBid;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import services.GrowerService;
import services.HandlerBidService;

import utils.JsonMsgUtils;

@Security.Authenticated(AdminSecured.class)
public class GrowerController extends Controller {

  private final GrowerService growerService;
  private final HandlerBidService handlerBidService;
  private final ObjectMapper jsonMapper;

  @Inject
  public GrowerController(GrowerService growerService, HandlerBidService handlerBidService) {
    this.growerService = growerService;
    this.handlerBidService = handlerBidService;
    this.jsonMapper = new ObjectMapper();
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

  public Result getGrowerByEmailAddress(String emailAddress) {
    Grower grower = growerService.growerLookupByEmailAddress(emailAddress);

    if (grower == null) {
      return notFound(JsonMsgUtils.growerEmailNotFoundMessage(emailAddress));
    }

    try {
      return created(jsonMapper.writeValueAsString(grower));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  public Result getAllBids(long growerId) {
    List<HandlerBid> growerBids = handlerBidService.getByGrower(growerId);

    // growerBids will be null if Grower with growerId cannot be found.
    if (growerBids == null) {
      return notFound(JsonMsgUtils.growerNotFoundMessage(growerId));
    }

    try {
      return ok(jsonMapper.writeValueAsString(growerBids));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }
}
