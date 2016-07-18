package controllers.security;

import com.google.inject.Inject;

import models.Trader;

import play.Logger;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

import services.TraderService;

import utils.ResponseHeaders;

public class TraderSecured extends Security.Authenticator {

  private TraderService traderService;

  @Inject
  public TraderSecured(TraderService traderService) {
    this.traderService = traderService;
  }

  @Override
  public String getUsername(Context ctx) {
    String authTokenHeaderValue
      = ctx.request().getHeader(TraderSecurityController.AUTH_TOKEN_HEADER);

    Logger.debug("Recieved Token: " + authTokenHeaderValue);
    if (authTokenHeaderValue != null) {
      Trader trader = traderService.getByAuthToken(authTokenHeaderValue);
      if (trader != null) {
        ctx.args.put(TraderSecurityController.TRADER_KEY, trader);
        return trader.getEmailAddress().toString();
      } else {
        Logger.error("No Trader With Auth Token: " + authTokenHeaderValue);
      }
    } 

    return null;
  }

  @Override
  public Result onUnauthorized(Context ctx) {
    ResponseHeaders.addResponseHeaders(ctx.response());

    // TODO Redirect to Login.
    Logger.info("Unauthorized Access");
    return unauthorized("Please Log In");
  }

}
