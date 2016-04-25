package controllers;

import com.avaje.ebean.Ebean;

import java.util.List;

import models.EmailAddress;
import models.Grower;
import models.GrowerOfferResponse;
import models.Handler;
import models.Offer;

import play.db.ebean.Model;
// TODO Remove *
import play.mvc.*;

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

        List<GrowerOfferResponse> growerResponseList = GrowerOfferResponse.find.all();
        Ebean.delete(growerResponseList);

        List<EmailAddress> emailList = EmailAddress.find.all();
        Ebean.delete(emailList);

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
