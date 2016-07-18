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

import services.bid_management.WaterfallService;
import services.bid_management.FCFSService;
import services.bid_management.BidManagementService;

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
    Logger.info("parsing expiration time...\n\n");

    //TODO fix Error checking for existance of MANAGEMENT_TYPE? seems repetitive/ask ryan

    JsonNode typeMap = data.get(BidJsonConstants.MANAGEMENT_TYPE);
    if(typeMap.has(BidJsonConstants.DELAY_KEY)) {
      int delayInt = typeMap.get(BidJsonConstants.DELAY_KEY).asInt();
      return LocalDateTime.now().plusMinutes(delayInt);
    }
    setInvalid(missingParameterError(BidJsonConstants.DELAY_KEY));
    return null;
  }

  protected ManagementTypeInfo parseManagementType(JsonNode data) {
    Logger.info("parsing management type...\n\n");
    if (!data.has(BidJsonConstants.MANAGEMENT_TYPE)) {
      setInvalid(missingParameterError(BidJsonConstants.MANAGEMENT_TYPE));
      return null;
    }

    JsonNode typeMap = data.get(BidJsonConstants.MANAGEMENT_TYPE);

    if(!typeMap.has(BidJsonConstants.DELAY_KEY)) {
      setInvalid(missingParameterError(BidJsonConstants.DELAY_KEY));
      return null;
    }
    int delayInt = typeMap.get(BidJsonConstants.DELAY_KEY).asInt();
    
    if(typeMap.has(BidJsonConstants.TYPE_KEY)) {
      String className = typeMap.get(BidJsonConstants.TYPE_KEY).asText();
      Duration delayTime = Duration.ofMinutes(delayInt);
      switch(className) {
        case BidJsonConstants.ManagementTypes.WATERFALL:
          return new ManagementTypeInfo(WaterfallService.class, delayTime);
        case BidJsonConstants.ManagementTypes.FCFS: 
          return new ManagementTypeInfo(FCFSService.class, delayTime);
        default:
          setInvalid("Management Type invalid: specified type " + className +" not found\n");
          return null;          
      }
    } 

    setInvalid(missingParameterError(BidJsonConstants.TYPE_KEY));
    return null;
  }


  private static class BidJsonConstants {
    private static final String ALMOND_VARIETY = "almond_variety";
    private static final String ALMOND_SIZE = "almond_size";
    private static final String ALMOND_POUNDS = "almond_pounds";
    private static final String PRICE_PER_POUND = "price_per_pound";
    private static final String COMMENT = "comment";

    private static final String MANAGEMENT_TYPE = "management_type";
    
    private static final String TYPE_KEY = "type";
    private static final String DELAY_KEY = "delay";

    public static class ManagementTypes {
      private static final String WATERFALL = "WaterfallService";
      private static final String FCFS = "FCFSService";
    }
  }
  public static class ManagementTypeInfo {
    private Class<? extends BidManagementService> typeClass;
    private Duration delay;

      ManagementTypeInfo(Class<? extends BidManagementService> typeClass, Duration delay) {
        this.typeClass = typeClass;
        this.delay = delay;
      }

      public Class<? extends BidManagementService> getClassType() { return typeClass; }
      public String className() { return typeClass.getName(); }
      public Duration getDelay() { return delay; }

  }
  
}