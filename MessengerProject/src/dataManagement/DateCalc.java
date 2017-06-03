package dataManagement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateCalc {

	private static SimpleDateFormat forDay = new SimpleDateFormat("HHmmss"),
			wholeYear = new SimpleDateFormat("yyyyMMddHHmmss"), logger = new SimpleDateFormat("dd-MM-YYYY_HH.mm.ss"),
			forYear = new SimpleDateFormat("yyyyMMdd");
	private static final Object calendarLock = new Object();

	public static String getMessageDate() {
		return forDay.format(new Date());
	}

	public static String getDeviceDate() {
		return wholeYear.format(new Date());
	}

	public static String getLoggerDate() {
		return logger.format(new Date());
	}

	public static SimpleDateFormat getForDay() {
		return forDay;
	}

	public static SimpleDateFormat getForYear() {
		return forYear;
	}

	public static SimpleDateFormat getWholeYear() {
		return wholeYear;
	}

	public static SimpleDateFormat getLogger() {
		return logger;
	}

	public static String getDeviceDate(int days, int hours, int minutes, int seconds) {
		synchronized (calendarLock) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.DATE, days);
			cal.add(Calendar.HOUR, hours);
			cal.add(Calendar.MINUTE, minutes);
			cal.add(Calendar.SECOND, seconds);
			return wholeYear.format(cal.getTime());
		}
	}

	public static boolean isDeviceDateCorrect(String date) {
		try {
			wholeYear.parse(date);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	public static String getNextDay(String currentDay) {
		synchronized (calendarLock) {
			Calendar cal = Calendar.getInstance();
			try {
				cal.setTime(forYear.parse(currentDay));
			} catch (ParseException e) {
				Logger.getInstance().log("Error CD1: Could not parse the date! #BlameBene");
				e.printStackTrace();
				return null;
			}
			cal.add(Calendar.DATE, 1);
			return forYear.format(cal.getTime());
		}
	}

	public static int getLowestDateDevice(String[] strings) {
		Date[] dates = new Date[strings.length];
		for (int i = 0; i < dates.length; i++) {
			try {
				dates[i] = wholeYear.parse(strings[i]);
			} catch (ParseException e) {
				Logger.getInstance().log("Error CD0: Could not parse the date! #BlameBene");
				e.printStackTrace();
				return i;
			}
		}
		int number = 0;
		for (int i = 1; i < dates.length; i++)
			if (dates[number].compareTo(dates[i]) < 0)
				number = i;

		return number;
	}

	public static boolean isBelow(String date, String string) {
		try {
			return forDay.parse(date).compareTo(forDay.parse(string)) < 0;
		} catch (ParseException e) {
			Logger.getInstance().log("Error CD2: Could not parse! #BlameBene");
			e.printStackTrace();
		}
		return false;
	}

}
