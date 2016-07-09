package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import services.OfferService;
import services.messaging.offer.OfferMessageService;

import models.Offer;

import utils.JsonMsgUtils;

public class BidController extends Controller {
  
  private final OfferService offerService;
  private final OfferMessageService offerMessageService;

  private final ObjectMapper jsonMapper;

  @Inject
  public BidController(OfferService offerService, OfferMessageService offerMessageService) {
    this.offerService = offerService;
    this.offerMessageService = offerMessageService;

    this.jsonMapper = new ObjectMapper();
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

  // TODO Not Secured. Implement non-admin means of responding to offer.
  public Result rejectOffer(long offerId, long growerId) {
    Offer offer = offerService.getById(offerId);
    if (offer == null) {
      return notFound(JsonMsgUtils.offerNotFoundMessage(offerId));
    }

    OfferResponseResult success = offer.growerRejectOffer(growerId);

    return success.isValid() ? ok(JsonMsgUtils.successfullReject())
        : internalServerError(JsonMsgUtils.offerNotRejected(success.getInvalidResponseMessage()));
  }

}