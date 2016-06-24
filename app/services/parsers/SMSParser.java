package services.parsers;

public class SMSParser extends BaseParser {

  private Long offerID;
  private Integer almoundPounds;
  private boolean validity;
  private String errorMessage;

  public SMSParser(String smsMessage) {
    super();

    offerID = parseID(smsMessage);
    if (offerID == -1L) {
    	return;
    }
    almoundPounds = parsePounds(smsMessage);
    if (almoundPounds == -1) {
    	return;
    }
  }

  public Long getOfferID() {
  	return offerID;
  }

  public Integer getAlmoundPounds() {
  	return almoundPounds;
  }

  private Long parseID(String smsMessage) {
  	setInvalid("Offer ID is not formatted correctly. Could not process bid response.");
  	return -1L;
  }

  private Integer parsePounds(String smsMessage) {
  	setInvalid("Almound Pounds amount is not formatted correctly. Could not process bid response.");
  	return -1;
  }
}