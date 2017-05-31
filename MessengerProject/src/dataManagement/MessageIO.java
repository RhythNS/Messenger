package dataManagement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import server.Constants;

public class MessageIO {

	private RandomAccessFile raf;
	private final Object lock = new Object();
	private String date;
	private File dir;

	MessageIO(String date, File messagesDir) {
		this.date = date;
		dir = new File(messagesDir, date);
		dir.mkdirs();
		File randomAccessFile = new File(dir, "raf.txt");
		try {
			randomAccessFile.createNewFile();
		} catch (IOException e1) {
			new FileException(randomAccessFile);
			Logger.getInstance().log("Error MIO1: Could not create a new file! #BlameBene");
			e1.printStackTrace();
		}
		try {
			raf = new RandomAccessFile(randomAccessFile, "rw");
		} catch (FileNotFoundException e) {
			new FileException(randomAccessFile);
			Logger.getInstance().log("Error MIO0: Could not init RandomAccessFile. #BlameBene");
			e.printStackTrace();
		}
		if (randomAccessFile.length() > 0) {
			Logger.getInstance()
					.log("Notice MIO0: File " + randomAccessFile.getName() + " already has data in it. Writing to the end of it!");
			try {
				raf.seek(raf.length());
			} catch (IOException e) {
				Logger.getInstance().log("Error MIO2: Could not seek or get the file length! #BlameBene");
				e.printStackTrace();
			}
		}
	}

	long[] write(int from, int to, String message) {
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
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString(from, Character.MAX_RADIX));
		sb.append(Constants.SEPERATOR);
		sb.append(Integer.toString(to, Character.MAX_RADIX));
		sb.append(Constants.SEPERATOR);
		for (int i = 0; i < sb.length(); i++) {
			try {
				raf.write(sb.charAt(i));
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

	TextMessage read(TextMessage msg) {
		try {
			raf.seek(msg.pointerFrom);
		} catch (IOException e) {
			Logger.getInstance().log("Error MIO7: Could not seek! #BlameBene");
			e.printStackTrace();
			return null;
		}
		StringBuilder[] sb = new StringBuilder[3];
		for (int i = 0; i < sb.length; i++)
			sb[i] = new StringBuilder();

		int incrementer = 0;
		for (long i = msg.pointerFrom; i < msg.pointerTo + 1; i++) {
			try {
				char c = (char) raf.read();
				if (incrementer < 3 && c == Constants.SEPERATOR) {
					incrementer++;
					continue;
				}
				sb[incrementer].append(c);
			} catch (IOException e) {
				Logger.getInstance().log("Error MIO8: Could not read! #BlameBene");
				e.printStackTrace();
				return null;
			}
		}
		if (sb[0].length() == 0 || sb[1].length() == 0 || sb[2].length() == 0) {
			Logger.getInstance().log("Error MIO9: Something went wrong with getting the messages. Lengths: ( "
					+ sb[0].length() + "/" + sb[1].length() + "/" + sb[2].length() + ") #BlameBene");
			return null;
		}
		msg.from = Integer.parseInt(sb[0].toString(), Character.MAX_RADIX);
		msg.to = Integer.parseInt(sb[1].toString(), Character.MAX_RADIX);
		msg.setContent(sb[2].toString());
		return msg;
	}

	Object getLock() {
		return lock;
	}

	String getDate() {
		return date;
	}

	File getDir() {
		return dir;
	}
}
