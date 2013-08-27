package org.jappler.laundrycheck;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jappler.laundrycheck.OpenTableService.ReservationCheckResult.ReservationCheckResultType;

import com.google.api.client.http.GenericUrl;

/**
 * A class for checking reservation availability at
 * OpenTable restaurants by checking the public 'opentable'
 * page. Currently only supports the French Laundry (add more
 * if you so desire).
 */
public class OpenTableService {

    /**
     * A mapping of Restaurants and their known restaurant ID
     * 
     * TODO: Add like, thousands more
     * TODO: Ignore above todo, that's silly
     */
    public enum Restaurant {
        FRENCH_LAUNDRY(1180);

        private int restuarantId;

        Restaurant(int restaurantId) {
            this.restuarantId = restaurantId;
        }

        int getRestaurantId() {
            return this.restuarantId;
        }
    }

    public static class ReservationCheckResult {
        public enum ReservationCheckResultType {
            TOO_SOON, UNAVAILABLE, OFFLINE, AVAILABLE;
        }

        private String availableUrl;
        private ReservationCheckResultType type;

        ReservationCheckResult(ReservationCheckResultType type,
                String availableUrl) {
            this.type = type;
            this.availableUrl = availableUrl;
        }

        public ReservationCheckResultType getType() {
            return this.type;
        }

        public String getAvailableUrl() {
            return this.availableUrl;
        }
    }

    private static final String OPENTABLE_OPENTABLES_URL = "http://www.opentable.com/opentables.aspx";
    private static final String DATE_EXCEEDED = "Your requested date exceeds";
    private static final String NONE_AVAILABLE = "There are no reservations currently available";
    private static final String NO_TABLES_WITHIN = "No tables are available within";
    private static final String OFFLINE = "Currently offline";

    private static final DateFormat OPENTABLE_DATE_FORMAT = new SimpleDateFormat("M/d/yyyy H:mm:ss a");

    private static final Pattern AVAILABLE_TIME_PATTERN = Pattern
            .compile("<span class=\\\"t\\\">([0-9]{1,2}:[0-9]{1,2})</span>");

    public static ReservationCheckResult checkReservationsForDay(Restaurant restaurant, Date date, int partySize) {
        GenericUrl url = getOpenTableReservationPageUrl(restaurant, date, partySize);
        String resPageOutput = HttpUtil.getHTMLForURL(url);

        ReservationCheckResultType result = ReservationCheckResultType.AVAILABLE;
        
        if (resPageOutput.trim().isEmpty()) {
            result = ReservationCheckResultType.OFFLINE;
        } else if (resPageOutput.contains(DATE_EXCEEDED)) {
            result = ReservationCheckResultType.TOO_SOON;
        } else if (resPageOutput.contains(NO_TABLES_WITHIN) || resPageOutput.contains(NONE_AVAILABLE)) {
            result = ReservationCheckResultType.UNAVAILABLE;
        } else if (resPageOutput.contains(OFFLINE)) {
            result = ReservationCheckResultType.OFFLINE;
        }
        
        String times = "";
        if (result == ReservationCheckResultType.AVAILABLE) {
            Matcher timeMatcher = AVAILABLE_TIME_PATTERN.matcher(resPageOutput);
            
            times += " ";
            while (timeMatcher.find()) {
                times += timeMatcher.group(1) + " ";
            }
        }

        return new ReservationCheckResult(result, times + url.build());
    }

    private static GenericUrl getOpenTableReservationPageUrl(Restaurant restaurant, Date date, int partySize) {
        GenericUrl url = new GenericUrl(OPENTABLE_OPENTABLES_URL);

        url.put("d", OPENTABLE_DATE_FORMAT.format(date).toString());

        // OpenTable variable, not sure what it does yet
        url.put("m", "4");
        
        // 'p' appears to be the party size
        url.put("p", Integer.toString(partySize));

        // 'r' is the id of the restaurant
        url.put("r", Integer.toString(restaurant.getRestaurantId()));

        // 't' is apparently the reservation type, in this case a restaurant
        url.put("t", "rest");

        return url;
    }
}
