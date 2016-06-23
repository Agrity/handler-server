package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Handler;

import play.data.Form;
import play.data.FormFactory;
import play.data.validation.Constraints;
import play.libs.Json;
import play.mvc.*;

import services.parsers.LoginJsonParser;

import javax.inject.Inject;

public class SecurityController extends Controller {

  @Inject FormFactory formFactory;

  public final static String AUTH_TOKEN_HEADER = "X-AUTH-TOKEN";
  public static final String AUTH_TOKEN = "authToken";


  public static Handler getHandler() {
    return (Handler)Http.Context.current().args.get("handler");
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


    //User user = User.findByEmailAddressAndPassword(login.emailAddress, login.password);

    //if (user == null) {
    //    return unauthorized();
    //}
    //else {
    //    String authToken = user.createToken();
    //    ObjectNode authTokenJson = Json.newObject();
    //    authTokenJson.put(AUTH_TOKEN, authToken);
    //    response().setCookie(Http.Cookie.builder(AUTH_TOKEN, authToken).withSecure(ctx().request().secure()).build());
    //    return ok(authTokenJson);
    //}
    return null;
  }

  @Security.Authenticated(Secured.class)
  public Result logout() {
    response().discardCookie(AUTH_TOKEN);
    //getUser().deleteAuthToken();
    return redirect("/");
  }

  public static class Login {

    @Constraints.Required
    @Constraints.Email
    public String emailAddress;

    @Constraints.Required
    public String password;

  }

}
