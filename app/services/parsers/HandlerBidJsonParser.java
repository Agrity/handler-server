package services.parsers;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import models.Almond;
import models.Almond.AlmondVariety;
import models.Grower;
import models.Handler;
import models.HandlerBid;

import play.Logger;

import services.DateService;
import services.GrowerService;
import services.impl.EbeanGrowerService;
import services.bid_management.WaterfallService;
import services.bid_management.FCFSService;
import services.bid_management.BidManagementService;

import java.util.Date;

/**
 * Class to parse json data to create new HandlerBid.
 *
 * Expected Json Structure:
 *  {
 *    HANDLER_ID: ... , (Constant in JsonParser super class)
 *
 *    GROWER_IDS: [
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
 *    (TODO Determine Date Format)
 *    PAYMENT_DATE: ... ,
 *
 *    MANAGEMENT_TYPE: {
 *      TYPE: ...
 *      DELAY: ...
 *    },
 *
 *    ==== OPTIONAL ====
 *
 *    COMMENT: ... ,
 *  }
 */
public class HandlerBidJsonParser extends BaseParser {

  private Handler handler;
  private List<Grower> growers;
  private AlmondVariety almondVariety;
  private String almondSize;
  private Integer almondPounds;
  private String pricePerPound;
  private LocalDate startPaymentDate;
  private LocalDate endPaymentDate;
  private ManagementTypeInfo managementType;
  private LocalDateTime expirationTime;
  private String comment;

  private final GrowerService growerService;

