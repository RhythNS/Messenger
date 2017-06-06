package userDataManagement;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateCalc {

	private static SimpleDateFormat forDay = new SimpleDateFormat("HHmmss"), forYear = new SimpleDateFormat("yyyyMMdd");
	private static final Object calendarLock = new Object();

	public static void setOfSet(String date) {
		synchronized (calendarLock) {
			SimpleDateFormat wholeYear = new SimpleDateFormat("yyyyMMddHHmmss");
			Date serverDate = null;
			try {
				serverDate = wholeYear.parse(date);
			} catch (ParseException e) {
				System.err.println(" #BlameBene");
				e.printStackTrace();
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(serverDate);
			cal.getTimeZone();
		}
	}

	public static String getForYearDate() {
		return forYear.format(new Date());
	}

	public static String getTime(){
		return null;
	}
	public synchronized static String getNextDay(String currentDay) {
		synchronized (calendarLock) {
			Calendar cal = Calendar.getInstance();
			try {
				cal.setTime(forYear.parse(currentDay));
			} catch (ParseException e) {
				System.err.println("Could not parse the date! #BlameBene");
				e.printStackTrace();
				return null;
			}
			cal.add(Calendar.DATE, 1);
			return forYear.format(cal.getTime());
		}
	}

	public static String[] sort(File[] files) {
		String[] retArr = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			if (files[i] == null || !files[i].exists()) {
				System.err.println("One File was null or not existent! #BlameBene");
				return null;
			}
			retArr[i] = files[i].getName();
		}
		String temp;
		for (int i = 1; i < files.length; i++) {
			for (int j = i; j > 0; j--) {
				try {
					if (forYear.parse(retArr[j]).compareTo(forYear.parse(retArr[j - 1])) < 0) {
						temp = retArr[j];
						retArr[j] = retArr[j - 1];
						retArr[j - 1] = temp;
					}
				} catch (ParseException e) {
					System.err.println("Could not parse the date #BlameBene");
					e.printStackTrace();
					return null;
				}
			}
		}
		return retArr;
	}

	public static boolean forDayIsBelow(String date, String otherDate) {
		try {
			return forDay.parse(date).compareTo(forDay.parse(otherDate)) < 0;
		} catch (ParseException e) {
			System.err.println("Could not parse! #BlameBene");
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		SimpleDateFormat wholeYear = new SimpleDateFormat("yyyyMMddHHmmss");
		System.out.println(wholeYear.format(cal.getTime()));
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(wholeYear.format(cal.getTime()));
	}

}
