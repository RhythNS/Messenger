package dataManagement;

import java.io.File;

import server.Constants;

public class MessageDirector {

	private Index[] indexList;
	int pointer;
	private File dir;

	MessageDirector(File sysDir) {
		indexList = new Index[Constants.MESSAGES_DAYS_KEPT_IN_DATAMANAGMENT];
		dir = new File(sysDir, "messages");
		if (!dir.exists())
			dir.mkdirs();
		File[] files = dir.listFiles();
		if (files == null || files.length == 0) {
			pointer = 0;
			indexList[pointer] = new Index(DateCalc.getForYearDate(), dir);
		} else {
			boolean currentDayFound = false;

			String[] arr = DateCalc.sort(files);

			int difference = 0, length = arr.length;
			if (arr.length > indexList.length) {
				Logger.getInstance().log("Error MD1: Somehow there are more files than possible! #BlameBene");
				difference = arr.length - indexList.length;
				length = indexList.length;
			}

			String currentDate = DateCalc.getForYearDate();

			for (int i = 0; i < length; i++) {
				indexList[i] = new Index(arr[i + difference], dir);
				if (indexList[i].getDate().equals(currentDate))
					currentDayFound = true;
			}
			if (!currentDayFound) {
				Logger.getInstance().log("Error MD2: The current day was not found! #BlameBene");
				pointer = 0;
				indexList[pointer].kill();
				indexList[pointer] = new Index(currentDate, dir);
			} else
				pointer = length - 1;
		}
	}

	Mailbox getMessages(int tag, String date, int groupTags[]) {
		if (date.length() != 14) {
			Logger.getInstance().log("Error MD0: Length of date is not right! #BlameBene");
			return new Mailbox();
		}
		String subDate = date.substring(0, 8);
		for (int i = 0; i < indexList.length; i++)
			if (indexList[i] != null && indexList[i].getDate().equals(subDate))
				synchronized (indexList[i].getLock()) {
					return indexList[i].readAll(date.substring(8, date.length()), tag, groupTags);
				}
		return new Mailbox();
	}

	boolean writeMessage(String date, int fromTag, int toTag, String message) {
		String subDate = date.substring(0, 8);
		for (int i = 0; i < indexList.length; i++)
			if (indexList[i] != null && indexList[i].getDate().equals(subDate))
				synchronized (indexList[i].getLock()) {
					return indexList[i].write(fromTag, toTag, message, date.substring(8));
				}
		addDay();
		if (indexList[pointer] == null && indexList[pointer].getDate().equals(subDate))
			synchronized (indexList[pointer].getLock()) {
				return indexList[pointer].write(fromTag, toTag, message, date.substring(8));
			}

		return false;
	}

	boolean writeFile(String date, int fromTag, int toTag, String file) {
		String subDate = date.substring(0, 8);
		for (int i = 0; i < indexList.length; i++)
			if (indexList[i] != null && indexList[i].getDate().equals(subDate))
				synchronized (indexList[i].getLock()) {
					return indexList[i].saveFile(fromTag, toTag, file, date.substring(8));
				}

		addDay();
		if (indexList[pointer] == null && indexList[pointer].getDate().equals(subDate))
			synchronized (indexList[pointer].getLock()) {
				return indexList[pointer].saveFile(fromTag, toTag, file, date.substring(8));
			}

		return false;
	}

	synchronized void addDay() {
		String thisDay = indexList[pointer].getDate();

		if (thisDay.equals(DateCalc.getForYearDate()))
			return;

		String nextDay = DateCalc.getNextDay(thisDay);
		pointer++;
		if (pointer > indexList.length - 1)
			pointer = 0;
		if (indexList[pointer] != null)
			synchronized (indexList[pointer].getLock()) {
				indexList[pointer].kill();
			}
		indexList[pointer] = new Index(nextDay, dir);
	}

}
