package utils;

import play.mvc.Http.Response;

public abstract class ResponseHeaders {

  public static void addResponseHeaders(Response response) {
    response.setHeader("Access-Control-Allow-Origin","*");
    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    response.setHeader("Access-Control-Allow-Headers", "X-ADMIN-TOKEN, X-HANDLER-TOKEN, X-TRADER-TOKEN, Content-Type");
  }
}
