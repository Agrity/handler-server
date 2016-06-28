package controllers.security;

import com.google.inject.Inject;

import models.Handler;

import play.Logger;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

import services.HandlerService;

import utils.ResponseHeaders;

public class HandlerSecured extends Security.Authenticator {

  private HandlerService handlerService;

  @Inject
  public HandlerSecured(HandlerService handlerService) {
    this.handlerService = handlerService;
  }

  @Override
  public String getUsername(Context ctx) {
    String authTokenHeaderValue
      = ctx.request().getHeader(HandlerSecurityController.AUTH_TOKEN_HEADER);

    Logger.debug("Recieved Token: " + authTokenHeaderValue);
    if (authTokenHeaderValue != null) {
      Handler handler = handlerService.getByAuthToken(authTokenHeaderValue);
      if (handler != null) {
        ctx.args.put(HandlerSecurityController.HANDLER_KEY, handler);
        return handler.getEmailAddress();
      } else {
        Logger.error("No Handler With Auth Token: " + authTokenHeaderValue);
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
