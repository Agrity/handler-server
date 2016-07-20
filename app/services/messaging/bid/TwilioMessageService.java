package services.messaging.bid;

import java.util.*;
import com.twilio.sdk.*;
import com.twilio.sdk.resource.factory.*;
import com.twilio.sdk.resource.instance.*;
import com.twilio.sdk.resource.list.*;
import com.twilio.sdk.TwilioRestResponse;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import services.messaging.MessageServiceConstants.TwilioFields;
import play.Logger;

public class TwilioMessageService {

  public boolean sendMessage(String to, String msg) {
  	boolean success = true;

  	List<NameValuePair> params = new ArrayList<NameValuePair>(); 
    params.add(new BasicNameValuePair("To", to));    
    params.add(new BasicNameValuePair("From", TwilioFields.getTwilioNumber())); 
    params.add(new BasicNameValuePair("Body", msg));

    try {
      Message message = TwilioFields.getMessageFactory().create(params);
    } catch (TwilioRestException e) {
      success = false;
      Logger.error("=== Error Sending SMS Message === to " + to
                 + " " + e.getErrorMessage() + "\n\n");
    }

  	return success;
  }
	
}