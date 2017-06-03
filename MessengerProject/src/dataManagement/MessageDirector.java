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
		dir.mkdirs();
	}

	Mailbox getMessages(int tag, String date, int groupTags[]) {
		if (date.length() != 14) {
			Logger.getInstance().log("Error MD0:  #BlameBene");
			return null;
		}
		String subDate = date.substring(0, 8);
		for (int i = 0; i < indexList.length; i++)
			if (indexList[i].getDate().equals(subDate))
				synchronized (indexList[i].getLock()) {
					return indexList[i].readAll(date.substring(8, date.length()), tag, groupTags);
				}
		return null;
	}

	boolean writeMessage(String date, int fromTag, int toTag, String message) {
		String subDate = date.substring(0, 8);
		for (int i = 0; i < indexList.length; i++)
			if (indexList[i].getDate().equals(subDate))
				synchronized (indexList[i].getLock()) {
					return indexList[i].write(fromTag, toTag, message, date.substring(8));
				}
		return false;
	}

	boolean writeFile(String date, int fromTag, int toTag, String file) {
		String subDate = date.substring(0, 8);
		for (int i = 0; i < indexList.length; i++)
			if (indexList[i].getDate().equals(subDate))
				synchronized (indexList[i].getLock()) {
					return indexList[i].saveFile(fromTag, toTag, file, date.substring(8));
				}
		return false;
	}

	void addDay() {
		String previousDay = getPreviousDay();
		pointer++;
		if (pointer > indexList.length - 1)
			pointer = 0;
		if (indexList[pointer] != null)
			synchronized (indexList[pointer].getLock()) {
				indexList[pointer].kill();
			}
		indexList[pointer] = new Index(previousDay, dir);
	}

	private String getPreviousDay() {
		int i = pointer - 1;
		if (i == -1)
			i = indexList.length - 1;
		return DateCalc.getNextDay(indexList[i].getDate());
	}

}
