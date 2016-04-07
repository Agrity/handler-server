package services;

import java.util.List;

import models.Grower;
import models.Handler;

import play.mvc.Controller;


public class HandlerService extends Controller {

  private static Integer curHandlerNum = 0;

  public static List<Handler> getAllHandlers() {
    return Handler.find.all();
  }

  public static Handler getHandler(Long id) {
    Handler handler = Handler.find.byId(id);


    return handler;
  }

  public static Handler createHandler(Long id) {
    return Handler.find.byId(id);
  }

  public static Handler createFakeHandler() {
    Handler fakeHandler = new Handler("TestHandler " + curHandlerNum);
    fakeHandler.save();

    curHandlerNum++;

    return fakeHandler;
  }

  public static boolean checkHandlerOwnsGrower(Handler handler, Grower grower) {
    return handler.getId() == grower.getHandler().getId();
  }
}
