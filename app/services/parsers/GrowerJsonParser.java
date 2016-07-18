package services.parsers;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

import models.EmailAddress;
import models.Grower;
import models.Handler;
import models.PhoneNumber;

import services.EmailService;
import services.PhoneMessageService;
import play.Logger;

/**
 * Class to parse json data to create new Grower.
 *
 * Expected Json Structure:
 *  {
 *    HANDLER_ID: ... (Constant in JsonParser super class)
 *
 *    FIRST_NAME: ...
 *    LAST_NAME: ...
 *
 *
 *
 *    EMAIL_ADDRESS: ...
 *
 *    PHONE_NUMBERS: ...
 *  }
 */
public class GrowerJsonParser extends BaseSellerJsonParser {

  private Handler handler;

  public GrowerJsonParser(JsonNode data) {
    super();

    handler = parseHandler(data);
    if (handler == null) {
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

  public Grower formGrower() {
    if (!isValid()) {
      throw new RuntimeException("Attempted to create Grower from invalid parser.\n");
    }

    Grower newGrower = new Grower(
        getHandler(),
        getFirstName(),
        getLastName(),
        getEmailAddress(),
        getPhoneNumber());

    return newGrower;
  }

  public void updateGrower(Grower grower) {
    if (!isValid()) {
      throw new RuntimeException("Attempted to update Grower from invalid parser.\n");
    }

    grower.setFirstName(getFirstName());
    grower.setLastName(getLastName());
    grower.setPhoneNumber(getPhoneNumber());
    grower.setEmailAddress(getEmailAddress()); 

  }

  public Handler getHandler() {
    ensureValid();
    return handler;
  }
}
