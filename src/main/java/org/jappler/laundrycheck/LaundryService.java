package org.jappler.laundrycheck;

import java.util.Calendar;

import org.jappler.laundrycheck.OpenTableService.ReservationCheckResult;
import org.jappler.laundrycheck.OpenTableService.ReservationCheckResult.ReservationCheckResultType;
import org.jappler.laundrycheck.OpenTableService.Restaurant;

public class LaundryService {

    /** Check two more days just to be sure **/
    private static final int TWO_MONTHS_PLUS_TWO = 31 * 2 + 2;
    
    /** Be nice when scraping public pages, in ms **/
    private static final int WAIT_TIME_BETWEEN_CHECKS = 9 * 1000;
    
    /**
     * Check for a reservation at French Laundry during the next
     * two months for a given party size
     * @return The result of the checks
     */
    public static ReservationCheckResult checkFLReservationsForNextTwoMonths(int partySize) {
        Calendar cal = Calendar.getInstance();

        // Use 7:00PM as OpenTable checks 2 hours before and after (5-9pm)
        cal.set(Calendar.HOUR, 7);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.AM_PM, Calendar.PM);

        ReservationCheckResult result = null;
        
        for (int i = 0; i < TWO_MONTHS_PLUS_TWO; i++) {
            Calendar current = Calendar.getInstance();
            current.setTime(cal.getTime());

            current.add(Calendar.DAY_OF_YEAR, i);

            result = OpenTableService.checkReservationsForDay(Restaurant.FRENCH_LAUNDRY, current.getTime(), partySize);

            System.out.println("Checking for day " + current.getTime().toString() + ": " + result.getType());

            if (result.getType() != ReservationCheckResultType.UNAVAILABLE)
                return result;

            try {
                Thread.sleep(WAIT_TIME_BETWEEN_CHECKS);
            } catch (InterruptedException e) { }
        }

        return result;
    }
}
