package dataManagement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Users {

	private File users;
	private RandomAccessFile raf;
	final long BYTES_PER_USER = 100; // TODO Find a value
	final char FILLER = ',', SEPERATOR = '.';

	public Users(File sysLine) {
		users = new File(sysLine.getAbsolutePath() + "/users.txt");
		boolean makeHeader = !users.exists();
		try {
			raf = new RandomAccessFile(users, "rw");
		} catch (FileNotFoundException e) {
			System.err.println("Error U3: Could not init the RandomAccessFile! #BlameBene");
			Logger.getInstance().log("Error U3: Could not init the RandomAccessFile! #BlameBene");
			new FileException(users);
		}
		if (makeHeader) {
			Logger.getInstance().log("Users File does not exists. Making one!");
			try {
				raf.writeBytes(
						"THIS_IS_THE_USERNAME_AND_PASSWORD_SAVE_FILE_NOEDITTHIS_IS_THE_USERNAME_AND_PASSWORD_SAVE_FILE_NOEDIT");
			} catch (IOException e) {
				System.err.println("Error U4: Could not init the RandomAccessFile! #BlameBene");
				Logger.getInstance().log("Error U4: Could not init the RandomAccessFile! #BlameBene");
				e.printStackTrace();
			}
		}
	}

	boolean login(int tag, String password) {
		try {
			raf.seek(tag * BYTES_PER_USER);
		} catch (IOException e) {
			System.err.println("Error U6: Could not seek for login! #BlameBene");
			Logger.getInstance().log("Error U6: Could not seek for login! #BlameBene");
			e.printStackTrace();
			return false;
		}
		try {
			while ((char) raf.read() != SEPERATOR)
				;
		} catch (IOException e) {
			System.err.println("Error U7: Could not read for login! #BlameBene");
			Logger.getInstance().log("Error U7: Could not read for login! #BlameBene");
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		char c = 's';
		try {
			c = (char) raf.read();
		} catch (IOException e) {
			System.err.println("Error U7: Could not read for login! #BlameBene");
			Logger.getInstance().log("Error U7: Could not read for login! #BlameBene");
			e.printStackTrace();
		}
		try {
			while (c != FILLER && raf.getFilePointer() < (tag + 1) * BYTES_PER_USER) {
				sb.append(c);
				c = (char) raf.read();
			}
		} catch (IOException e) {
			System.err.println("Error U8: Could not read or getFilePointer for login! #BlameBene");
			Logger.getInstance().log("Error U8: Could not read or getFilePointer for login! #BlameBene");
			e.printStackTrace();
		}
		return sb.toString().equalsIgnoreCase(password);
	}

	int register(String username, String password) {
		if (username.length() + password.length() > BYTES_PER_USER && isUsernameInUse(username))
			return 0;
		long size = getSize();
		try {
			raf.seek(size);
		} catch (IOException e) {
			System.err.println("Error U0: Could not seek! #BlameBene");
			Logger.getInstance().log("Error U0: Could not seek! #BlameBene");
			e.printStackTrace();
			return 0;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(username);
		sb.append(SEPERATOR);
		sb.append(password);
		for (int i = sb.length(); i < BYTES_PER_USER; i++) {
			sb.append(FILLER);
		}
		try {
			raf.writeBytes(sb.toString());
		} catch (IOException e) {
			System.err.println("Error U0: Could not write to Users! #BlameBene");
			Logger.getInstance().log("Error U1: Could not write to Users! #BlameBene");
			e.printStackTrace();
		}
		Logger.getInstance().log(username + " has registerd!");
		return (int) (size / BYTES_PER_USER);
	}

	private boolean isUsernameInUse(String username) {
		if (getSize() <= BYTES_PER_USER)
			return false;
		for (long i = BYTES_PER_USER; i < getSize(); i += BYTES_PER_USER) {
			try {
				raf.seek(i);
			} catch (IOException e1) {
				System.err.println("Error U5: Could not seek for username! #BlameBene");
				Logger.getInstance().log("Error U5: Could not seek from username! #BlameBene");
				e1.printStackTrace();
			}
			StringBuilder sb = new StringBuilder();
			char c = 's';
			try {
				c = (char) raf.read();
			} catch (IOException e) {
				System.err.println("Error U2: Could not read from User! #BlameBene");
				Logger.getInstance().log("Error U2: Could not read from User! #BlameBene");
				e.printStackTrace();
				return true;
			}
			while (c != SEPERATOR) {
				sb.append(c);
				try {
					c = (char) raf.read();
				} catch (IOException e) {
					System.err.println("Error U2: Could not read from User! #BlameBene");
					Logger.getInstance().log("Error U2: Could not read from User! #BlameBene");
					e.printStackTrace();
					return true;
				}
			}
			if (sb.toString().equalsIgnoreCase(username))
				return true;
		}
		return false;
	}

	private long getSize() {
		return users.length();
	}

}
