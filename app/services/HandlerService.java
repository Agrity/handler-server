package services;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

import models.DBConstants;
import models.Grower;
import models.Handler;

import play.mvc.Controller;
import play.mvc.Result;

import services.parsers.HandlerJsonParser;


// TODO Figure out how to construct Result without needing to extend Controller.
public class HandlerService extends Controller {

  // Used by createFakeHandler() for testing.
  private static Integer curHandlerNum = 0;

  public static List<Handler> getAllHandlers() {
    return Handler.find.all();
  }

  public static Handler getHandler(Long id) {
    Handler handler = Handler.find.byId(id);

    return handler;
  }

  public static Handler getHandler(String companyName) {
    List<Handler> handler =
        Handler.find.where().like(DBConstants.HandlerColumns.COMPANY_NAME, companyName).findList();

    return handler.isEmpty() ? null : handler.get(0);
  }

  /*
   * Testing Function.
   */
  public static Handler createFakeHandler() {
    Handler fakeHandler = new Handler("TestHandler " + curHandlerNum);
    fakeHandler.save();

    curHandlerNum++;

    return fakeHandler;
  }

  public static boolean checkHandlerOwnsGrower(Handler handler, Grower grower) {
    return handler.getId() == grower.getHandler().getId();
  }

  public static Result createHandlerResult(JsonNode data) {
    HandlerJsonParser parser = new HandlerJsonParser(data);
    if (!parser.isValid()) {
      return badRequest(parser.getErrorMessage());
    }

    Handler handler = createHandler(parser);

    return created("Handler Created: " + handler + "\n");
  }

  /*
   * WARNING: Parser paramerter must be a valid parser, otherwise runtime exception will be thrown.
   */
  private static Handler createHandler(HandlerJsonParser parser) {
    Handler newHandler = new Handler(parser.getCompanyName());
    newHandler.save();

    return newHandler;
  }
}
