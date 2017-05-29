package dataManagement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateCalc {

	private static SimpleDateFormat messageDate = new SimpleDateFormat("HHmmss"),
			deviceDate = new SimpleDateFormat("yyyyMMddHHmmss"),
			loggerDate = new SimpleDateFormat("dd-MM-YYYY_HH.mm.ss");;

	public static String getMessageDate() {
		return messageDate.format(new Date());
	}

	public static String getDeviceDate() {
		return deviceDate.format(new Date());
	}

	public static String getLoggerDate() {
		return loggerDate.format(new Date());
	}

	public static synchronized String getDeviceDate(int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, days);
		return deviceDate.format(cal.getTime());
	}

	public static boolean isDeviceDateCorrect(String date) {
		try {
			deviceDate.parse(date);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	public static int getLowestDateDevice(String string) { // 14
		Date[] dates = new Date[50];
		for (int i = 0; i < dates.length; i++) {
			try {
				dates[i] = deviceDate.parse(string.substring(i * 14, (i + 1) * 14 - 1));
			} catch (ParseException e) {
				Logger.getInstance().log("Error CD0: Could not parse the date! #BlameBene");
				e.printStackTrace();
				return i;
			}
		}
		int number = 0;
		for (int i = 0; i < dates.length - 1; i++)
			if (dates[i].compareTo(dates[i + 1]) < 0)
				number = i;

		return number;
	}

	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder();
		Calendar cal = Calendar.getInstance();
		for (int i = 0; i < 200; i++) {
			cal.setTime(new Date());
			cal.add(Calendar.HOUR, (int) (Math.random() * 999999));
			sb.append(DateCalc.deviceDate.format(cal.getTime()));
		}
		System.out.println(sb.toString());
	}

}
