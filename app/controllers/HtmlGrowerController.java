package controllers;
import controllers.ErrorMessages;

import models.Grower;
import models.Handler;
// TODO Remove *
import play.mvc.*;

import services.GrowerService;
import services.HandlerService;

public class HtmlGrowerController extends Controller {

    public Result handlerGrowerList(long handlerId) {
      Handler handler = HandlerService.getHandler(handlerId);

      if (handler == null) {
        return badRequest(ErrorMessages.handlerNotFoundMessage(handlerId));
      }

      return ok(views.html.growers.growerList.render(handler.getGrowersList()));
    }

    public Result growerView(long growerId) {
      Grower grower = GrowerService.getGrower(growerId);

      if (grower == null) {
        return badRequest(ErrorMessages.growerNotFoundMessage(growerId));
      }

      return ok(views.html.growers.growerView.render(grower));
    }
}
