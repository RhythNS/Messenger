package dataManagement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Users {

	// TODO MIGHT WANT TO REWRITE SINCE A SEPERATOR IS NOT A GOOD IDEA

	private File users;
	private RandomAccessFile raf;
	private BinaryTreeFile binaryTreeFile;
	final long BYTES_PER_USER = 100;
	final char FILLER = '#', SEPERATOR = ',';

	Users(File sysLine) {
		users = new File(sysLine, "users.txt");
		boolean makeHeader = !users.exists();
		try {
			raf = new RandomAccessFile(users, "rw");
		} catch (FileNotFoundException e) {
			Logger.getInstance().log("Error U3: Could not init the RandomAccessFile! #BlameBene");
			new FileException(users);
		}
		if (makeHeader) {
			Logger.getInstance().log("Notice U0: Users File does not exists. Making one!");
			try {
				for (byte b = 0; b < BYTES_PER_USER; b++)
					raf.writeBytes("#");
			} catch (IOException e) {
				Logger.getInstance().log("Error U4: Could not init the RandomAccessFile! #BlameBene");
				e.printStackTrace();
			}
		}
		binaryTreeFile = new BinaryTreeFile(sysLine);
	}

	boolean login(int tag, String password) {
		try {
			raf.seek(tag * BYTES_PER_USER);
		} catch (IOException e) {
			Logger.getInstance().log("Error U6: Could not seek for login! #BlameBene");
			e.printStackTrace();
			return false;
		}
		try {
			while ((char) raf.read() != SEPERATOR)
				;
		} catch (IOException e) {
			Logger.getInstance().log("Error U7: Could not read for login! #BlameBene");
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		char c = 's';
		try {
			c = (char) raf.read();
		} catch (IOException e) {
			Logger.getInstance().log("Error U7: Could not read for login! #BlameBene");
			e.printStackTrace();
		}
		try {
			while (c != FILLER && raf.getFilePointer() < (tag + 1) * BYTES_PER_USER) {
				sb.append(c);
				c = (char) raf.read();
			}
		} catch (IOException e) {
			Logger.getInstance().log("Error U8: Could not read or getFilePointer for login! #BlameBene");
			e.printStackTrace();
		}
		return sb.toString().equalsIgnoreCase(password);
	}

	int login(String username, String password) {
		int tag = binaryTreeFile.getTag(username);
		if (tag > 0)
			return (login(tag, password)) ? tag : -1;
		Logger.getInstance().log("Error U10: Username not found! #BlameBene");
		return -1;
	}

	int register(String username, String password) {
		if (username.length() + password.length() > BYTES_PER_USER || binaryTreeFile
				.getTag(username) != -1/* isUsernameInUse(username) */)
			return 0;
		long size = getSize();
		try {
			raf.seek(size);
		} catch (IOException e) {
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
			Logger.getInstance().log("Error U1: Could not write to Users! #BlameBene");
			e.printStackTrace();
			return 0;
		}
		int tag = (int) (size / BYTES_PER_USER);
		if (!binaryTreeFile.addUser(tag, username)) {
			try {
				raf.setLength(size);
			} catch (IOException e) {
				Logger.getInstance().log(
						"Error U9: Could not delete the User! This will lead to major problems down the road! #BlameBene");
				e.printStackTrace();
				return 0;
			}
			return 0;
		}
		Logger.getInstance().log(username + " has registerd!");
		return tag;
	}

	private long getSize() {
		return users.length();
	}

}
