package services.parsers;

/**
 * Class to do basic parsing SMS Message from a String to a Long and Integer.
 * Expected String format:
 * 123 456789
 * Offer ID Number followed by whitespace followed by Number pounds accepted.
 * NOTE: Does NOT accpet anything outside of this format (e.g. comma in pounds number)
 */

public class SMSParser extends BaseParser {

  private Long offerID;
  private Integer almoundPounds;

  public SMSParser(String smsMessage) {
    super();

    String[] splited = smsMessage.split("\\s+");

    if (splited.length != 2) {
    	setInvalid("Please format message as \"[ID#] [# pounds Accepted]\". Could not process bid response.");
    	return;
    }

    offerID = parseID(splited[0]);
    if (offerID == null) {
    	return;
    }

    almoundPounds = parsePounds(splited[1]);
    if (almoundPounds == null) {
    	return;
    }
    setValid();
  }

  public Long getOfferID() {
  	return offerID;
  }

  public Integer getAlmoundPounds() {
  	return almoundPounds;
  }

  private Long parseID(String firstHalf) {
  	Long result = parseLong(firstHalf);
  	if (result == null) {
  	  setInvalid("Offer ID is not formatted correctly. Please format message as \"[ID#] [# pounds Accepted]\". Could not process bid response.");
  	}
  	return result;
  }

  private Integer parsePounds(String secondHalf) {
  	Integer result = parseInteger(secondHalf);
  	if (result == null) {
  	  setInvalid("Number of pounds accepted is not formatted correctly. Please format message as \"[ID#] [# pounds Accepted]\". Could not process bid response.");
  	}
  	return result;
  }
}