  public HandlerBidJsonParser(JsonNode data) {
    super();

    // TODO -- Extremely Hacky -- Change to Dependency Injection.
    //      See Guice AssistedInjection
    growerService = new EbeanGrowerService();

    handler = parseHandler(data);
    if (handler == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    growers = parseGrowers(data);
    if (growers == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    almondVariety = parseAlmondVariety(data);
    if (almondVariety == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    almondSize = parseAlmondSize(data);
    if (almondSize == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    almondPounds = parseAlmondPounds(data);
    if (almondPounds == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    pricePerPound = parsePricePerPound(data);
    if (pricePerPound == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    startPaymentDate = parseStartPaymentDate(data);
    if (startPaymentDate == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    endPaymentDate = parseEndPaymentDate(data);
    if (endPaymentDate == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    managementType = parseManagementType(data);
    if(managementType == null) {
      //Parser set to invalid with proper error message.
      return;
    }

    comment = parseComment(data);
    if (comment == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    // Valid json data recieved
    setValid();
  }

  public HandlerBid formBid() {
    if (!isValid()) {
      throw new RuntimeException("Attempted to create bid from invalid parser.\n");
    }

    HandlerBid newBid = new HandlerBid(
        getHandler(),
        getGrowers(),
        getAlmondVariety(),
        getAlmondSize(),
        getAlmondPounds(),
        getPricePerPound(),
        getStartPaymentDate(),
        getEndPaymentDate(),
        getComment(),
        getManagementType().className(),
        getExpirationTime());

    return newBid;
  }

  public void updateBid(HandlerBid handlerBid) {
    if (!isValid()) {
      throw new RuntimeException("Attempted to create bid from invalid parser.\n");
    }

    handlerBid.setAlmondVariety(getAlmondVariety());
    handlerBid.setAlmondSize(getAlmondSize());
    handlerBid.setAlmondPounds(getAlmondPounds());
    handlerBid.setPricePerPound(getPricePerPound());
    handlerBid.setStartPaymentDate(getStartPaymentDate());
    handlerBid.setEndPaymentDate(getEndPaymentDate());
    handlerBid.setComment(getComment());

  }

  public Handler getHandler() {
    ensureValid();
    return handler;
  }

  public List<Grower> getGrowers() {
    ensureValid();
    return growers;
  }

  public AlmondVariety getAlmondVariety() {
    ensureValid();
    return almondVariety;
  }

  public String getAlmondSize() {
    ensureValid();
    return almondSize;
  }

  public Integer getAlmondPounds() {
    ensureValid();
    return almondPounds;
  }

  public String getPricePerPound() {
    ensureValid();
    return pricePerPound;
  }

  public LocalDate getStartPaymentDate() {
    ensureValid();
    return startPaymentDate;
  }

  public LocalDate getEndPaymentDate() {
    ensureValid();
    return endPaymentDate;
  }

  public ManagementTypeInfo getManagementType() {
    ensureValid();
    return managementType;
  }

  public String getComment() {
    ensureValid();
    return comment;
  }

  public LocalDateTime getExpirationTime() {
    return expirationTime;
  }

  private List<Grower> parseGrowers(JsonNode data) {
    // Check grower ids are present.
    Logger.info("parsing growers...\n\n");
    if (!data.has(HandlerBidJsonConstants.GROWER_IDS)) {
      setInvalid(missingParameterError(HandlerBidJsonConstants.GROWER_IDS));
      return null;

    } 
    
    JsonNode growerIds = data.get(HandlerBidJsonConstants.GROWER_IDS);

    // Grower IDs should be formatted as an array of strings.
    if (!growerIds.isArray()) {
      setInvalid("Grower ID Format Invalid: array of strings expected.");
      return null;
    }

    List<Grower> processedGrowers = new ArrayList<>();

    for (JsonNode node : growerIds) {
      Long growerId = parseLong(node.asText());

      // Ensure grower id is valid integer format.
      if (growerId == null) {
        setInvalid("Grower id value [" + node.asText() + "] is not a valid long integer.\n");
        return null;
      }

      // Check grower exists with given id.
      Grower grower = growerService.getById(growerId);
      if (grower == null) {
        setInvalid("Grower does not exist with grower id [" + growerId + "].\n");
        return null;
      }

      // Ensure given handler owns grower
      if (!handlerService.checkHandlerOwnsGrower(handler, grower)) {
        setInvalid("Handler with id [ " + handler.getId() + " ] does not own grower with id [ "
            + growerId + " ].\n");
        return null;

      }

      processedGrowers.add(grower);
    }

    return processedGrowers;
  }

  private AlmondVariety parseAlmondVariety(JsonNode data) {
    // Check almound variety is preseent.
    Logger.info("parsing almond variety...\n\n");
    if (!data.has(HandlerBidJsonConstants.ALMOND_VARIETY)) {
      setInvalid(missingParameterError(HandlerBidJsonConstants.ALMOND_VARIETY));
      return null;
    } 
    
    AlmondVariety almondVariety =
        Almond.stringToAlmondVariety(data.get(HandlerBidJsonConstants.ALMOND_VARIETY).asText());

    if (almondVariety == null) {
      setInvalid("Almond Variety Format Invalid: string of valid almond variety expected.\n");
      return null;
    }

    return almondVariety;
  }

  private String parseAlmondSize(JsonNode data) {
    // Check almound size is present.
    Logger.info("parsing almond size...\n\n");
    if (!data.has(HandlerBidJsonConstants.ALMOND_SIZE)) {
      setInvalid(missingParameterError(HandlerBidJsonConstants.ALMOND_SIZE));
      return null;
    } 

    // TODO Add checks to verify correctly formatted almond size.
    
    return data.get(HandlerBidJsonConstants.ALMOND_SIZE).asText();
  }

  private Integer parseAlmondPounds(JsonNode data) {
    // Check almound pounds is present.
    Logger.info("parsing almond pounds...\n\n");
    if (!data.has(HandlerBidJsonConstants.ALMOND_POUNDS)) {
      setInvalid(missingParameterError(HandlerBidJsonConstants.ALMOND_POUNDS));
      return null;

    } 
    
    Long almondPounds = parseLong(data.get(HandlerBidJsonConstants.ALMOND_POUNDS).asText());

    if (almondPounds == null) {
      setInvalid("Almond Pound Format Invalid: integer expected.\n");
      return null;
    }

    return almondPounds.intValue();
  }

  private String parsePricePerPound(JsonNode data) {
    // Check price per pound is present.
    Logger.info("parsing price per pound...\n\n");
    if (!data.has(HandlerBidJsonConstants.PRICE_PER_POUND)) {
      setInvalid(missingParameterError(HandlerBidJsonConstants.PRICE_PER_POUND));
      return null;

    } 
    
    // TODO Change To Monetary Value
    String pricePerPound = data.get(HandlerBidJsonConstants.PRICE_PER_POUND).asText();

    if (pricePerPound == null) {
      setInvalid("Almond Price Per Pound Format Invalid: monetary value string expected.\n");
      return null;
    }

    return "$" + pricePerPound;
  }

  private LocalDate parseStartPaymentDate(JsonNode data) {
    // Check payment date is preseent.
    Logger.info("parsing start payment date...\n\n");
    if (!data.has(HandlerBidJsonConstants.START_PAYMENT_DATE)) {
      setInvalid(missingParameterError(HandlerBidJsonConstants.START_PAYMENT_DATE));
      return null;

    } 
    
    String dateString = data.get(HandlerBidJsonConstants.START_PAYMENT_DATE).asText();

    if (!DateService.verifyDateString(dateString)) {
      // TODO: Determine Date Format
      setInvalid("Date String Format Invalid: string with TODO format expected.\n"); 
      return null;
    }

    return DateService.stringToDate(dateString);
  }

  private LocalDate parseEndPaymentDate(JsonNode data) {
    // Check payment date is preseent.
    Logger.info("parsing end payment date...\n\n");
    if (!data.has(HandlerBidJsonConstants.END_PAYMENT_DATE)) {
      setInvalid(missingParameterError(HandlerBidJsonConstants.END_PAYMENT_DATE));
      return null;

    } 
    
    String dateString = data.get(HandlerBidJsonConstants.END_PAYMENT_DATE).asText();

    if (!DateService.verifyDateString(dateString)) {
      // TODO: Determine Date Format
      setInvalid("Date String Format Invalid: string with TODO format expected.\n"); 
      return null;
    }

    return DateService.stringToDate(dateString);
  }

  private ManagementTypeInfo parseManagementType(JsonNode data) {
    Logger.info("parsing management type...\n\n");
    if (!data.has(HandlerBidJsonConstants.MANAGEMENT_TYPE)) {
      setInvalid(missingParameterError(HandlerBidJsonConstants.MANAGEMENT_TYPE));
      return null;
    }

    JsonNode typeMap = data.get(HandlerBidJsonConstants.MANAGEMENT_TYPE);

    int delayInt;
    if(typeMap.has(HandlerBidJsonConstants.DELAY_KEY)) {
      delayInt = typeMap.get(HandlerBidJsonConstants.DELAY_KEY).asInt();
    } else {
      setInvalid(missingParameterError(HandlerBidJsonConstants.DELAY_KEY));
      return null;
    }

    LocalDateTime currentTime = LocalDateTime.now();
    expirationTime = currentTime.plusMinutes(delayInt);
  
    if(typeMap.has(HandlerBidJsonConstants.TYPE_KEY)) {
      String className = typeMap.get(HandlerBidJsonConstants.TYPE_KEY).asText();
      Duration delayTime = Duration.ofMinutes(delayInt);
      switch(className) {
        case HandlerBidJsonConstants.ManagementTypes.WATERFALL:
          return new ManagementTypeInfo(WaterfallService.class, delayTime);
        case HandlerBidJsonConstants.ManagementTypes.FCFS: 
          return new ManagementTypeInfo(FCFSService.class, delayTime);
        default:
          setInvalid("Management Type invalid: specified type " + className +" not found\n");
          return null;          
      }
    } 

    setInvalid(missingParameterError(HandlerBidJsonConstants.TYPE_KEY));
    return null;
  }


  private String parseComment(JsonNode data) {
    Logger.info("parsing comment...\n\n");
    // Check comment is present.
    if (!data.has(HandlerBidJsonConstants.COMMENT)) {
      return "";
    } 
    
    String commentString = data.get(HandlerBidJsonConstants.COMMENT).asText();

    if (commentString == null) {
      setInvalid("Comment Format Invalid: string format expected.\n");
      return null;
    }

    return commentString;
  }

  private static class HandlerBidJsonConstants {
    private static final String GROWER_IDS = "grower_ids";

    private static final String ALMOND_VARIETY = "almond_variety";
    private static final String ALMOND_SIZE = "almond_size";
    private static final String ALMOND_POUNDS = "almond_pounds";

    private static final String PRICE_PER_POUND = "price_per_pound";

    private static final String START_PAYMENT_DATE = "start_payment_date";

    private static final String END_PAYMENT_DATE = "end_payment_date";

    private static final String MANAGEMENT_TYPE = "management_type";

    private static final String COMMENT = "comment";

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

      ManagementTypeInfo(Class<? extends BidManagementService> c, Duration d) {
        this.typeClass = c;
        this.delay = d;
      }

      public Class<? extends BidManagementService> getClassType() { return typeClass; }
      public String className() { return typeClass.getName(); }
      public Duration getDelay() { return delay; }

  }
}
