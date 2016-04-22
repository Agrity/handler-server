package controllers;
import controllers.ErrorMessages;

import models.Handler;
// TODO Remove *
import play.mvc.*;

import services.HandlerService;

public class HtmlOfferController extends Controller {

    public Result handlerOfferList(long handlerId) {
      Handler handler = HandlerService.getHandler(handlerId);

      if (handler == null) {
        return badRequest(ErrorMessages.handlerNotFoundMessage(handlerId));
      }

      return ok(views.html.offers.offerList.render(handler.getOfferList()));
    }
}
