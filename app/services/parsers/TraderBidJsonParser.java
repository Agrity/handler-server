package services.parsers;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import models.Almond;
import models.Almond.AlmondVariety;
import models.Trader;
import models.HandlerSeller;
import models.TraderBid;

import play.Logger;

import services.DateService;
import services.GrowerService;
import services.impl.EbeanGrowerService;

import java.util.Date;

/**
 * Class to parse json data to create new HandlerBid.
 *
 * Expected Json Structure:
 *  {
 *    TRADER_ID: ... , (Constant in JsonParser super class)
 *
 *    HANDLERSELLER_IDS: [
 *      ... ,
 *      ... ,
 *      ...
 *    ],
 *
 *    ALMOND_VARIETY: ... ,
 *
 *    (TODO Change from String to Actual Almond Size)
 *    ALMOND_SIZE: ... ,
 *
 *    ALMOND_POUNDS: ... ,
 *
 *    ALMOND_PRICE_PER_POUND: ... ,
 *
 *    ==== OPTIONAL ====
 *
 *    COMMENT: ... ,
 *  }
 */
public class TraderBidJsonParser extends BidJsonParser {

  private AlmondVariety almondVariety;
  private String almondSize;
  private Integer almondPounds;
  private String pricePerPound;
  private String comment;

  public TraderBidJsonParser(JsonNode data) {
    super();
  }
}