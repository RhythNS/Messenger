package userDataManagement;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import user.Message;

import dataManagement.FileException;

/**
 * The less cared DataManagement child \(>.<)/
 *
 * @author RhythNS_
 */
public class DataManagement {

	private MessageDirector messageHandler;
	private FileSaver fileSaver;

	private File deviceNrFile;

	public DataManagement(File saveDirectory) {
		if (saveDirectory == null) {
			saveDirectory = new File(System.getProperty("user.dir") + "/MessengerSaves");
			saveDirectory.mkdir();
		}
		if (!saveDirectory.isDirectory())
			new FileException(saveDirectory);

		messageHandler = new MessageDirector(saveDirectory);

		File saveFiles = new File(saveDirectory, "files");
		saveFiles.mkdirs();
		fileSaver = new FileSaver(saveFiles);

		deviceNrFile = new File(saveDirectory, "deviceNr.txt");
		if (!deviceNrFile.exists())
			try {
				deviceNrFile.createNewFile();
			} catch (IOException e1) {
				System.err.println("Could not make deviceNrFile! #BlameBene");
				e1.printStackTrace();
			}
	}

	public ArrayList<Message> readAllTag(String date, int tag) {
		return messageHandler.getMessages(tag, date);
	}

	public boolean saveMessage(String date, int fromTag, int toTag, String message) {
		return messageHandler.writeMessage(date, fromTag, toTag, message);
	}

	public String saveFile(int fromTag, int toTag, String name, byte[] data) {
		return fileSaver.write(fromTag, toTag, name, data);
	}

	public void addDay() {
		messageHandler.addDay();
	}

	public void saveDeviceNr(int deviceNumber) {
		DeviceNrSaver.save(deviceNumber, deviceNrFile);
	}

	public int getDeviceNr() {
		return DeviceNrSaver.load(deviceNrFile);
	}

}
