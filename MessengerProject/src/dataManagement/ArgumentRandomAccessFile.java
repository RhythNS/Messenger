package dataManagement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import server.Constants;

public class ArgumentRandomAccessFile {

	private RandomAccessFile raf, traf;
	private final long BYTES_PER_USER;
	private final int[] arguments;

	ArgumentRandomAccessFile(File file, int... argumentsLength) {
		int bytesPerUser = 0;
		for (int i = 0; i < argumentsLength.length; i++) {
			bytesPerUser += argumentsLength[i];
		}
		BYTES_PER_USER = bytesPerUser;
		arguments = argumentsLength;
		try {
			raf = new RandomAccessFile(file, "rw");
		} catch (FileNotFoundException e) {
			Logger.getInstance().log("Error U0: Could not init the RandomAccessFile! #BlameBene");
			new FileException(file);
		}
		File trafFile = new File(file.getParentFile(), "deletedTags.txt");
		try {
			traf = new RandomAccessFile(trafFile, "rw");
		} catch (FileNotFoundException e) {
			Logger.getInstance().log("Error U12: Could not init the Traf! #BlameBene");
			e.printStackTrace();
			new FileException(trafFile);
		}
	}

	boolean isArgumentCorrect(int tag, int argumentNumber, String argument) {
		if (argument == null) {
			Logger.getInstance().log("Error U8: Invalid Parameters! #BlameBene");
			return false;
		}
		String result = getArgument(tag, argumentNumber);
		return result != null ? result.equals(argument) : false;
	}

	String getArgument(int tag, int argumentNumber) {
		if (tag < 1 || argumentNumber < 0 || argumentNumber > arguments.length - 1) {
			Logger.getInstance().log("Error U1: Invalid Parameters! #BlameBene");
			return null;
		}
		tag--;
		int start = 0;
		for (int i = 0; i < argumentNumber; i++) {
			start += arguments[i];
		}
		try {
			raf.seek(tag * BYTES_PER_USER + start);
		} catch (IOException e) {
			Logger.getInstance().log("Error U1: Could not seek! #BlameBene");
			e.printStackTrace();
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arguments[argumentNumber]; i++) {
			try {
				char input = (char) raf.read();
				if (input == Constants.FILLER)
					break;
				sb.append(input);
			} catch (IOException e) {
				Logger.getInstance().log("Error U2: Could not read! #BlameBene");
				e.printStackTrace();
				return null;
			}
		}
		if (sb.length() == 0) {
			Logger.getInstance().log("Error U3: StringBuilders length is 0! #BlameBene");
			return null;
		}
		return sb.toString();
	}

	int add(String... givenArguments) {
		if (givenArguments == null || givenArguments.length != arguments.length) {
			Logger.getInstance()
					.log("Error U6: GivenArguments does not have the length of arguments or is null! #BlameBene");
			return 0;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arguments.length; i++) {
			if (givenArguments[i] == null || givenArguments[i].length() > arguments[i]
					|| givenArguments[i].length() == 0) {
				Logger.getInstance()
						.log("Error U4: Argument " + i + " is too long, not long enough or null! #BlameBene");
				return 0;
			}
			sb.append(givenArguments[i]);
			for (int j = givenArguments[i].length(); j < arguments[i]; j++) {
				sb.append(Constants.FILLER);
			}
		}
		int tag = 0;
		try {
			if (traf.length() == 0) {
				try {
					raf.seek(raf.length());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				StringBuilder sb2 = new StringBuilder();
				long i;
				for(i = traf.length() - 2; i > -1; i--) {
					traf.seek(i);
					char input = (char) traf.read();
					if(input == Constants.SEPERATOR)
						break;
					sb2.append(input);
				}
				tag = Integer.parseInt(sb2.reverse().toString(), Character.MAX_RADIX);
				raf.seek(tag * BYTES_PER_USER);
				if (i == 0)
					i--;
				traf.setLength(i + 1);
			}
		} catch (IOException e1) {
			Logger.getInstance().log("Error U15: Could not get traf length, setsize or read! #BlameBene");
			e1.printStackTrace();
		}
		for (int i = 0; i < sb.length(); i++) {
			try {
				raf.write(sb.charAt(i));
			} catch (IOException e) {
				Logger.getInstance().log("Error U5: Could not write! #BlameBene");
				e.printStackTrace();
				return 0;
			}
		}
		try {
			return tag == 0 ? (int) (raf.length() / BYTES_PER_USER) : tag;
		} catch (IOException e) {
			Logger.getInstance().log("Error U7: Could not get length! #BlameBene");
			e.printStackTrace();
			return 0;
		}
	}

	boolean remove(int tag) {
		try {
			traf.seek(traf.length());
		} catch (IOException e1) {
			Logger.getInstance().log("Error U13: Could not seek! #BlameBene");
			e1.printStackTrace();
			return false;
		}
		String sTag = Integer.toString(tag, Character.MAX_RADIX);
		try {
			for (int i = 0; i < sTag.length(); i++) {
				traf.write(sTag.charAt(i));
			}
			traf.write(Constants.SEPERATOR);
		} catch (IOException e) {
			Logger.getInstance().log("Error U14: Could not write! #BlameBene");
			e.printStackTrace();
		}
		tag--;
		try {
			raf.seek(BYTES_PER_USER * tag);
		} catch (IOException e) {
			Logger.getInstance().log("Error U8: Could not seek! #BlameBene");
			e.printStackTrace();
			return false;
		}
		for (int i = 0; i < BYTES_PER_USER; i++) {
			try {
				raf.write(Constants.FILLER);
			} catch (IOException e) {
				Logger.getInstance().log("Error U11: Could not write! #BlameBene");
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	boolean exists(int tag) {
		tag--;
		try {
			raf.seek(BYTES_PER_USER * tag);
		} catch (IOException e) {
			Logger.getInstance().log("Error U9: Could not seek! #BlameBene");
			e.printStackTrace();
			return false;
		}
		try {
			return raf.read() != Constants.FILLER;
		} catch (IOException e) {
			Logger.getInstance().log("Error U10: Could not read! #BlameBene");
			e.printStackTrace();
			return false;
		}
	}
}