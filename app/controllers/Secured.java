package controllers;

import models.Handler;

import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

import services.HandlerService;

public class Secured extends Security.Authenticator {

  private HandlerService handlerService;

  public Secured(HandlerService handlerService) {
    this.handlerService = handlerService;
  }

  @Override
  public String getUsername(Context ctx) {
    String authTokenHeaderValue
      = ctx.request().getHeader(SecurityController.AUTH_TOKEN_HEADER);

    if (authTokenHeaderValue != null) {
      Handler handler = handlerService.getByAuthToken(authTokenHeaderValue);
      if (handler != null) {
        ctx.args.put(SecurityController.HANDLER_KEY, handler);
        return handler.getEmailAddress();
      }
    }

    return null;
  }

  @Override
  public Result onUnauthorized(Context ctx) {
    // TODO Redirect to Login.
    return unauthorized();
  }

}
