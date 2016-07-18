package services.parsers;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

import models.EmailAddress;
import models.PhoneNumber;

import services.EmailService;
import services.PhoneMessageService;
import play.Logger;

/**
 * Class to parse json data to create new Seller.
 *
 * Expected Json Structure:
 *  {
 *    BUYER_ID: ... (Constant in JsonParser super class)
 *
 *    FIRST_NAME: ...
 *    LAST_NAME: ...
 *
 *
 *
 *    EMAIL_ADDRESSES: [
 *      ... ,
 *      ... ,
 *      ...
 *    ]
 *
 *    PHONE_NUMBERS: [
 *      ... ,
 *      ... ,
 *      ...
 *    ]
 *  }
 */
public abstract class BaseSellerJsonParser extends BaseParser {

  private String firstName;
  private String lastName;
  private EmailAddress emailAddress;
  private PhoneNumber phoneNumber;


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

  protected void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  protected void setLastName(String lastName) {
    this.lastName = lastName;
  }

  protected void setEmailAddress(EmailAddress emailAddress) {
    this.emailAddress = emailAddress;
  }

  protected void setPhoneNumber(PhoneNumber phoneNumber) {
    this.phoneNumber = phoneNumber;
  }


  protected static class SellerJsonConstants {
    protected static final String FIRST_NAME = "first_name";
    protected static final String LAST_NAME = "last_name";
  }
}
