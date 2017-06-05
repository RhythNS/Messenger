package userDataManagement;

import java.io.File;

public class MessageDirector {
	private Index[] indexList;
	int pointer;
	private File dir;

	MessageDirector(File sysDir) {
		indexList = new Index[DataConstants.DAYS_KEPT_IN_RAM];
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

			String currentDate = DateCalc.getForYearDate();

			for (int i = 0; i < length; i++) {
				indexList[i] = new Index(arr[i + difference], dir);
				if (indexList[i].getDate().equals(currentDate))
					currentDayFound = true;
			}
			if (!currentDayFound) {
				pointer = 0;
				indexList[pointer].close();
				indexList[pointer] = new Index(currentDate, dir);
			} else
				pointer = length - 1;
		}
	}

	Mailbox getMessages(int tag, String date) {
		if (date.length() != 8) {
			System.err.println("Length of date is not right! #BlameBene");
			return null;
		}
		for (int i = 0; i < indexList.length; i++)
			if (indexList[i] != null && indexList[i].getDate().equals(date))
				synchronized (indexList[i].getLock()) {
					return indexList[i].readAll(tag);
				}
		Index olderIndex = getOlderDay(date);
		if (olderIndex != null) {
			Mailbox mb = olderIndex.readAll(tag);
			olderIndex.close();
			return mb;
		}
		return null;
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
		Index olderIndex = getOlderDay(subDate);
		if (olderIndex != null) {
			boolean alright = olderIndex.write(fromTag, toTag, message, date.substring(8));
			olderIndex.close();
			return alright;
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
		Index olderIndex = getOlderDay(subDate);
		if (olderIndex != null) {
			boolean alright = olderIndex.saveFile(fromTag, toTag, file, date.substring(8));
			olderIndex.close();
			return alright;
		}
		return false;
	}

	synchronized Index getOlderDay(String date) {
		File file = new File(dir, date + ".txt");
		if (!file.exists())
			return null;
		return new Index(date, dir);
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
				indexList[pointer].close();
			}
		indexList[pointer] = new Index(nextDay, dir);
	}

}
