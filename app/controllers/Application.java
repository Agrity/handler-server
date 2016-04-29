package controllers;

import com.avaje.ebean.Ebean;

import java.util.List;

import models.EmailAddress;
import models.Grower;
import models.OfferResponse;
import models.Handler;
import models.Offer;
import play.Logger;
// TODO Remove *
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

    public Result index() {
      return ok(views.html.index.render());
    }

    public Result reset() {
      if (!init()) {
        List<Grower> growerList = Grower.find.all();
        Ebean.delete(growerList);

        List<Offer> offerList = Offer.find.all();
        Ebean.delete(offerList);

        List<OfferResponse> growerResponseList = OfferResponse.find.all();
        Ebean.delete(growerResponseList);

        List<EmailAddress> emailList = EmailAddress.find.all();
        Ebean.delete(emailList);

        Logger.debug("Handlers: " + Handler.find.all() + "\n\n");
        Logger.debug("Growers: " + Grower.find.all() + "\n\n");
        Logger.debug("GrowerOfferResponse: " + OfferResponse.find.all() + "\n\n");
        Logger.debug("EmailAddress: " + EmailAddress.find.all() + "\n\n");

      }

      return redirect("/");
    }

    private boolean init() {
      if (Handler.find.all().isEmpty()) {
        Handler handler = new Handler("Example Company");
        handler.save();
        return true;
      }
      return false;
    }
}
