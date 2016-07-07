package services.parsers;

/**
 * Class to do basic parsing SMS Message from a String to a Long and Integer.
 * Expected String format:
 * "123 456789"
 *
 * Offer ID Number followed by whitespace followed by Number pounds accepted.
 * NOTE: Does NOT accpet anything outside of this format (e.g. comma in pounds number)
 */
import play.Logger;

public class SMSParser extends BaseParser {

  private Long offerID;
  private Integer pounds;
  private boolean accepted;
  private static final String errorResponse = 
                    "Please format message as \"[ID#] [#lbs Accepting]\". Could not process bid response.";

  public SMSParser(String smsMessage) {
    super();

    String[] splited = smsMessage.split("\\s+");

    if (splited.length != 2) {
      Logger.error("invalid length\n\n");
    	setInvalid(errorResponse);
    	return;
    }

    offerID = parseID(splited[0]);
    if (offerID == null) {
      Logger.error("invalid ID\n\n");
    	return;
    }

    pounds = parsePounds(splited[1]);
    if (pounds == null) {
      Logger.error("invalid command \n\n");
      return;
    }

    setValid();
  }

  public Long getID() {
  	return offerID;
  }

  public boolean getAccepted() {
    return accepted;
  }

  public Integer getPounds() {
  	return pounds;
  }

  private Long parseID(String firstHalf) {
  	Long result = parseLong(firstHalf);

  	if (result == null) {
  	  setInvalid("Offer ID is not formatted correctly. " + errorResponse);
  	}

  	return result;
  }

  private Integer parsePounds(String secondHalf) {
    secondHalf = secondHalf.replace(",", "");
    Integer result = parseInteger(secondHalf);

  	if (result == null) {
  	  setInvalid("Number of pounds accepted is not formatted correctly. " + errorResponse);
  	}

  	return result;
  }
}