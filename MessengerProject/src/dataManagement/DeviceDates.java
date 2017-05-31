package dataManagement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import server.Constants;

public class DeviceDates {

	private File file;
	private RandomAccessFile raf;
	private final int BYTES_PER_DATE = 14, BYTES_PER_USER = BYTES_PER_DATE * 50;

	DeviceDates(File saveDir) {
		file = new File(saveDir, "devicesDate.txt");
		boolean makeHeader = !file.exists();
		try {
			raf = new RandomAccessFile(file, "rw");
		} catch (FileNotFoundException e) {
			Logger.getInstance().log("Error DD0: Could not init the RandomAccessFile! #BlameBene");
			new FileException(file);
		}
		if (makeHeader) {
			Logger.getInstance().log("Notice DD0: DevicesDate does not exists. Making one!");
			try {
				for (int b = 0; b < BYTES_PER_USER; b++)
					raf.write(Constants.FILLER);
			} catch (IOException e) {
				Logger.getInstance().log("Error DD1: Could not init the RandomAccessFile! #BlameBene");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Logs a Tag in. Tag must be above 0. Returns the new deviceNr and last
	 * login date in form of DeviceLogin. If something went wrong null is
	 * returned!
	 */
	DeviceLogin login(int tag, int device) {
		if (isCorrect(tag) || device < 0 || device > BYTES_PER_USER / BYTES_PER_DATE) {
			String date = DateCalc.getDeviceDate(Constants.DAYS_GET_FOR_NEW_DEVICE_LOGIN, 0, 0, 0);
			device = register(tag, date);
			return new DeviceLogin(date, device);
		}
		try {
			raf.seek(BYTES_PER_USER * tag + device * BYTES_PER_DATE);
		} catch (IOException e) {
			Logger.getInstance().log(
					"Error DD2: Could not read from the file. Wrong input, file is corrupt or File faulty. #BlameBene");
			e.printStackTrace();
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < BYTES_PER_DATE; i++) {
			try {
				sb.append((char) raf.read());
			} catch (IOException e) {
				Logger.getInstance().log(
						"Error DD3: Could not read from the file. Wrong input, file is corrupt or File faulty. #BlameBene");
				e.printStackTrace();
				return null;
			}
		}
		if (sb.charAt(0) == Constants.FILLER) {
			Logger.getInstance().log(
					"Error DD13: Device Nr returned Filler. Wrong input, file is corrupt or File faulty. #BlameBene");
			String date = DateCalc.getDeviceDate(Constants.DAYS_GET_FOR_NEW_DEVICE_LOGIN, 0, 0, 0);
			device = register(tag, date);
			return new DeviceLogin(date, device);
		}
		String result = sb.toString();
		if (!DateCalc.isDeviceDateCorrect(result)) {
			Logger.getInstance().log(
					"Error DD14: Device Nr returned wrong Date. Wrong input, file is corrupt or File faulty. #BlameBene");
			try {
				raf.seek(BYTES_PER_USER * tag + device * BYTES_PER_DATE);
			} catch (IOException e) {
				Logger.getInstance().log("Error DD15: Could not seek. #BlameBene");
				e.printStackTrace();
				return null;
			}
			for (int i = 0; i < BYTES_PER_DATE; i++) {
				try {
					raf.write(Constants.FILLER);
				} catch (IOException e) {
					Logger.getInstance().log("Error DD16: Could not write. #BlameBene");
					e.printStackTrace();
					return null;
				}
			}
			String date = DateCalc.getDeviceDate(Constants.DAYS_GET_FOR_NEW_DEVICE_LOGIN, 0, 0, 0);
			device = register(tag, date);
			return new DeviceLogin(date, device);
		}
		return new DeviceLogin(result, device);
	}

	/**
	 * Checks if a tag has to be added to the file. Returns wheter it created to
	 * the tag
	 */
	private boolean isCorrect(int tag) {
		int amount = 0;
		try {
			amount = (int) (raf.length() / BYTES_PER_USER);
		} catch (IOException e) {
			Logger.getInstance().log("Error DD9: Could not get the length. #BlameBene");
			e.printStackTrace();
			return false;
		}
		amount = tag - (amount - 1);
		System.out.println(amount);
		if (amount < 1)
			return false;
		if (amount > 1)
			Logger.getInstance().log("Notice DD1: Amount is somehow " + amount
					+ ". This could mean that the input was wrong or the file is damaged!");
		try {
			raf.seek(raf.length());
		} catch (IOException e) {
			Logger.getInstance().log("Error DD10: Could not seek. #BlameBene");
			e.printStackTrace();
			return false;
		}
		for (int i = 0; i < amount * BYTES_PER_USER; i++) {
			try {
				raf.write(Constants.FILLER);
			} catch (IOException e) {
				Logger.getInstance().log("Error DD11: Could not write. #BlameBene");
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	private int register(int tag, String date) {
		try {
			raf.seek(BYTES_PER_USER * tag);
		} catch (IOException e) {
			Logger.getInstance().log(
					"Error DD4: Could not read from the file. Wrong input, file is corrupt or File faulty. #BlameBene");
			e.printStackTrace();
			return -1;
		}
		StringBuilder sb = new StringBuilder();
		char input = 's';
		try {
			input = (char) raf.read();
		} catch (IOException e) {
			Logger.getInstance().log(
					"Error DD5: Could not read from the file. Wrong input, file is corrupt or File faulty. #BlameBene");
			e.printStackTrace();
			return -1;
		}
		try {
			while (input != Constants.FILLER && raf.getFilePointer() < BYTES_PER_USER * (tag + 1)) {
				sb.append(input);
				input = (char) raf.read();
			}
		} catch (IOException e) {
			Logger.getInstance().log("Error DD6: Could not get the File pointer. #BlameBene");
			e.printStackTrace();
			return -1;
		}
		int deviceNr = -1;
		if (sb.length() != 0 && input != Constants.FILLER)
			deviceNr = DateCalc.getLowestDateDevice(sb.toString());
		else
			try {
				deviceNr = (int) (((raf.getFilePointer() - 1) - (tag * BYTES_PER_USER)) / BYTES_PER_DATE);
			} catch (IOException e) {
				Logger.getInstance().log("Error DD7: Could not get the File pointer. #BlameBene");
				e.printStackTrace();
				return -1;
			}
		try {
			raf.seek(BYTES_PER_USER * tag + BYTES_PER_DATE * deviceNr);
		} catch (IOException e) {
			Logger.getInstance().log("Error DD8: Could not seek. #BlameBene");
			e.printStackTrace();
			return -1;
		}
		for (int i = 0; i < date.length(); i++) {
			try {
				raf.write(date.charAt(i));
			} catch (IOException e) {
				Logger.getInstance().log("Error DD17: Could not write. #BlameBene");
				e.printStackTrace();
				return -1;
			}
		}
		return deviceNr;
	}

	void logout(int tag, int device, boolean timeout) {
		try {
			raf.seek(BYTES_PER_USER * tag + BYTES_PER_DATE * device);
		} catch (IOException e) {
			Logger.getInstance().log("Error DD18: Could not seek! Maybe wrong input? (tag:" + tag + ", device:" + device
					+ ") #BlameBene");
			e.printStackTrace();
			return;
		}
		String date = "";
		if (timeout)
			date = DateCalc.getDeviceDate(0, 0, 0, Constants.TIMEOUT_IN_SECONDS);
		else
			date = DateCalc.getDeviceDate();
		for (int i = 0; i < date.length(); i++) {
			try {
				raf.write(date.charAt(i));
			} catch (IOException e) {
				Logger.getInstance().log("Error DD19: Could not write #BlameBene");
				e.printStackTrace();
				return;
			}
		}
	}

}
