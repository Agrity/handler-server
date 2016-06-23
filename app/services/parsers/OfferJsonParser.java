package services.parsers;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import models.Almond;
import models.Almond.AlmondVariety;
import models.Grower;
import models.Handler;
import models.Offer;

import services.DateService;
import services.GrowerService;
import services.impl.EbeanGrowerService;

import java.lang.reflect.Constructor;

/**
 * Class to parse json data to create new Offer.
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
public class OfferJsonParser extends JsonParser {

  private Handler handler;
  private List<Grower> growers;
  private AlmondVariety almondVariety;
  private Integer almondPounds;
  private String pricePerPound;
  private LocalDate paymentDate;
  private ManagementTypeInfo managementType;
  private String comment;

  private final GrowerService growerService;

  public OfferJsonParser(JsonNode data) {
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

    paymentDate = parsePaymentDate(data);
    if (paymentDate == null) {
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

  public Offer formOffer() {
    if (!isValid()) {
      throw new RuntimeException("Attempted to create Offer from invalid parser.\n");
    }

    Offer newOffer = new Offer(
        getHandler(),
        getGrowers(),
        getAlmondVariety(),
        getAlmondPounds(),
        getPricePerPound(),
        getPaymentDate(),
        getComment());

    return newOffer;
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

  public Integer getAlmondPounds() {
    ensureValid();
    return almondPounds;
  }

  public String getPricePerPound() {
    ensureValid();
    return pricePerPound;
  }

  public LocalDate getPaymentDate() {
    ensureValid();
    return paymentDate;
  }

  public ManagementTypeInfo getManagementType() {
    ensureValid();
    return managementType;
  }

  public String getComment() {
    ensureValid();
    return comment;
  }

  private List<Grower> parseGrowers(JsonNode data) {
    // Check grower ids are present.
    if (!data.has(OfferJsonConstants.GROWER_IDS)) {
      setInvalid(missingParameterError(OfferJsonConstants.GROWER_IDS));
      return null;

    } 
    
    JsonNode growerIds = data.get(OfferJsonConstants.GROWER_IDS);

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
    if (!data.has(OfferJsonConstants.ALMOND_VARIETY)) {
      setInvalid(missingParameterError(OfferJsonConstants.ALMOND_VARIETY));
      return null;
    } 
    
    AlmondVariety almondVariety =
        Almond.stringToAlmondVariety(data.get(OfferJsonConstants.ALMOND_VARIETY).asText());

    if (almondVariety == null) {
      setInvalid("Almond Variety Format Invalid: string of valid almond variety expected.\n");
      return null;
    }

    return almondVariety;
  }

  private Integer parseAlmondPounds(JsonNode data) {
    // Check almound pounds is present.
    if (!data.has(OfferJsonConstants.ALMOND_POUNDS)) {
      setInvalid(missingParameterError(OfferJsonConstants.ALMOND_POUNDS));
      return null;

    } 
    
    Long almondPounds = parseLong(data.get(OfferJsonConstants.ALMOND_POUNDS).asText());

    if (almondPounds == null) {
      setInvalid("Almond Pound Format Invalid: integer expected.\n");
      return null;
    }

    return almondPounds.intValue();
  }

  private String parsePricePerPound(JsonNode data) {
    // Check price per pound is present.
    if (!data.has(OfferJsonConstants.PRICE_PER_POUND)) {
      setInvalid(missingParameterError(OfferJsonConstants.PRICE_PER_POUND));
      return null;

    } 
    
    // TODO Change To Monetary Value
    String pricePerPound = data.get(OfferJsonConstants.PRICE_PER_POUND).asText();

    if (pricePerPound == null) {
      setInvalid("Almond Price Per Pound Format Invalid: monetary value string expected.\n");
      return null;
    }

    return "$" + pricePerPound;
  }

  private LocalDate parsePaymentDate(JsonNode data) {
    // Check payment date is preseent.
    if (!data.has(OfferJsonConstants.PAYMENT_DATE)) {
      setInvalid(missingParameterError(OfferJsonConstants.PAYMENT_DATE));
      return null;

    } 
    
    String dateString = data.get(OfferJsonConstants.PAYMENT_DATE).asText();

    if (!DateService.verifyDateString(dateString)) {
      // TODO: Determine Date Format
      setInvalid("Date String Format Invalid: string with TODO format expected.\n"); 
      return null;
    }

    return DateService.stringToDate(dateString);
  }

  private ManagementTypeInfo parseManagementType(JsonNode data) {
    if (!data.has(OfferJsonConstants.MANAGEMENT_TYPE)) {
      setInvalid(missingParameterError(OfferJsonConstants.MANAGEMENT_TYPE));
      return null;
    }

    JsonNode typeMap = data.get(OfferJsonConstants.MANAGEMENT_TYPE);

    int delayInt;
    if(typeMap.has(OfferJsonConstants.DELAY_KEY)) {
      delayInt = typeMap.get(OfferJsonConstants.DELAY_KEY).asInt();
    } else {
      setInvalid("Delay field not found.\n");
      return null;
    }
  
    if(typeMap.has(OfferJsonConstants.TYPE_KEY)) {
      String className = typeMap.get(OfferJsonConstants.TYPE_KEY).asText();

      try {
        Class<?> mgmtClass = Class.forName("services.offer_management." + className);
    
        return new ManagementTypeInfo(mgmtClass, Duration.ofMinutes(delayInt));

      } catch (ClassNotFoundException ce) {
        setInvalid("Management Type invalid: specified type " + className +" not found\n");
        return null;
      }
    } 

    setInvalid("Type field not found.\n");
    return null;
  }


  private String parseComment(JsonNode data) {
    // Check comment is present.
    if (!data.has(OfferJsonConstants.COMMENT)) {
      return "";
    } 
    
    String commentString = data.get(OfferJsonConstants.COMMENT).asText();

    if (commentString == null) {
      setInvalid("Comment Format Invalid: string format expected.\n");
      return null;
    }

    return commentString;
  }

  private static class OfferJsonConstants {
    private static final String GROWER_IDS = "grower_ids";

    private static final String ALMOND_VARIETY = "almond_variety";
    private static final String ALMOND_POUNDS = "almond_pounds";

    private static final String PRICE_PER_POUND = "price_per_pound";

    private static final String PAYMENT_DATE = "payment_date";

    private static final String MANAGEMENT_TYPE = "management_type";

    private static final String COMMENT = "comment";

    private static final String TYPE_KEY = "type";
    private static final String DELAY_KEY = "delay";

  }

  public static class ManagementTypeInfo {
    private Class typeClass;
    private Duration delay;

      ManagementTypeInfo(Class c, Duration d) {
        this.typeClass = c;
        this.delay = d;
      }

      public Class getClassType() { return typeClass; }
      public Duration getDelay() { return delay; }

  }
}
