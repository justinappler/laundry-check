package org.jappler.laundrycheck;

import org.jappler.laundrycheck.OpenTableService.ReservationCheckResult;

public class LaundryCheck {

    private static final int DEFAULT_PARTY_SIZE = 2;
    private static final String DEFAULT_PHONE_NUMBER = "+14155551212";

    /**
     * Checks the next two months for French Laundry Reservations
     * using the public OpenTable reservation check website.  Sends
     * a notification SMS if a reservation is found using the Twilio
     * API.
     * 
     * Be sure to set the environment variables specified in {@link SmsService}
     * before using.
     * 
     * Usage
     *    <code>
     *      ./laundryService  [-phone <phoneNumber>] [-party <partySize>]
     *    </code>
     */
    public static void main(String[] args) throws Exception {
        
        int partySize = getPartySizeOrDefault(args);
        String phoneNumber = getPhoneNumberOrDefault(args);
        
        ReservationCheckResult checkResult = LaundryService.checkFLReservationsForNextTwoMonths(partySize);
        SmsService smsService = SmsService.getInstance();
        
        switch (checkResult.getType()) {
        case AVAILABLE:
            System.out.println("Reservation found! " + checkResult.getAvailableUrl());
            smsService.sendSMS(phoneNumber, "FLRes: " + checkResult.getAvailableUrl());
            break;
        case UNAVAILABLE:
        case TOO_SOON:
        case OFFLINE:
        default:
            System.out.println("No Reservation: " + checkResult.getType());
            break;
        }
    }

    private static String getPhoneNumberOrDefault(String[] args) {
        String phoneNumber = DEFAULT_PHONE_NUMBER;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-phone") && i + 1 < args.length)
                phoneNumber = args[i + 1];
        }
        return phoneNumber;
    }

    private static int getPartySizeOrDefault(String[] args) {
        int partySize = DEFAULT_PARTY_SIZE;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-party") && i + 1 < args.length)
                partySize = Integer.parseInt(args[i + 1]);
        }
        return partySize;
    }
}
