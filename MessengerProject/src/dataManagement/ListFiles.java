package dataManagement;

import server.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ListFiles {

	RandomAccessFile raf;
	final int BYTES_PER_USER, BYTES_PER_VALUE, AMOUNT_OF_VALUES;

	ListFiles(int amount, int length, File file) {
		BYTES_PER_VALUE = length;
		AMOUNT_OF_VALUES = amount;
		BYTES_PER_USER = length * amount;
		try {
			raf = new RandomAccessFile(file, "rw");
		} catch (FileNotFoundException e) {
			Logger.getInstance().log("Error LF0: Could not init RandomAccessFile! #BlameBene");
			e.printStackTrace();
			new FileException(file);
		}
	}

	boolean make(int tag) {
		long length;
		try {
			raf.seek(length = raf.length());
		} catch (IOException e) {
			Logger.getInstance().log("Error LF1: Could not seek or get the file length! #BlameBene");
			e.printStackTrace();
			return false;
		}
		long toMake = tag * BYTES_PER_USER;
		if (toMake < length) {
			return true;
		}
		if (toMake - BYTES_PER_USER - 1 > length)
			Logger.getInstance().log(
					"Notice LF0: Making more tags than is should be! (" + length / BYTES_PER_USER + ") #BlameBene");
		for (long i = length; i < toMake; i++) {
			try {
				raf.write(Constants.FILLER);
			} catch (IOException e) {
				Logger.getInstance().log("Error LF3: Could not write! #BlameBene");
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	boolean tagRegisterd(int tag) {
		try {
			return raf.length() > tag * BYTES_PER_USER;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	String get(int tag, int number) {
		try {
			raf.seek(BYTES_PER_USER * (tag - 1) + number * BYTES_PER_VALUE);
		} catch (IOException e) {
			Logger.getInstance().log("Error LF4: Could not seek! #BlameBene");
			e.printStackTrace();
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < BYTES_PER_VALUE; i++) {
			try {
				char input = (char) raf.read();
				if (input == Constants.FILLER)
					break;
				sb.append(input);
			} catch (IOException e) {
				Logger.getInstance().log("Error LF5: Could not read! #BlameBene");
				e.printStackTrace();
				return null;
			}
		}
		return sb.toString();
	}

	String[] getAll(int tag) {
		String[] strings = new String[AMOUNT_OF_VALUES];
		for (int i = 0; i < AMOUNT_OF_VALUES; i++) {
			strings[i] = get(tag, i);
		}
		return strings;
	}

	boolean set(int tag, int number, String list) {
		if (list.length() > BYTES_PER_VALUE) {
			Logger.getInstance().log("Error LF7: Given String is longer than it can be! #BlameBene");
			return false;
		}
		try {
			raf.seek(BYTES_PER_USER * (tag - 1) + number * BYTES_PER_VALUE);
		} catch (IOException e) {
			Logger.getInstance().log("Error LF6: Could not seek! #BlameBene");
			e.printStackTrace();
			return false;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(list);
		for (int i = list.length(); i < BYTES_PER_VALUE; i++) {
			sb.append(Constants.FILLER);
		}
		for (int i = 0; i < sb.length(); i++) {
			try {
				raf.write(sb.charAt(i));
			} catch (IOException e) {
				Logger.getInstance().log("Error LF8: Could not write! #BlameBene");
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	int find(int tag, String toFind) {
		for (int i = 0; i < AMOUNT_OF_VALUES; i++) {
			try {
				raf.seek(tag * BYTES_PER_USER + i * BYTES_PER_VALUE);
			} catch (IOException e) {
				Logger.getInstance().log("Error LF11: Could not seek #BlameBene");
				e.printStackTrace();
				return -1;
			}
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < BYTES_PER_VALUE; j++) {
				try {
					char input = (char) raf.read();
					if (input == Constants.FILLER)
						break;
					sb.append(input);
				} catch (IOException e) {
					Logger.getInstance().log("Error LF12: Could not read! #BlameBene");
					e.printStackTrace();
					return -1;
				}
			}
			if (sb.length() != 0 && sb.toString().equals(toFind))
				return i;
		}
		return -1;
	}

	boolean deleteAll(int tag) {
		try {
			raf.seek(tag * BYTES_PER_USER);
		} catch (IOException e) {
			Logger.getInstance().log("Error LF13: Could not seek! #BlameBene");
			e.printStackTrace();
			return false;
		}
		for (int i = 0; i < AMOUNT_OF_VALUES; i++) {
			for (int j = 0; j < BYTES_PER_VALUE; j++) {
				try {
					raf.write(Constants.FILLER);
				} catch (IOException e) {
					Logger.getInstance().log("Error LF14: COuld not write! #BlameBene");
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}

	int getFirstNull(int tag) {
		try {
			for (int i = 0; i < raf.length() / BYTES_PER_USER; i++) {
                try {
                    raf.seek((tag - 1) * BYTES_PER_USER + i * BYTES_PER_VALUE);
                } catch (IOException e) {
                    Logger.getInstance().log("Error LF9: Could not seek #BlameBene");
                    e.printStackTrace();
                    return -1;
                }
                try {
                    if (raf.read() == Constants.FILLER)
                        return i;
                } catch (IOException e) {
                    Logger.getInstance().log("Error Lf10: Could not read! #BlameBene");
                    e.printStackTrace();
                    return -1;
                }
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	boolean setAdmin(int tag, int tagToPromote) {
		String tagS = Integer.toString(tagToPromote, Character.MAX_RADIX);
		String prevAdmin = get(tag, 0);
		int pointer = find(tag, tagS);
		if (pointer == -1)
			pointer = getFirstNull(tag);
		return set(tag, 0, tagS) && set(tag, pointer, prevAdmin);
	}


}
