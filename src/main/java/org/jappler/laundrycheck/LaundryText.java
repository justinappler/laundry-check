package org.jappler.laundrycheck;
import java.util.HashMap;
import java.util.Map;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Sms;

public class LaundryText {

	public static final String ACCOUNT_SID = System.getenv("TWILIO_SID");
	public static final String AUTH_TOKEN = System.getenv("TWILIO_TOKEN");
	
	public static final String TWILIO_PHONE_NUMBER = "+16507794377";

	public static void sendSMS(String to, String text) {
		Sms message;
		
		try {
			TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);

			// Build a filter for the SmsList
			Map<String, String> params = new HashMap<String, String>();
			params.put("Body", text);
			params.put("To", to);
			params.put("From", TWILIO_PHONE_NUMBER);

			SmsFactory messageFactory = client.getAccount().getSmsFactory();
			message = messageFactory.create(params);
		} catch (TwilioRestException tre) {
			System.err.println("Text Message Failure!");
			tre.printStackTrace();
			return;
		}
		
		if (message != null)
			System.out.println("Text Message Sent! Id: " + message.getSid());
	}
}
