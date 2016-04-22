package services;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

import models.Offer;

import play.mvc.Controller;
import play.mvc.Result;

import services.parsers.OfferJsonParser;


public class OfferService extends Controller {

  public static List<Offer> getAllOffers() {
    return Offer.find.all();
  }

  public static Offer getOffer(Long id) {
    return Offer.find.byId(id);
  }

  public static Result createOfferResult(JsonNode data) {
    OfferJsonParser parser = new OfferJsonParser(data);

    if (!parser.isValid()) {
      return badRequest(parser.getErrorMessage());
    }

    Offer offer = createOffer(parser);

    return created("Offer Created: " + offer + "\n");
  }

  private static Offer createOffer(OfferJsonParser parser) {
    if (!parser.isValid()) {
      throw new RuntimeException("Attempted to create Offer from invalid parser.\n");
    }

    Offer newOffer = new Offer(
        parser.getHandler(),
        parser.getGrowers(),
        parser.getAlmondVariety(),
        parser.getAlmondPounds(),
        parser.getPricePerPound(),
        parser.getPaymentDate());

    

    newOffer.save();
    return newOffer;
  }
}
