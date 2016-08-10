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
import services.HandlerSellerService;
import services.impl.EbeanHandlerSellerService;
import services.bid_management.TraderBidManagementService;

import services.bid_management.WaterfallService;
import services.bid_management.TraderFCFSService;
import services.bid_management.TraderSTFCService;

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
 *    ALMOND_SIZE: ... ,
 *
 *    GRADE: ... ,
 *
 *    ALMOND_POUNDS: ... ,
 *
 *    ALMOND_PRICE_PER_POUND: ... ,
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
public class TraderBidJsonParser extends BidJsonParser {

  private Trader trader;
  private List<HandlerSeller> handlerSellers;
  private String almondVariety;
  private String almondSize;
  private String grade;
  private Integer almondPounds;
  private String pricePerPound;
  private TraderManagementTypeInfo managementType;
  private LocalDateTime expirationTime;
  private String comment;

  private final HandlerSellerService handlerSellerService;

  public TraderBidJsonParser(JsonNode data) {
    super();

    // TODO -- Extremely Hacky -- Change to Dependency Injection.
    //      See Guice AssistedInjection
    handlerSellerService = new EbeanHandlerSellerService();

    trader = parseTrader(data);
    if (trader == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    handlerSellers = parseHandlerSellers(data);
    if (handlerSellers == null) {
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

    grade = parseGrade(data);
    if (grade == null) {
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

    expirationTime = parseExpirationTime(data);
    if(expirationTime == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    setValid();
  }

  public TraderBid formBid() {
    if (!isValid()) {
      throw new RuntimeException("Attempted to create bid from invalid parser.\n");
    }

    TraderBid newBid = new TraderBid(
        getTrader(),
        getHandlerSellers(),
        getAlmondVariety(),
        getAlmondSize(),
        getGrade(),
        getAlmondPounds(),
        getPricePerPound(),
        getComment(),
        getManagementType().className(),
        getExpirationTime());

    return newBid;
  }

  public void updateBid(TraderBid traderBid) {
    if (!isValid()) {
      throw new RuntimeException("Attempted to create bid from invalid parser.\n");
    }

    traderBid.setAlmondVariety(getAlmondVariety());
    traderBid.setGrade(getGrade());
    traderBid.setAlmondPounds(getAlmondPounds());
    traderBid.setPricePerPound(getPricePerPound());
    traderBid.setComment(getComment());
  }

  public Trader getTrader() {
    return trader;
  }

  public List<HandlerSeller> getHandlerSellers() {
    return handlerSellers;
  }

  public String getAlmondVariety() {
    ensureValid();
    return almondVariety;
  }

  public String getAlmondSize() {
    ensureValid();
    return almondSize;
  }

  public String getGrade() {
    ensureValid();
    return grade;
  }

  public Integer getAlmondPounds() {
    ensureValid();
    return almondPounds;
  }

  public String getPricePerPound() {
    ensureValid();
    return pricePerPound;
  }

  public TraderManagementTypeInfo getManagementType() {
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

  private List<HandlerSeller> parseHandlerSellers(JsonNode data) {
    // Check handlerSeller ids are present.
    Logger.info("parsing handler sellers...\n\n");
    if (!data.has(TraderBidJsonConstants.HANDLERSELLER_IDS)) {
      setInvalid(missingParameterError(TraderBidJsonConstants.HANDLERSELLER_IDS));
      return null;
    } 
    
    JsonNode handlerSellerIds = data.get(TraderBidJsonConstants.HANDLERSELLER_IDS);

    // HandlerSeller IDs should be formatted as an array of strings.
    if (!handlerSellerIds.isArray()) {
      setInvalid("HandlerSeller ID Format Invalid: array of strings expected.");
      return null;
    }

    List<HandlerSeller> processedHandlerSellers = new ArrayList<>();

    for (JsonNode node : handlerSellerIds) {
      Long handlerSellerId = parseLong(node.asText());

      // Ensure handlerSeller id is valid integer format.
      if (handlerSellerId == null) {
        setInvalid("HandlerSeller id value [" + node.asText() + "] is not a valid long integer.\n");
        return null;
      }

      // Check handlerSeller exists with given id.
      HandlerSeller handlerSeller = handlerSellerService.getById(handlerSellerId);
      if (handlerSeller == null) {
        setInvalid("HandlerSeller does not exist with handlerSeller id [" + handlerSellerId + "].\n");
        return null;
      }

      // Ensure given trader owns handlerSeller
      if (!traderService.checkTraderOwnsHandlerSeller(trader, handlerSeller)) {
        setInvalid("Trader with id [ " + trader.getId() + " ] does not own handlerSeller with id [ "
            + handlerSellerId + " ].\n");
        return null;

      }
      processedHandlerSellers.add(handlerSeller);
    }

    return processedHandlerSellers;
  }


  protected String parseGrade(JsonNode data) {
    // Check if grade is present.
    if (!data.has(TraderBidJsonConstants.GRADE)) {
      return "";
    } 

    return data.get(TraderBidJsonConstants.GRADE).asText();
  }

  private TraderManagementTypeInfo parseManagementType(JsonNode data) {
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
      Duration delayTime = Duration.ofHours(delayInt);
      switch(className) {
        case BidJsonConstants.ManagementTypes.STFC:
           return new TraderManagementTypeInfo(TraderSTFCService.class, delayTime);
        case BidJsonConstants.ManagementTypes.FCFS: 
          return new TraderManagementTypeInfo(TraderFCFSService.class, delayTime);
        default:
          setInvalid("Management Type invalid: specified type " + className +" not found\n");
          return null;          
      }
    }
    setInvalid(missingParameterError(BidJsonConstants.TYPE_KEY));
    return null;
  } 

  public static class TraderManagementTypeInfo {
    private Class<? extends TraderBidManagementService> typeClass;
    private Duration delay;

    TraderManagementTypeInfo(Class<? extends TraderBidManagementService> typeClass, Duration delay) {
      this.typeClass = typeClass;
      this.delay = delay;
    }

    public Class<? extends TraderBidManagementService> getClassType() { return typeClass; }
    public String className() { return typeClass.getName(); }
    public Duration getDelay() { return delay; }
  }

  private static class TraderBidJsonConstants {
    private static final String HANDLERSELLER_IDS = "handlerSeller_ids";
    private static final String GRADE = "grade";
  }

}
