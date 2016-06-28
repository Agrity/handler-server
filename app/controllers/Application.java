package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

  public Result home() {
    return ok("Home.");
  }

  public Result options(String path) {
    return ok("")
      .withHeader("Access-Control-Allow-Origin","*")
      .withHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
      .withHeader("Access-Control-Allow-Headers", "X-ADMIN-TOKEN, X-HANDLER-TOKEN, Content-Type");
  }

  public Result reset() {
    //if (!init()) {
    //  List<Grower> growerList = Grower.find.all();
    //  Ebean.delete(growerList);

    //  List<Offer> offerList = Offer.find.all();
    //  Ebean.delete(offerList);

    //  List<OfferResponse> growerResponseList = OfferResponse.find.all();
    //  Ebean.delete(growerResponseList);

    //  List<EmailAddress> emailList = EmailAddress.find.all();
    //  Ebean.delete(emailList);

    //  Logger.debug("Handlers: " + Handler.find.all() + "\n\n");
    //  Logger.debug("Growers: " + Grower.find.all() + "\n\n");
    //  Logger.debug("GrowerOfferResponse: " + OfferResponse.find.all() + "\n\n");
    //  Logger.debug("EmailAddress: " + EmailAddress.find.all() + "\n\n");

    //}

    return redirect("/");
  }
}
