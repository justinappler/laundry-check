package org.jappler.laundrycheck;

public class LaundryCheck {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LaundryService.Result checkResult = LaundryService.checkReservationsForNextTwoMonths();
		
		switch (checkResult.getType()) {
			case AVAILABLE:
				System.out.println("Reservation found! " + checkResult.getAvailableUrl());
				LaundryText.sendSMS("+16504000475", "FLRes: " + checkResult.getAvailableUrl());
				break;
			case UNAVAILABLE:
				System.out.println("Reservations unavailable");
				break;
			case TOO_SOON:
				break;
		}
	}

}
