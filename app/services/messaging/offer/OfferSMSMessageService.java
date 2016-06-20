/* === TODO: Finish imports to sync up with rest of back-end === */
/* === TODO: "Download Twilio" libraries === */
/* === TODO: Buy Twilio Account and hard-code account info === */
/* === TODO: Compile and test === */

package services.messaging.offer;

import java.util.*;
import com.twilio.sdk.*;
import com.twilio.sdk.resource.factory.*;
import com.twilio.sdk.resource.instance.*;
import com.twilio.sdk.resource.list.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import models.Grower;
import models.Offer;

import play.Logger;


public class OfferSMSMessageService implements OfferMessageService {

    private static final String ACCOUNT_SID = ""; /* FIXME: Add ID */
    private static final String AUTH_TOKEN = ""; /* FIXME: Add Auth Token */
    private static final String TWILIO_NUMBER = ""; /* FIXME: Add out Twilio number in format "+16501231234" as a string*/
    
    private static final TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
    private static final Account account = client.getAccount();
    private static final MessageFactory messageFactory = account.getMessageFactory();
    
    /* MessageFactory object is used to send messages from Twilio account */
    public MessageFactory getMessageFactory() {
        return messageFactory; 
    }
    
    /* Takes an offer object and sends out SMS message containing bid to all growers using Twilio account */
    public boolean send(Offer offer) {
        boolean success = true;
        for (Grower curGrower: offer.getAllGrowers()) { 
            for (String phoneNumber: curGrower.getPhoneNumbers()) {
                /* number needs to be in format "+18155926350" as a string */
                List<NameValuePair> params = new ArrayList<NameValuePair>(); 
                params.add(new BasicNameValuePair("To", phoneNumber));      
                params.add(new BasicNameValuePair("From", TWILIO_NUMBER)); 
                String body = createBodyText(curGrower, offer);
                params.add(new BasicNameValuePair("Body", body)); 

                try {
                    Message message = messageFactory.create(params);
                 } catch (Exception e) {
                    success = false;
                    Logger.error("=== Error Sending SMS Message ===\n" + e.getCause() + "\n\n");
                }
            }
        }
        return success;
    }
    
    private String createBodyText(Grower curGrower, Offer offer) {
        String body = "Hi " + curGrower.getFullName() + ",\n"
                    + "Here are the specs for a new bid: \n"
                    + offer.getAlmondVariety() + "\n"
                    /* === TODO: Almond Size === */
                    + offer.getAlmondPoundsString() + "lbs\n"
                    + "$" + offer.getPricePerPound() + "/lb\n" 
                    + offer.getComment() + "\n"
                    + "Reply 0 to decline or number of pounds that you would like to accept" /* instructions on how to respond */
                    + "-" + /* add handler's contact's name in addition? */ offer.getHandler().getCompanyName(); 
        return body;
    }  
}
