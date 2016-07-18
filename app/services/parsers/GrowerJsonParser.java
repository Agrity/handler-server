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
public class GrowerJsonParser extends BaseParser {

  private Handler handler;
  private String firstName;
  private String lastName;
  private EmailAddress emailAddress;
  private PhoneNumber phoneNumber;

  public GrowerJsonParser(JsonNode data) {
    super();

    handler = parseHandler(data);
    if (handler == null) {
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

  public String getFirstName() {
    ensureValid();
    return firstName;
  }

  public String getLastName() {
    ensureValid();
    return lastName;
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
