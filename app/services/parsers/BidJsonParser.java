package services.parsers;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import models.Almond;
import models.Almond.AlmondVariety;

import play.Logger;

import services.DateService;
import services.GrowerService;
import services.impl.EbeanGrowerService;

public class BidJsonParser extends BaseParser {
  
  public BidJsonParser() {
    super();
  }

  protected AlmondVariety parseAlmondVariety(JsonNode data) {
    // Check almound variety is preseent.
    Logger.info("parsing almond variety...\n\n");
    if (!data.has(BidJsonConstants.ALMOND_VARIETY)) {
      setInvalid(missingParameterError(BidJsonConstants.ALMOND_VARIETY));
      return null;
    } 
    
    AlmondVariety almondVariety =
        Almond.stringToAlmondVariety(data.get(BidJsonConstants.ALMOND_VARIETY).asText());

    if (almondVariety == null) {
      setInvalid("Almond Variety Format Invalid: string of valid almond variety expected.\n");
      return null;
    }

    return almondVariety;
  }

  protected String parseAlmondSize(JsonNode data) {
    // Check almound size is present.
    Logger.info("parsing almond size...\n\n");
    if (!data.has(BidJsonConstants.ALMOND_SIZE)) {
      setInvalid(missingParameterError(BidJsonConstants.ALMOND_SIZE));
      return null;
    } 

    // TODO Add checks to verify correctly formatted almond size.
    
    return data.get(BidJsonConstants.ALMOND_SIZE).asText();
  }

  protected Integer parseAlmondPounds(JsonNode data) {
    // Check almound pounds is present.
    Logger.info("parsing almond pounds...\n\n");
    if (!data.has(BidJsonConstants.ALMOND_POUNDS)) {
      setInvalid(missingParameterError(BidJsonConstants.ALMOND_POUNDS));
      return null;

    } 
    
    Long almondPounds = parseLong(data.get(BidJsonConstants.ALMOND_POUNDS).asText());

    if (almondPounds == null) {
      setInvalid("Almond Pound Format Invalid: integer expected.\n");
      return null;
    }

    return almondPounds.intValue();
  }

  protected String parsePricePerPound(JsonNode data) {
    // Check price per pound is present.
    Logger.info("parsing price per pound...\n\n");
    if (!data.has(BidJsonConstants.PRICE_PER_POUND)) {
      setInvalid(missingParameterError(BidJsonConstants.PRICE_PER_POUND));
      return null;

    } 
    
    // TODO Change To Monetary Value
    String pricePerPound = data.get(BidJsonConstants.PRICE_PER_POUND).asText();

    if (pricePerPound == null) {
      setInvalid("Almond Price Per Pound Format Invalid: monetary value string expected.\n");
      return null;
    }

    return "$" + pricePerPound;
  }

  protected String parseComment(JsonNode data) {
    Logger.info("parsing comment...\n\n");
    // Check comment is present.
    if (!data.has(BidJsonConstants.COMMENT)) {
      return "";
    } 
    
    String commentString = data.get(BidJsonConstants.COMMENT).asText();

    if (commentString == null) {
      setInvalid("Comment Format Invalid: string format expected.\n");
      return null;
    }

    return commentString;
  }

  protected LocalDateTime parseExpirationTime(JsonNode data) {
    JsonNode typeMap = data.get(BidJsonConstants.MANAGEMENT_TYPE);
    if(typeMap.has(BidJsonConstants.DELAY_KEY)) {
      int delayInt = typeMap.get(BidJsonConstants.DELAY_KEY).asInt();
      return LocalDateTime.now().plusMinutes(delayInt);
    }
    setInvalid(missingParameterError(BidJsonConstants.DELAY_KEY));
    return null;
  }

  protected static class BidJsonConstants {
    private static final String ALMOND_VARIETY = "almond_variety";
    private static final String ALMOND_SIZE = "almond_size";
    private static final String ALMOND_POUNDS = "almond_pounds";
    private static final String PRICE_PER_POUND = "price_per_pound";
    private static final String COMMENT = "comment";

    protected static final String MANAGEMENT_TYPE = "management_type";
    
    protected static final String TYPE_KEY = "type";
    protected static final String DELAY_KEY = "delay";

    public static class ManagementTypes {
      protected static final String WATERFALL = "WaterfallService";
      protected static final String FCFS = "FCFSService";
    }
  }
}
