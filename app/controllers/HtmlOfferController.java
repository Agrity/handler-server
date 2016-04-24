package controllers;
import controllers.ErrorMessages;

import models.Handler;
import models.Offer;
// TODO Remove *
import play.mvc.*;

import services.HandlerService;
import services.OfferService;

public class HtmlOfferController extends Controller {

    public Result handlerOfferList(long handlerId) {
      Handler handler = HandlerService.getHandler(handlerId);

      if (handler == null) {
        return badRequest(ErrorMessages.handlerNotFoundMessage(handlerId));
      }

      return ok(views.html.offers.offerList.render(handler.getOfferList()));
    }

    public Result offerView(long offerId) {
      Offer offer = OfferService.getOffer(offerId);

      if (offer == null) {
        return badRequest(ErrorMessages.offerNotFoundMessage(offerId));
      }

      return ok(views.html.offers.offerView.render(offer));
    }
}
