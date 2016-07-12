package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import controllers.security.AdminSecured;
import models.Trader;
import models.Handler;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import services.TraderService;
import services.HandlerService;
import services.parsers.UserJsonParser;

import utils.JsonMsgUtils;

@Security.Authenticated(AdminSecured.class)
public class AdminTraderController extends Controller {

  private final TraderService traderService;
  private final HandlerService handlerService;

  private final ObjectMapper jsonMapper;

  @Inject
  public AdminTraderController(TraderService traderService, HandlerService handlerService) {
    this.traderService = traderService;
    this.handlerService = handlerService;

    this.jsonMapper = new ObjectMapper();
  }
}