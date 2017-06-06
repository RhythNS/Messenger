package userDataManagement;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateCalc {

	private static SimpleDateFormat forDay = new SimpleDateFormat("HHmmss"), forYear = new SimpleDateFormat("yyyyMMdd");

	public static String getForYearDate() {
		return forYear.format(new Date());
	}

	public static String getTime(){
		return null;
	}
	public synchronized static String getNextDay(String currentDay) {
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

}
