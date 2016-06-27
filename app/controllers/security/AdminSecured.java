package controllers.security;

import play.Logger;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class AdminSecured extends Security.Authenticator {

  public static final String AUTH_TOKEN_HEADER = "X-ADMIN-TOKEN";

  private static final String ADMIN_SECRET = "development-use-only";

  @Override
  public String getUsername(Context ctx) {
    String authTokenHeaderValue
      = ctx.request().getHeader(AUTH_TOKEN_HEADER);

    if (authTokenHeaderValue != null) {
      if (authTokenHeaderValue.equals(ADMIN_SECRET)) {
        return "ADMIN";
      } else {
        Logger.error("Incorrect Admin Credentials");
      }
    } 

    return null;
  }

  @Override
  public Result onUnauthorized(Context ctx) {
    // TODO Redirect to Login.
    Logger.info("Unauthorized Admin Access");
    return unauthorized("Admin Only");
  }

}
