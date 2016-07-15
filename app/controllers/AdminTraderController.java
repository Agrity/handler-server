package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import controllers.security.AdminSecured;
import models.Trader;
import models.HandlerSeller;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import services.TraderService;
import services.HandlerSellerService;
import services.parsers.UserJsonParser;

import utils.JsonMsgUtils;

@Security.Authenticated(AdminSecured.class)
public class AdminTraderController extends Controller {

  private final TraderService traderService;
  private final HandlerSellerService handlerSellerService;

  private final ObjectMapper jsonMapper;

  @Inject
  public AdminTraderController(TraderService traderService, HandlerSellerService handlerSellerService) {
    this.traderService = traderService;
    this.handlerSellerService = handlerSellerService;

    this.jsonMapper = new ObjectMapper();
  }

  // Annotation ensures that POST request is of type application/json. If not HTTP 400 response
  // returned.
  @BodyParser.Of(BodyParser.Json.class)
  public Result createTrader() {
    JsonNode data = request().body().asJson();

    if (data == null) {
      return badRequest(JsonMsgUtils.expectingData());
    }

    UserJsonParser parser = new UserJsonParser(data);

    if (!parser.isValid()) {
      return badRequest(JsonMsgUtils.caughtException(parser.getErrorMessage()));
    }

    Trader trader = parser.formTrader();
    trader.save();

    try {
      return created(jsonMapper.writeValueAsString(trader));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  public Result getAllTraders() {
    try {
      return ok(jsonMapper.writeValueAsString(traderService.getAll()));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  public Result getTrader(long id) {
    Trader trader = traderService.getById(id);

    if (trader == null) {
      return notFound(JsonMsgUtils.traderNotFoundMessage(id));
    }

    try {
      return ok(jsonMapper.writeValueAsString(trader));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  public Result getAllHandlerSellers(long traderId) {
    Trader trader = traderService.getById(traderId);

    if (trader == null) {
      return notFound(JsonMsgUtils.traderNotFoundMessage(traderId));
    }

    try {
      return ok(jsonMapper.writeValueAsString(handlerSellerService.getByTrader(traderId)));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  public Result getHandlerSeller(long traderId, long handlerSellerId) {
    Trader trader = traderService.getById(traderId);
    
    if (trader == null) {
      return notFound(JsonMsgUtils.traderNotFoundMessage(traderId));
    }

    HandlerSeller handlerSeller = handlerSellerService.getById(handlerSellerId);
    if (handlerSeller == null) {
      return notFound(JsonMsgUtils.handlerSellerNotFoundMessage(handlerSellerId));
    }

    if (!traderService.checkTraderOwnsHandlerSeller(trader, handlerSeller)) {
      return badRequest(JsonMsgUtils.traderDoesNotOwnHandlerMessage(trader, handlerSeller));
    }

    try {
      return ok(jsonMapper.writeValueAsString(handlerSeller));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }
}