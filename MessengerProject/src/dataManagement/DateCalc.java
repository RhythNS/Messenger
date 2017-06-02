package dataManagement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateCalc {

	private static SimpleDateFormat forDay = new SimpleDateFormat("HHmmss"),
			wholeYear = new SimpleDateFormat("yyyyMMddHHmmss"),
			logger = new SimpleDateFormat("dd-MM-YYYY_HH.mm.ss"),
			forYear = new SimpleDateFormat("yyyyMMdd");

	public static String getMessageDate() {
		return forDay.format(new Date());
	}

	public static String getDeviceDate() {
		return wholeYear.format(new Date());
	}

	public static String getLoggerDate() {
		return logger.format(new Date());
	}

	public static synchronized String getDeviceDate(int days, int hours, int minutes, int seconds) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, days);
		cal.add(Calendar.HOUR, hours);
		cal.add(Calendar.MINUTE, minutes);
		cal.add(Calendar.SECOND, seconds);
		return wholeYear.format(cal.getTime());
	}

	public static boolean isDeviceDateCorrect(String date) {
		try {
			wholeYear.parse(date);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	public static int getLowestDateMessageDirector(MessageIO[] messages) {
		Date[] dates = new Date[messages.length];
		for (int i = 0; i < messages.length; i++) {
			if (messages[i] != null)
				try {
					dates[i] = forYear.parse(messages[i].getDate());
				} catch (ParseException e) {
					Logger.getInstance().log("Error CD1: Could not parse the date! #BlameBene");
					e.printStackTrace();
					return -1;
				}
		}
		int number = 0;
		Date date = null;
		for (; number < dates.length; number++) {
			if (dates[number] != null) {
				date = dates[number];
				break;
			}
		}
		if (date == null) {
			Logger.getInstance().log("Error CD2: All messages were null! #BlameBene");
			return -1;
		}
		for (int i = number + 1; i < dates.length; i++)
			if (dates[i] != null && date.compareTo(dates[i]) > 0) {
				date = dates[i];
				number = i;
			}
		return number;
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

	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder();
		Calendar cal = Calendar.getInstance();
		MessageIO[] messages = new MessageIO[50];
		for (int i = 0; i < messages.length; i++) {
			cal.setTime(new Date());
			cal.add(Calendar.HOUR, (int) (Math.random() * 999999));
			messages[i] = new MessageIO(forYear.format(cal.getTime()), null);
			System.out.print(i + ": " + messages[i].getDate() + "\n");
		}
		System.out.println();
		System.out.println(DateCalc.getLowestDateMessageDirector(messages));
	}

}
