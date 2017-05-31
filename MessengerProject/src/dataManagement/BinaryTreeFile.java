package dataManagement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import server.Constants;

public class BinaryTreeFile {

	private RandomAccessFile raf;
	private final int NAME_SIZE, POINTER_SIZE = 6, DATA_SIZE;

	BinaryTreeFile(File tree, int nameSize) {
		NAME_SIZE = nameSize;
		DATA_SIZE = NAME_SIZE + POINTER_SIZE * 3;

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
	 * Adds a new name to the file. Returns wheter it succeeded or not!
	 */
	boolean add(int tag, String name) {
		if (name.length() > NAME_SIZE) {
			Logger.getInstance().log("Error BTF2: Can not save that name. It is too long! #BlameBene");
			return false;
		}
		try {
			if (raf.length() == 0) {
				return make(tag, name);
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
				for (int i = 0; i < NAME_SIZE - 1; i++) {
					input = (char) raf.read();
					if (input == Constants.FILLER)
						break;
					sb.append(input);
				}
			} catch (IOException e) {
				Logger.getInstance().log("Error BTF6: Could not get the FilePointer or read! #BlameBene");
				e.printStackTrace();
				return false;
			}
			int result = sb.toString().compareTo(name);
			if (result == 0) {
				Logger.getInstance().log("Error BTF7: The name is already registerd! #BlameBene");
				return false;
			} else if (result < 0) { // For example res = A, name = Z
				atPosition = seekAt((atPosition * DATA_SIZE) + NAME_SIZE + POINTER_SIZE);
			} else { // For example res = Z, name = A
				atPosition = seekAt((atPosition * DATA_SIZE) + NAME_SIZE + (POINTER_SIZE * 2));
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
				return make(tag, name);
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
			if (input == Constants.FILLER)
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
		return (int) Long.parseLong(sb.toString(), Character.MAX_RADIX);
	}

	/**
	 * Makes a user at the end of the file. Returns whter it succeeded or not!
	 */
	private boolean make(int tag, String name) {
		try {
			raf.seek(raf.length());
		} catch (IOException e1) {
			Logger.getInstance().log("Error BTF4: Could not seek or get the Length! #BlameBene");
			e1.printStackTrace();
			return false;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		for (int i = 0; i < NAME_SIZE - name.length(); i++) {
			sb.append(Constants.FILLER);
		}
		String convertedTag = Integer.toString(tag, Character.MAX_RADIX);
		sb.append(convertedTag);
		for (int i = 0; i < POINTER_SIZE * 3 - convertedTag.length(); i++) {
			sb.append(Constants.FILLER);
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
	 * @param name
	 * @return
	 */
	int getTag(String name) {
		try {
			if (raf.length() == 0)
				return -1;
		} catch (IOException e2) {
			Logger.getInstance().log("Error BTF19: Could not get the length! #BlameBene");
			e2.printStackTrace();
			return -1;
		}
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
			for (int i = 0; i < NAME_SIZE - 1; i++) {
				if (input == Constants.FILLER)
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
			int result = sb.toString().compareTo(name);
			if (result == 0) { // Get the tag
				try {
					raf.seek(atPosition * DATA_SIZE + NAME_SIZE);
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
					if (input == Constants.FILLER)
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
			} else if (result < 0) { // For example res = A, name = Z
				atPosition = seekAt(atPosition * DATA_SIZE + NAME_SIZE + POINTER_SIZE * 1);
			} else { // For example res = Z, name = A
				atPosition = seekAt(atPosition * DATA_SIZE + NAME_SIZE + POINTER_SIZE * 2);
			}
			if (atPosition == -1 || atPosition == -2)
				return atPosition;
		}
	}

}