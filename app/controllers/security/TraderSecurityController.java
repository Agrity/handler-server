package controllers.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.inject.Inject;

import models.Trader;

import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import services.TraderService;
import services.parsers.LoginJsonParser;

import utils.ResponseHeaders;

public class TraderSecurityController extends Controller {

  public static final String AUTH_TOKEN_HEADER = "X-TRADER-TOKEN";
  public static final String AUTH_TOKEN = "auth_token";

  public static final String TRADER_KEY = "trader";

  private final TraderService traderService;

  @Inject
  public TraderSecurityController(TraderService traderService) {
    this.traderService = traderService;
  }

  public static Trader getTrader() {
    return (Trader)Http.Context.current().args.get(TRADER_KEY);
  }

  // Returns an authToken
  @BodyParser.Of(BodyParser.Json.class)
  public Result login() {
    ResponseHeaders.addResponseHeaders(response());

    JsonNode data = request().body().asJson();

    if (data == null) {
      // TODO Change to Valid Error JSON
      return badRequest("Expecting Some Data.\n");
    }

    LoginJsonParser parser = new LoginJsonParser(data);

    if (!parser.isValid()) {
      // TODO Change to Valid Error JSON
      Logger.info("Login Parsing Error: " + parser.getErrorMessage());
      return badRequest(parser.getErrorMessage());
    }

    Trader trader = traderService.getByEmailAddressAndPassword(
        parser.getEmailAddress().toLowerCase(), parser.getPassword());

    if (trader == null) {
      // TODO Change to Invalid Login Credentials Error.
      Logger.error("Invalid Login Credentials.");
      return unauthorized();
    }

    String authToken = trader.createToken();

    response()
        .setCookie(Http.Cookie.builder(AUTH_TOKEN, authToken)
        .withSecure(ctx().request().secure())
        .build());

    ObjectNode authTokenJson = Json.newObject();
    authTokenJson.put(AUTH_TOKEN, authToken);

    return ok(authTokenJson);
  }

  @Security.Authenticated(TraderSecured.class)
  public Result logout() {
    ResponseHeaders.addResponseHeaders(response());

    response().discardCookie(AUTH_TOKEN);

    Trader curTrader = getTrader();
    if (curTrader != null)
        curTrader.deleteAuthToken();


    return ok("Successfully logged out.");
  }
}
