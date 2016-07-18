package services.parsers;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

import models.EmailAddress;
import models.HandlerSeller;
import models.Trader;
import models.PhoneNumber;

import services.EmailService;
import services.PhoneMessageService;
import play.Logger;

/**
 * Class to parse json data to create new Grower.
 *
 * Expected Json Structure:
 *  {
 *    TRADER_ID: ... (Constant in JsonParser super class)
 *
 *    FIRST_NAME: ...
 *    LAST_NAME: ...
 *
 *
 *
 *    EMAIL_ADDRESSES: ...
 *
 *    PHONE_NUMBERS: ...
 *  }
 */
public class HandlerSellerJsonParser extends BaseSellerJsonParser {

  private Trader trader;

  public HandlerSellerJsonParser(JsonNode data) {
    super();

    trader = parseTrader(data);
    if (trader == null) {
      // Parser set to invalid with proper error message.
      return;
    }
    
    setFirstName(parseName(data, SellerJsonConstants.FIRST_NAME));
    if (getFirstName() == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    setLastName(parseName(data, SellerJsonConstants.LAST_NAME));
    if (getLastName() == null) {
      // Parser set to invalid with proper error message.
      return;
    }
    
    setEmailAddress(parserSellerEmailAddress(data));
    if (getEmailAddress() == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    setPhoneNumber(parsePhoneNumber(data));
    if (getPhoneNumber() == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    // Valid json data recieved
    setValid();
  }

  public HandlerSeller formHandlerSeller() {
    if (!isValid()) {
      throw new RuntimeException("Attempted to create HandlerSeller from invalid parser.\n");
    }

    HandlerSeller newHandlerSeller = new HandlerSeller(
        getTrader(),
        getFirstName(),
        getLastName(),
        getEmailAddress(),
        getPhoneNumber());

    return newHandlerSeller;
  }

  public void updateHandlerSeller(HandlerSeller handlerSeller) {
    if (!isValid()) {
      throw new RuntimeException("Attempted to update handlerSeller from invalid parser.\n");
    }

    handlerSeller.setFirstName(getFirstName());
    handlerSeller.setLastName(getLastName());
    handlerSeller.setPhoneNumber(getPhoneNumber());
    handlerSeller.setEmailAddress(getEmailAddress()); 

  }

  public Trader getTrader() {
    ensureValid();
    return trader;
  }
}
