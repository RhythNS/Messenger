package userDataManagement;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateCalc {

	private static SimpleDateFormat forDay = new SimpleDateFormat("HHmmss"), forYear = new SimpleDateFormat("yyyyMMdd"),
			wholeYear = new SimpleDateFormat("yyyyMMddHHmmss");
	private static final Object calendarLock = new Object();
	private static long miliOffset;

	public static void setOfSet(String date) {
		SimpleDateFormat wholeYear = new SimpleDateFormat("yyyyMMddHHmmss");
		Date serverDate = null;
		try {
			serverDate = wholeYear.parse(date);
		} catch (ParseException e) {
			System.err.println("Could not parse! As a result all messages will be displayed wrong! #BlameBene");
			e.printStackTrace();
		}
		miliOffset = new Date().getTime() - serverDate.getTime();
	}

	public static String getForYearDate() {
		return forYear.format(new Date());
	}

	public static Date getTime() {
		return new Date(new Date().getTime() - miliOffset);
	}

	public static SimpleDateFormat getWholeYear() {
		return wholeYear;
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
		String date = "20170607181125";
		SimpleDateFormat wholeYear = new SimpleDateFormat("yyyyMMddHHmmss");
		DateCalc.setOfSet(date);
		System.out.println(wholeYear.format(DateCalc.getTime()));
	}

}
