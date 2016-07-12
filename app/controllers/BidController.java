package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import services.OfferService;
import services.GrowerService;

import models.Offer;
import models.OfferResponseResult;
import models.Grower;

import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import utils.JsonMsgUtils;
import services.messaging.MessageServiceConstants;

public class BidController extends Controller {
  
  private final OfferService offerService;
  private final GrowerService growerService;

 // private final ObjectMapper jsonMapper;

  @Inject
  public BidController(OfferService offerService, GrowerService growerService) {
    this.offerService = offerService;
    this.growerService = growerService;

  //  this.jsonMapper = new ObjectMapper();
  }

  public Result acceptOffer(long offerId, long growerId) {
    Offer offer = offerService.getById(offerId);
    if (offer == null) {
      return notFound(JsonMsgUtils.offerNotFoundMessage(offerId));
    }

    // TODO Change to actual pounds accepted once implemented.
    OfferResponseResult success = offer.growerAcceptOffer(growerId, offer.getAlmondPounds());
    
    return success.isValid() ? ok(JsonMsgUtils.successfullAccept())
        : internalServerError(JsonMsgUtils.offerNotAccepted(success.getInvalidResponseMessage()));
  }

  public Result rejectOffer(long offerId, long growerId) {
    Offer offer = offerService.getById(offerId);
    if (offer == null) {
      return notFound(JsonMsgUtils.offerNotFoundMessage(offerId));
    }

    OfferResponseResult success = offer.growerRejectOffer(growerId);

    return success.isValid() ? ok(JsonMsgUtils.successfullReject())
        : internalServerError(JsonMsgUtils.offerNotRejected(success.getInvalidResponseMessage()));
  }

  public Result displayPartialPage(long offerId, long growerId) {
    Offer offer = offerService.getById(offerId);
    if (offer == null) {
      return notFound(JsonMsgUtils.offerNotFoundMessage(offerId));
    }
    Grower grower = growerService.getById(growerId);
    if (grower == null) {
      return notFound(JsonMsgUtils.offerNotFoundMessage(growerId));
    }
    return ok(views.html.partialAcceptPage.render(offer, grower, MessageServiceConstants.EmailFields.getDomain()));
  }

}