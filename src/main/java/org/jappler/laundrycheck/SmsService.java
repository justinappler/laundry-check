package org.jappler.laundrycheck;

import java.util.HashMap;
import java.util.Map;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Sms;

/**
 * A simple class for sending text messages using the <em>awesome</em> 
 * Twilio API.  Be sure to set the <code>TWILIO_SID</code>, <code>TWILIO_TOKEN</code>,
 * and <code>TWILIO_PHONE_NUMBER</code> environment variables prior to 
 * using this class.
 */
public class SmsService {

    private static final String ACCOUNT_SID = System.getenv("TWILIO_SID");
    private static final String AUTH_TOKEN = System.getenv("TWILIO_TOKEN");
    private static final String TWILIO_PHONE_NUMBER = System.getenv("TWILIO_PHONE_NUMBER");

    private TwilioRestClient restClient;
    private static SmsService serviceInstance;

    private SmsService() {
        restClient = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
    }

    public static SmsService getInstance() {
        if (serviceInstance == null) {
            serviceInstance = new SmsService();
        }

        return serviceInstance;
    }

    /**
     * Sends an SMS message to the specified 'to' number.
     * 
     * @param to
     *            The cell number to send the message to
     * @param text
     *            The text of the message
     * @return
     */
    public String sendSMS(String to, String text) throws TwilioRestException {
        Sms message;

        Map<String, String> params = new HashMap<String, String>();
        params.put("Body", text);
        params.put("To", to);
        params.put("From", TWILIO_PHONE_NUMBER);

        SmsFactory messageFactory = restClient.getAccount().getSmsFactory();
        message = messageFactory.create(params);

        return message.getSid();
    }
}
