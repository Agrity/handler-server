package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import controllers.security.AdminSecured;
import models.HandlerSeller;
import models.TraderBid;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import services.HandlerSellerService;
import services.TraderBidService;
import services.parsers.HandlerSellerJsonParser;

import utils.JsonMsgUtils;

@Security.Authenticated(AdminSecured.class)
public class AdminHandlerSellerController extends Controller {

  private final HandlerSellerService handlerSellerService;
  private final TraderBidService traderBidService;
  private final ObjectMapper jsonMapper;

  @Inject
  public AdminHandlerSellerController(HandlerSellerService handlerSellerService,
      TraderBidService traderBidService) {
    this.handlerSellerService = handlerSellerService;
    this.traderBidService = traderBidService;
    this.jsonMapper = new ObjectMapper();
  }

  // Annotation ensures that POST request is of type application/json. If not HTTP 400 response
  // returned.
  @BodyParser.Of(BodyParser.Json.class)
  public Result createHandlerSeller() {
    JsonNode data = request().body().asJson();

    if (data == null) {
      return badRequest(JsonMsgUtils.expectingData());
    }

    HandlerSellerJsonParser parser = new HandlerSellerJsonParser(data);

    if (!parser.isValid()) {
      return badRequest(JsonMsgUtils.caughtException(parser.getErrorMessage()));
    }

    HandlerSeller handlerSeller = parser.formHandlerSeller();
    handlerSeller.save();
    

    try {
      return created(jsonMapper.writeValueAsString(handlerSeller));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  public Result updateHandlerSeller(long handlerSellerId) {
    JsonNode data = request().body().asJson();

    if (data == null) {
      return badRequest(JsonMsgUtils.expectingData());
    }

    HandlerSeller handlerSeller = handlerSellerService.getById(handlerSellerId);
    if (handlerSeller == null) {
      return notFound(JsonMsgUtils.handlerSellerNotFoundMessage(handlerSellerId));
    }

    HandlerSellerJsonParser parser = new HandlerSellerJsonParser(data);

    if (!parser.isValid()) {
      return badRequest(JsonMsgUtils.caughtException(parser.getErrorMessage()));
    }

    for(TraderBid traderBid: traderBidService.getByHandlerSeller(handlerSellerId)) {
      if(traderBid.bidCurrentlyOpen()) {
        //Conflict response
        return status(409, JsonMsgUtils.handlerSellerInBid(handlerSellerId, traderBid.getId()));
      }
    }

    parser.updateHandlerSeller(handlerSeller);
    handlerSeller.save();

    try {
      return created(jsonMapper.writeValueAsString(handlerSeller));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }

  }

  public Result deleteHandlerSeller(long handlerSellerId) {
    HandlerSeller handlerSeller = handlerSellerService.getById(handlerSellerId);
    
    if (handlerSeller == null) {
      return notFound(JsonMsgUtils.handlerSellerNotFoundMessage(handlerSellerId));
    }

    for(TraderBid traderBid: traderBidService.getByHandlerSeller(handlerSellerId)) {
      if (traderBid.bidCurrentlyOpen()) {
        //Conflict response
        return status(409, JsonMsgUtils.handlerSellerInBid(handlerSellerId, traderBid.getId()));
      }
    }

    handlerSeller.delete();
    return ok(JsonMsgUtils.handlerSellerDeleted(handlerSellerId));
  }

  public Result getAllHandlerSellers() {
    try {
      return created(jsonMapper.writeValueAsString(handlerSellerService.getAll()));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }

  public Result getHandlerSeller(long id) {
    HandlerSeller handlerSeller = handlerSellerService.getById(id);

    if (handlerSeller == null) {
      return notFound(JsonMsgUtils.handlerSellerNotFoundMessage(id));
    }

    try {
      return created(jsonMapper.writeValueAsString(handlerSeller));
    } catch (JsonProcessingException e) {
      return internalServerError(JsonMsgUtils.caughtException(e.toString()));
    }
  }
}
