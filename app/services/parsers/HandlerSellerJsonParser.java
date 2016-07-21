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
public class HandlerSellerJsonParser extends BaseParser {

  private Trader trader;
  private String firstName;
  private String lastName;
  private String companyName;
  private EmailAddress emailAddress;
  private PhoneNumber phoneNumber;

  public HandlerSellerJsonParser(JsonNode data) {
    super();

    trader = parseTrader(data);
    if (trader == null) {
      // Parser set to invalid with proper error message.
      return;
    }
    
    firstName = parseName(data, JsonConstants.FIRST_NAME);
    if (firstName == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    lastName = parseName(data, JsonConstants.LAST_NAME);
    if (lastName == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    companyName = parseCompanyName(data);
    if (companyName == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    
    emailAddress = parserSellerEmailAddress(data);
    if (emailAddress == null) {
      // Parser set to invalid with proper error message.
      return;
    }

    phoneNumber = parsePhoneNumber(data);
    if (phoneNumber == null) {
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
        getCompanyName(),
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
    handlerSeller.setCompanyName(getCompanyName());
    handlerSeller.setPhoneNumber(getPhoneNumber());
    handlerSeller.setEmailAddress(getEmailAddress()); 

  }

  public Trader getTrader() {
    ensureValid();
    return trader;
  }

  public String getFirstName() {
    ensureValid();
    return firstName;
  }

  public String getLastName() {
    ensureValid();
    return lastName;
  }

  public String getCompanyName() {
    ensureValid();
    return companyName;
  }

  public EmailAddress getEmailAddress() {
    ensureValid();
    return emailAddress;
  }

  public PhoneNumber getPhoneNumber() {
    ensureValid();
    return phoneNumber;
  }
}
