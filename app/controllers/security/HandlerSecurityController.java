package controllers.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.inject.Inject;

import models.Handler;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import services.HandlerService;
import services.parsers.LoginJsonParser;

public class HandlerSecurityController extends Controller {

  public static final String AUTH_TOKEN_HEADER = "X-AUTH-TOKEN";
  public static final String AUTH_TOKEN = "auth_token";

  public static final String HANDLER_KEY = "handler";

  private final HandlerService handlerService;

  @Inject
  public HandlerSecurityController(HandlerService handlerService) {
    this.handlerService = handlerService;
  }


  public static Handler getHandler() {
    return (Handler)Http.Context.current().args.get(HANDLER_KEY);
  }

  // Returns an authToken
  @BodyParser.Of(BodyParser.Json.class)
  public Result login() {

    JsonNode data = request().body().asJson();

    if (data == null) {
      // TODO Change to Valid Error JSON
      return badRequest("Expecting Some Data.\n");
    }

    LoginJsonParser parser = new LoginJsonParser(data);

    if (!parser.isValid()) {
      // TODO Change to Valid Error JSON
      return badRequest(parser.getErrorMessage());
    }


    Handler handler = handlerService.getByEmailAddressAndPassword(
        parser.getEmailAddress(), parser.getPassword());

    if (handler == null) {
      // TODO Change to Invalid Login Credentials Error.
      return unauthorized();
    }

    String authToken = handler.createToken();

    response()
        .setCookie(Http.Cookie.builder(AUTH_TOKEN, authToken)
        .withSecure(ctx().request().secure())
        .build());

    ObjectNode authTokenJson = Json.newObject();
    authTokenJson.put(AUTH_TOKEN, authToken);

    return ok(authTokenJson);
  }

  @Security.Authenticated(HandlerSecured.class)
  public Result logout() {
    response().discardCookie(AUTH_TOKEN);

    Handler curHandler = getHandler();
    if (curHandler != null)
        curHandler.deleteAuthToken();

    return redirect("/");
  }
}
