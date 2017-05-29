package dataManagement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BinaryTreeFile {

	private File tree;
	private RandomAccessFile raf;
	private final static int USERNAME_SIZE = 32, POINTER_SIZE = 6, DATA_SIZE = USERNAME_SIZE + POINTER_SIZE * 3;
	private final static char FILLER = '#';

	BinaryTreeFile(File sysLine) {
		tree = new File(sysLine, "tree.txt");
		if (!tree.exists()) {
			Logger.getInstance().log("Notice BTF0: BinaryTreeFile does not exists, making one!");
		}
		try {
			raf = new RandomAccessFile(tree, "rw");
		} catch (FileNotFoundException e) {
			Logger.getInstance().log("Error BTF0: Could not init RandomAccessFile! #BlameBene");
			e.printStackTrace();
		}
	}

	/**
	 * Adds a new User to the file. Returns wheter it succeeded or not!
	 */
	boolean addUser(int tag, String username) {
		if (username.length() > USERNAME_SIZE) {
			Logger.getInstance().log("Error BTF2: Can not save that username. It is too long! #BlameBene");
			return false;
		}
		try {
			if (raf.length() == 0) {
				return makeUser(tag, username);
			}
		} catch (IOException e) {
			Logger.getInstance().log("Error BTF1: Could not get the file length! #BlameBene");
			e.printStackTrace();
			return false;
		}
		int atPosition = 0;
		while (true) {
			try {
				raf.seek(atPosition * DATA_SIZE);
			} catch (IOException e2) {
				System.err.println("Error BTF18: Could not seek #BlameBene");
				e2.printStackTrace();
				return false;
			}
			StringBuilder sb = new StringBuilder();
			char input = 's';
			try {
				for (int i = 0; i < USERNAME_SIZE - 1; i++) {
					input = (char) raf.read();
					if (input == FILLER)
						break;
					sb.append(input);
				}
			} catch (IOException e) {
				Logger.getInstance().log("Error BTF6: Could not get the FilePointer or read! #BlameBene");
				e.printStackTrace();
				return false;
			}
			int result = sb.toString().compareTo(username);
			try {
				System.out.println(atPosition + " : " + raf.getFilePointer());
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			if (result == 0) {
				Logger.getInstance().log("Error BTF7: The Username is already registerd! #BlameBene");
				return false;
			} else if (result < 0) { // For example res = A, username = Z
				atPosition = seekAt((atPosition * DATA_SIZE) + USERNAME_SIZE + POINTER_SIZE);
			} else { // For example res = Z, username = A
				atPosition = seekAt((atPosition * DATA_SIZE) + USERNAME_SIZE + (POINTER_SIZE * 2));
			}
			if (atPosition == -2)
				return false;
			if (atPosition == -1) {
				String toWrite = "";
				try {
					toWrite = Integer.toString((int) (raf.length() / DATA_SIZE), Character.MAX_RADIX);
				} catch (IOException e1) {
					Logger.getInstance().log("Error BTF9: Could not get the file length! #BlameBene");
					e1.printStackTrace();
					return false;
				}
				if (toWrite.length() > POINTER_SIZE) {
					Logger.getInstance().log("Error BTF8: Pointer size is larger than it should be! ("
							+ toWrite.length() + ") #BlameBene");
					return false;
				}
				for (int i = 0; i < toWrite.length(); i++) {
					try {
						raf.write(toWrite.charAt(i));
					} catch (IOException e) {
						Logger.getInstance().log("Error BTF13: Could not write! #BlameBene");
						e.printStackTrace();
						return false;
					}
				}
				return makeUser(tag, username);
			}
		}
	}

	/**
	 * Seeks the pointer of the given position. Returns the new position. Can
	 * return -1 if no new position has been found. If this is the case, the
	 * pointer is placed at the pointers position! If an error occured -2 is
	 * returned!
	 */
	private int seekAt(long position) {
		try {
			raf.seek(position);
		} catch (IOException e) {
			System.err.println("Error BTF10: Could not seek! #BlameBene");
			e.printStackTrace();
			return -2;
		}
		StringBuilder sb = new StringBuilder();
		char input = 's';
		for (int i = 0; i < POINTER_SIZE; i++) {
			try {
				input = (char) raf.read();
			} catch (IOException e) {
				Logger.getInstance().log("Error BTF11: Could not read! #BlameBene");
				e.printStackTrace();
				return -2;
			}
			if (input == FILLER)
				break;
			sb.append(input);
		}
		if (sb.length() == 0) {
			try {
				raf.seek(position);
			} catch (IOException e) {
				Logger.getInstance().log("Error BTF12: Could not seek! #BlameBene");
				e.printStackTrace();
				return -2;
			}
			return -1;
		}
		System.out.println(sb.toString());
		return (int) Long.parseLong(sb.toString(), Character.MAX_RADIX);
	}

	/**
	 * Makes a user at the end of the file. Returns whter it succeeded or not!
	 */
	private boolean makeUser(int tag, String username) {
		try {
			raf.seek(raf.length());
		} catch (IOException e1) {
			Logger.getInstance().log("Error BTF4: Could not seek or get the Length! #BlameBene");
			e1.printStackTrace();
			return false;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(username);
		for (int i = 0; i < USERNAME_SIZE - username.length(); i++) {
			sb.append(FILLER);
		}
		String convertedTag = Integer.toString(tag, Character.MAX_RADIX);
		sb.append(convertedTag);
		for (int i = 0; i < POINTER_SIZE * 3 - convertedTag.length(); i++) {
			sb.append(FILLER);
		}
		for (int i = 0; i < sb.length(); i++) {
			try {
				raf.write(sb.charAt(i));
			} catch (IOException e) {
				Logger.getInstance().log("Error BTF3: Could not write to File! #BlameBene");
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets the tag of a user. Returns -1 if the user has not been found.
	 * Returns -2 if an error occured!
	 *
	 * @param username
	 * @return
	 */
	int getTag(String username) {
		if (tree.length() == 0)
			return -1;
		try {
			raf.seek(0);
		} catch (IOException e) {
			Logger.getInstance().log("Error BTF14: Could not seek! #BlameBene");
			e.printStackTrace();
			return -2;
		}
		int atPosition = 0;
		while (true) {
			try {
				raf.seek(atPosition * DATA_SIZE);
			} catch (IOException e1) {
				Logger.getInstance().log("Error BTF18: Could not seek! #BlameBene");
				e1.printStackTrace();
			}
			StringBuilder sb = new StringBuilder();
			char input = 's';
			try {
				input = (char) raf.read();
			} catch (IOException e) {
				Logger.getInstance().log("Error BTF15: Could not read! #BlameBene");
				e.printStackTrace();
				return -2;
			}
			for (int i = 0; i < USERNAME_SIZE - 1; i++) {
				if (input == FILLER)
					break;
				sb.append(input);
				try {
					input = (char) raf.read();
				} catch (IOException e) {
					System.err.println("Error BTF16: Could not read! #BlameBene");
					e.printStackTrace();
					return -2;
				}
			}
			int result = sb.toString().compareTo(username);
			if (result == 0) { // Get the tag
				try {
					raf.seek(atPosition * DATA_SIZE + USERNAME_SIZE);
				} catch (IOException e) {
					Logger.getInstance().log("Error BTF17: Could not seek! #BlameBene");
					e.printStackTrace();
					return -2;
				}
				sb = new StringBuilder();
				input = 's';
				try {
					input = (char) raf.read();
				} catch (IOException e) {
					Logger.getInstance().log("Error BTF15: Could not read! #BlameBene");
					e.printStackTrace();
					return -2;
				}
				for (int i = 0; i < POINTER_SIZE - 1; i++) {
					if (input == FILLER)
						break;
					sb.append(input);
					try {
						input = (char) raf.read();
					} catch (IOException e) {
						System.err.println("Error BTF16: Could not read! #BlameBene");
						e.printStackTrace();
						return -2;
					}
				}
				if (sb.length() == 0) {
					Logger.getInstance().log("Error BTF17: Tag was only Filler! #BlameBene");
					return -2;
				}
				return (int) Long.parseLong(sb.toString(), Character.MAX_RADIX);
			} else if (result < 0) { // For example res = A, username = Z
				atPosition = seekAt(atPosition * DATA_SIZE + USERNAME_SIZE + POINTER_SIZE * 1);
			} else { // For example res = Z, username = A
				atPosition = seekAt(atPosition * DATA_SIZE + USERNAME_SIZE + POINTER_SIZE * 2);
			}
			if (atPosition == -1 || atPosition == -2)
				return atPosition;
		}
	}

}