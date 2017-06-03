package dataManagement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import server.Constants;

public class BinaryTreeFile {

	private RandomAccessFile raf;
	private final int NAME_SIZE, POINTER_SIZE, DATA_SIZE;
	private File tree;

	BinaryTreeFile(File tree, int nameSize) {
		NAME_SIZE = nameSize;
		POINTER_SIZE = Constants.POINTER_SIZE;
		DATA_SIZE = NAME_SIZE + POINTER_SIZE * 3;

		this.tree = tree;

		if (!tree.exists())
			Logger.getInstance().log("Notice BTF0: BinaryTreeFile does not exists, making one!");

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
				String s = getTagAtPosition(atPosition);
				if (s == null || s.length() == 0) {
					Logger.getInstance().log("Error BT24: String was null or a length of 0! #BlameBene");
					return false;
				}
				if (s.charAt(0) == Constants.DELETE_SYMBOL) {
					try {
						raf.seek(atPosition * DATA_SIZE + NAME_SIZE);
					} catch (IOException e) {
						Logger.getInstance().log("Error BTF21: Could not seek! #BlameBene");
						e.printStackTrace();
						return false;
					}
					String toWrite = Integer.toString(tag, Character.MAX_RADIX);
					for (int i = 0; i < toWrite.length(); i++) {
						try {
							raf.write(toWrite.charAt(i));
						} catch (IOException e) {
							Logger.getInstance().log("Error BTF22: Could not write #BlameBene");
							e.printStackTrace();
							return false;
						}
					}
					for (int i = toWrite.length(); i < POINTER_SIZE; i++) {
						try {
							raf.write(Constants.FILLER);
						} catch (IOException e) {
							Logger.getInstance().log("Error BTF23: Could not write! #BlameBene");
							e.printStackTrace();
							return false;
						}
					}
					return true;
				}
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

	boolean delete(String name) {
		int tag = getTag(name);
		if (tag != -1 && tag != -2) {
			try {
				raf.write(Constants.DELETE_SYMBOL);
				for (int i = 1; i < POINTER_SIZE; i++)
					raf.write(Constants.FILLER);
			} catch (IOException e) {
				Logger.getInstance().log("Error BTF26: Could not write #BlameBene");
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets the tag of a user. Returns -1 if the user has not been found.
	 * Returns -2 if an error occured! If successeded the pointer is placed at
	 * the tag!
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
				String res = getTagAtPosition(atPosition);
				try {
					raf.seek(atPosition * DATA_SIZE + NAME_SIZE);
				} catch (IOException e) {
					Logger.getInstance().log("Error BTF26: Could not seek! #BlameBene");
					e.printStackTrace();
					return -2;
				}
				if (res == null || res.length() == 0)
					return -2;
				if (res.charAt(0) == Constants.DELETE_SYMBOL)
					return -1;
				return (int) Long.parseLong(res, Character.MAX_RADIX);
			} else if (result < 0) { // For example res = A, name = Z
				atPosition = seekAt(atPosition * DATA_SIZE + NAME_SIZE + POINTER_SIZE * 1);
			} else { // For example res = Z, name = A
				atPosition = seekAt(atPosition * DATA_SIZE + NAME_SIZE + POINTER_SIZE * 2);
			}
			if (atPosition == -1 || atPosition == -2)
				return atPosition;
		}
	}

	private String getTagAtPosition(int atPosition) {
		try {
			raf.seek(atPosition * DATA_SIZE + NAME_SIZE);
		} catch (IOException e) {
			Logger.getInstance().log("Error BTF17: Could not seek! #BlameBene");
			e.printStackTrace();
			return null;
		}
		StringBuilder sb = new StringBuilder();
		char input = 's';
		try {
			input = (char) raf.read();
		} catch (IOException e) {
			Logger.getInstance().log("Error BTF15: Could not read! #BlameBene");
			e.printStackTrace();
			return null;
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
				return null;
			}
		}
		if (sb.length() == 0) {
			Logger.getInstance().log("Error BTF17: Tag was only Filler! #BlameBene");
			return null;
		}
		return sb.toString();
	}

	private String getNameAtPosition(int atPosition) {
		try {
			raf.seek(atPosition * DATA_SIZE);
		} catch (IOException e) {
			Logger.getInstance().log("Error BTF25: Could not seek! #BlameBene");
			e.printStackTrace();
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < NAME_SIZE; i++) {
			try {
				char input = (char) raf.read();
				if (input == Constants.FILLER)
					break;
				sb.append(input);
			} catch (IOException e) {
				Logger.getInstance().log("Error BTF26: Could not read! #BlameBene");
				e.printStackTrace();
				return null;
			}
		}
		return sb.toString();
	}

	BinaryTreeFile refresh() {
		try {
			raf.close();
		} catch (IOException e) {
			Logger.getInstance().log("Error BTF20: Could not close the RandomAccessFile! #BlameBene");
			e.printStackTrace();
			return this;
		}
		String name = tree.getName();
		File backup = new File(tree.getParentFile(), "backup" + name);
		if (backup.exists())
			backup.delete();
		tree.renameTo(backup);
		tree = new File(tree.getParentFile(), name);
		try {
			raf = new RandomAccessFile(backup, "rw");
		} catch (FileNotFoundException e) {
			Logger.getInstance().log("Error BTF24: Could not init RandomAccessFile #BlameBene");
			e.printStackTrace();
			return this;
		}
		BinaryTreeFile newTree = new BinaryTreeFile(new File(tree.getParentFile(), name), NAME_SIZE);
		try {
			for (int i = 0; i < raf.length() / DATA_SIZE; i++) {
				String tag = getTagAtPosition(i);
				if (tag != null && tag.length() != 0 && tag.charAt(0) != Constants.DELETE_SYMBOL) {
					String nameTag = getNameAtPosition(i);
					if (nameTag != null && nameTag.length() != 0)
						newTree.add(Integer.parseInt(tag, Character.MAX_RADIX), nameTag);
				}
			}
		} catch (IOException e) {
			Logger.getInstance().log("Error BTF25: Could not get the file length! #BlameBene");
			e.printStackTrace();
			return this;
		}
		return newTree;
	}

}