package userDataManagement;

import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;

import dataManagement.FileException;

/**
 * The less cared DataManagement child \(>.<)/
 *
 * @author RhythNS_
 */
public class DataManagement {

	private MessageDirector messageHandler;
	private FileSaver fileSaver;

	private Thread nextDay;

	public DataManagement(File saveDirectory) {
		if (saveDirectory == null) {
			saveDirectory = new File(System.getProperty("user.dir") + "/MessengerSaves");
			saveDirectory.mkdir();
		}
		if (!saveDirectory.isDirectory())
			new FileException(saveDirectory);

		messageHandler = new MessageDirector(saveDirectory);

		nextDay = new Thread(new Runnable() {
			@Override
			public void run() {
				LocalTime lt = LocalTime.now();
				int time = lt.toSecondOfDay(), time2 = time;
				while (true) {
					time = lt.toSecondOfDay();
					if (time < time2) {
						messageHandler.addDay();
					}
					time2 = time;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						System.err.println("Error TCFD0: InterrupptedException! #BlameBene");
						e.printStackTrace();
					}
					lt = LocalTime.now();
				}
			}
		});
		nextDay.start();
	}

	public ArrayList<Message> readAllTag(String date, int tag) {
		return messageHandler.getMessages(tag, date);
	}

	public boolean saveMessage(String date, int fromTag, int toTag, String message) {
		return messageHandler.writeMessage(date, fromTag, toTag, message);
	}

	public boolean saveFile(int fromTag, int toTag, String name, byte[] data) {
		return fileSaver.write(fromTag, toTag, name, data);
	}

}
