package dataManagement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


public class MessageIO {

	private RandomAccessFile raf;

	MessageIO(File file) {
		try {
			raf = new RandomAccessFile(file, "rw");
		} catch (FileNotFoundException e) {
			new FileException(file);
			Logger.getInstance().log("Error MIO0: Could not init RandomAccessFile. #BlameBene");
			e.printStackTrace();
		}
		if (file.length() > 0) {
			Logger.getInstance()
					.log("Notice MIO0: File " + file.getName() + " already has data in it. Writing to the end of it!");
			try {
				raf.seek(raf.length());
			} catch (IOException e) {
				Logger.getInstance().log("Error MIO2: Could not seek or get the file length! #BlameBene");
				e.printStackTrace();
			}
		}
	}

	long[] write(String message) {
		long pointerFrom;
		try {
			pointerFrom = raf.length();
		} catch (IOException e1) {
			Logger.getInstance().log("Error MIO4: Could not get the file length! #BlameBene");
			e1.printStackTrace();
			return null;
		}
		try {
			raf.seek(pointerFrom);
		} catch (IOException e) {
			Logger.getInstance().log("Error MIO3: Could not seek! #BlameBene");
			e.printStackTrace();
			return null;
		}
		for (int i = 0; i < message.length(); i++) {
			try {
				raf.write(message.charAt(i));
			} catch (IOException e) {
				Logger.getInstance().log("Error MIO5: Could not write! #BlameBene");
				e.printStackTrace();
				return null;
			}
		}
		try {
			long[] ret = { pointerFrom, raf.length() - 1 };
			return ret;
		} catch (IOException e) {
			Logger.getInstance().log("Error MIO6: Could not get the file length! #BlameBene");
			e.printStackTrace();
			return null;
		}
	}

	Message read(Message msg) {
		try {
			raf.seek(msg.pointerFrom);
		} catch (IOException e) {
			Logger.getInstance().log("Error MIO7: Could not seek! #BlameBene");
			e.printStackTrace();
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (long i = msg.pointerFrom; i < msg.pointerTo + 1; i++) {
			try {
				sb.append((char)raf.read());
			} catch (IOException e) {
				Logger.getInstance().log("Error MIO8: Could not read! #BlameBene");
				e.printStackTrace();
				return null;
			}
		}
		msg.setContent(sb.toString());
		return msg;
	}

	boolean close() {
		try {
			raf.close();
		} catch (IOException e) {
			Logger.getInstance().log("Error MIO10: Could not close! #BlameBene");
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
