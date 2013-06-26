package org.jappler.laundrycheck;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.apache.ApacheHttpTransport;

public class LaundryService {
	
	public enum ResultType {
		TOO_SOON,
		UNAVAILABLE,
		OFFLINE,
		AVAILABLE;
	}
	
	public static class Result {
		
		private String availableUrl;
		private ResultType type;
		
		Result(ResultType type, String availableUrl) {
			this.type = type;
			this.availableUrl = availableUrl;
		}
		
		public ResultType getType() {
			return this.type;
		}
		
		public String getAvailableUrl() {
			return this.availableUrl;
		}
	}
	
	private static final String OPENTABLE_CHECK_URL = 
			"http://www.opentable.com/opentables.aspx";
	
	private static final DateFormat OPENTABLE_DATE_FORMAT = 
			new SimpleDateFormat("M/d/yyyy H:mm:ss a");
	
	private static final Pattern AVAILABLE_TIME_PATTERN = 
			Pattern.compile("<span class=\\\"t\\\">([0-9]{1,2}:[0-9]{1,2})</span>");
	
	public static Result checkReservationsForTwoMonthsFromToday() {
		Calendar date = Calendar.getInstance();
		
		date.add(Calendar.MONTH, 2);
		date.set(Calendar.HOUR, 7);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.AM_PM, Calendar.PM);
		
		Result result = null;
		for (int i = -4; i < 3; i++) {
			Calendar current = Calendar.getInstance();
			current.setTime(date.getTime());
			
			current.add(Calendar.DAY_OF_YEAR, i);
			
			result = checkReservationsForDay(current.getTime());
			
			System.out.println("Checking for day " + current.getTime().toString() + ": " + result.getType());

			if (result.getType() != ResultType.UNAVAILABLE)
				return result;
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {}
			

		}
		
		return result;
	}
	
	public static Result checkReservationsForDay(Date date) {		
		GenericUrl url = getUrl(date);

		String output = loadPage(url);
		
		if (output.trim().isEmpty()) {
			return new Result(ResultType.OFFLINE, url.build());
		} else if (output.contains("Your requested date exceeds")) {
			return new Result(ResultType.TOO_SOON, url.build());
		} else if (output.contains("There are no reservations currently available") || output.contains("No tables are available within")) {
			return new Result(ResultType.UNAVAILABLE, url.build());
		} else if (output.contains("Currently offline")) {
		   return new Result(ResultType.OFFLINE, url.build());
		}
		
		Matcher timeMatcher = AVAILABLE_TIME_PATTERN.matcher(output);
		
		String times = " ";
		while (timeMatcher.find()) {
			times += timeMatcher.group(1) + " ";
		}
		
		return new Result(ResultType.AVAILABLE, times + url.build());
	}
	
	private static GenericUrl getUrl(Date date) {
		GenericUrl url = new GenericUrl(OPENTABLE_CHECK_URL);
		
		url.put("d", OPENTABLE_DATE_FORMAT.format(date).toString());
		
		url.put("m", "4");
		url.put("p", "2");
		url.put("r", "1180");
		url.put("t", "rest");
		
		return url;
	}

	private static String loadPage(GenericUrl url) {
		try {
			ApacheHttpTransport transport = new ApacheHttpTransport(ApacheHttpTransport.newDefaultHttpClient());
			HttpRequestFactory factory = transport.createRequestFactory();

			HttpRequest request = factory.buildGetRequest(url);

			HttpResponse response = request.execute();
			
			return response.parseAsString();
		} catch (IOException e) {
			System.err.println("Error loading the page");
			e.printStackTrace();
		}
		
		return "";
	}
}
