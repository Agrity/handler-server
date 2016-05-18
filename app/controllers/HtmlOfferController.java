package controllers;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import controllers.ErrorMessages;

import models.Almond;
import models.Almond.AlmondVariety;
import models.Grower;
import models.Handler;
import models.Offer;

import play.data.Form;
// TODO Remove *
import play.mvc.*;

import services.GrowerService;
import services.HandlerService;
import services.OfferService;

public class HtmlOfferController extends Controller {

    public Result handlerOfferList(long handlerId) {
      Handler handler = HandlerService.getHandler(handlerId);

      if (handler == null) {
        return badRequest(ErrorMessages.handlerNotFoundMessage(handlerId));
      }

      return ok(views.html.offers.offerList.render(/*handler.getOfferList()*/null));
    }

    public Result offerView(long offerId) {
      Offer offer = OfferService.getOffer(offerId);

      if (offer == null) {
        return badRequest(ErrorMessages.offerNotFoundMessage(offerId));
      }

      return ok(views.html.offers.offerView.render(offer));
    }

  public Result offerCreateForm() {
    Handler handler = HandlerService.getHandler(1L);

    if (handler == null) {
      return badRequest(ErrorMessages.handlerNotFoundMessage(1L));
    }

    return ok(views.html.offers.offerCreate.render(handler));
  }

  public Result offerCreate() {
    // TODO Change from hardcode
    long handlerId = 1L;
    Handler handler = HandlerService.getHandler(handlerId);

    if (handler == null) {
      return badRequest(ErrorMessages.handlerNotFoundMessage(handlerId));
    }

    Form offerForm = Form.form().bindFromRequest();

    String almondVarietyStr = getFieldValue(offerForm, "almond_variety");
    if (almondVarietyStr == null) 
      return badRequest("Almond Variety Field Not Present");

    AlmondVariety almondVariety = Almond.stringToAlmondVariety(almondVarietyStr);
    if (almondVariety == null) 
      return badRequest("Almond Variety Field Not Valid Almond Type");


    String pricePerPound = getFieldValue(offerForm, "price_per_pound");
    if (pricePerPound == null) 
      return badRequest("Price Per Pound Field Not Present");

    if (!pricePerPound.contains("$")) {
      pricePerPound = "$" + pricePerPound;
    }

    String comment = getFieldValue(offerForm, "comment");
    if (comment == null) 
      comment = "";

    String almondPoundsStr = getFieldValue(offerForm, "almond_pounds");
    if (almondPoundsStr == null) 
      return badRequest("Almond Pounds Field Not Present");

    Long almondPounds = parseLong(almondPoundsStr);
    if (almondPounds == null) 
      return badRequest("Almond Pounds Field Not a Valid Integer");
    
    List<Long> growerIds = getGrowerIds(offerForm);
    List<Grower> growers = new ArrayList<>();
    for (Long id : growerIds) {
      Grower grower = GrowerService.getGrower(id);
      if (grower == null)
        return badRequest("Invalid Grower ID: " + id);

      growers.add(grower);
    }


    Offer offer = new Offer(handler, growers, almondVariety, almondPounds.intValue(), pricePerPound, LocalDate.now(), comment);
    offer.save();

    return redirect("/html/offers/" + offer.getId());
  }

  private static String getFieldValue(Form form, String fieldName) {
    Form.Field field = form.field(fieldName);
    return field == null ? null : field.value();
  }

  private static List<Long> getGrowerIds(Form form) {
    List<Long> growerIds = new ArrayList<>();
    int i = 0;
    while(form.field("growers["+i+"]").value() != null) {
       growerIds.add(parseLong(form.field("growers["+i+"]").value()));
       i++;
    }
    return growerIds;
  }

  private static Long parseLong(String numStr) {
    if (numStr == null) {
      return null;
    }

    try {
      Long num = Long.parseLong(numStr);
      return num;

    } catch (NumberFormatException e) {
      return null;
    }
  }

}
