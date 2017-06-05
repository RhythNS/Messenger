package userDataManagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import server.Constants;

public abstract class List {

	public enum Type {
		SOMEONE_REQUESTED_YOU(DataConstants.SOMEONE_REQUESTS_YOU), YOU_REQUESTED_SOMEONE(
				DataConstants.YOU_REQUEST_SOMEONE), MUTUALLY_ACCEPTED(
						DataConstants.MUTUALLY_ACCEPTED), BLOCKED(DataConstants.BLOCKED);
		char character;

		private Type(char character) {
			this.character = character;
		}

		public char getCharacter() {
			return character;
		}
	}

	private RandomAccessFile raf;
	private File list;

	public List(File saveDir, String name) {
		list = new File(saveDir, name + ".txt");
		try {
			raf = new RandomAccessFile(list, "rw");
		} catch (FileNotFoundException e) {
			System.err.println("Could not init RandomAccessFile! #BlameBene");
			new FileException(list);
			e.printStackTrace();
		}
	}

	/**
	 * Gets all tags from the list. Can return null
	 */
	ArrayList<Integer> getAll() {
		ArrayList<Integer> ints = new ArrayList<>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(list));
		} catch (FileNotFoundException e) {
			System.err.println("Could not init Reader! #BlameBene");
			new FileException(list);
			e.printStackTrace();
			return null;
		}
		try {
			String input = br.readLine();
			if (input == null) {
				br.close();
				return ints;
			}
			do {
				int tag = getTag(input);
				if (tag != -1)
					ints.add(tag);
				input = br.readLine();
			} while (input != null);
		} catch (IOException e) {
			System.err.println("Could not read or close the reader! #BlameBene");
			e.printStackTrace();
			try {
				br.close();
			} catch (IOException e1) {
				System.err.println("Could not close the reader! #BlameBene");
				e1.printStackTrace();
			}
			return null;
		}
		try {
			br.close();
		} catch (IOException e) {
			System.err.println("Could not close the reader! #BlameBene");
			e.printStackTrace();
		}
		return ints;
	}

	boolean isInList(int tag) {
		return getPosition(tag) != -1;
	}

	Type getRelationship(int tag) {
		String entry = getEntry(tag);
		if (entry == null) {
			System.err.println("Tag not found! #BlameBene");
			return null;
		}
		return getType(entry);
	}

	private String getEntry(int tag) {
		long pos = getPosition(tag);
		if (pos == -1)
			return null;
		try {
			raf.seek(pos);
		} catch (IOException e) {
			System.err.println("Could not seek! #BlameBene");
			e.printStackTrace();
			return null;
		}
		StringBuilder sb = new StringBuilder();
		char input = 's';
		try {
			for (int i = 0; i < DataConstants.POINTER_SIZE + 1; i++) {
				input = (char) raf.read();
				if (input == DataConstants.FILLER)
					break;
				sb.append(input);
			}
		} catch (IOException e) {
			System.err.println("Could not read! #BlameBene");
			e.printStackTrace();
			return null;
		}
		return sb.toString();
	}

	/**
	 * Deletes a tag from the list
	 *
	 * @param tag
	 *            The tag that should be deleted
	 * @return Wheter it successeded or not
	 */
	boolean delete(int tag) {
		long pos = getPosition(tag);
		if (pos == -1) {
			System.err.println("Tag was not found! #BlameBene");
			return false;
		}
		try {
			raf.seek(pos);
		} catch (IOException e) {
			System.err.println("Could not seek! #BlameBene");
			e.printStackTrace();
			return false;
		}
		for (int i = 0; i < Constants.POINTER_SIZE + 1; i++) {
			try {
				raf.write(Constants.FILLER);
			} catch (IOException e) {
				System.err.println("Could not write! #BlameBene");
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * Writes a tag and type to the list. If the tag is found it is simply
	 * overwritten!
	 *
	 * @param tag
	 *            The tag that should be added or modified!
	 * @param type
	 *            The Type of relationship that tag has
	 * @return Wheter it successeded or not
	 */
	boolean write(int tag, Type type) {
		long pos = getPosition(tag);
		if (pos == -1)
			try {
				pos = raf.length();
			} catch (IOException e) {
				System.err.println("Could not get the length of RandomAccessFile! #BlameBene");
				e.printStackTrace();
				return false;
			}
		try {
			raf.seek(pos);
		} catch (IOException e) {
			System.err.println("Could not seek! #BlameBene");
			e.printStackTrace();
			return false;
		}
		try {
			raf.write(type.character);
			String toWrite = Integer.toString(tag, Character.MAX_RADIX);
			for (int i = 0; i < toWrite.length(); i++) {
				raf.write(toWrite.charAt(i));
			}
			for (int i = 0; i < Constants.POINTER_SIZE - toWrite.length(); i++) {
				raf.write(Constants.FILLER);
			}
			raf.write('\n');
		} catch (IOException e) {
			System.err.println("Could not write! #BlameBene");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Checks if a tag is in the list. Can return -1 if that tag has not been
	 * found!
	 *
	 * @param tag
	 *            The requested tag
	 * @return The position the tag is in the list
	 */
	private long getPosition(int tag) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(list));
		} catch (FileNotFoundException e) {
			System.err.println("Could not init BufferedReader! #BlameBene");
			new FileException(list);
			e.printStackTrace();
			return -1;
		}
		long position = 0;
		try {
			String input = br.readLine();
			do {
				if (getTag(input) == tag) {
					br.close();
					return position;
				}
				position += input.length() + 1; // The \n Character
				input = br.readLine();
			} while (input != null);
		} catch (IOException e) {
			System.err.println("Could not readLine or close! #BlameBene");
			e.printStackTrace();
			try {
				br.close();
			} catch (IOException e1) {
				System.err.println("Could not close reader! #BlameBene");
				e1.printStackTrace();
			}
			return -1;
		}
		try {
			br.close();
		} catch (IOException e) {
			System.err.println("Could not close reader! #BlameBene");
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Gets the type of an input. Can return null if something went wrong!
	 *
	 * @param input
	 *            The read input!
	 * @return The type or null!
	 */
	private Type getType(String input) {
		if (input == null || input.length() == 0) {
			System.err.println("Input was null or had a length of 0! #BlameBene");
			return null;
		}
		char c = input.charAt(0);
		Type[] types = Type.values();
		for (int i = 0; i < types.length; i++) {
			if (c == types[i].character)
				return types[i];
		}
		System.err.println("Type not found! #BlameBene");
		return null;
	}

	/**
	 * Gets the tag of an input. Can return -1 if something went wrong or the
	 * tag was deleted!
	 *
	 * @param input
	 *            The read input
	 * @return the tag or -1
	 */
	private int getTag(String input) {
		if (input == null || input.length() < 2) {
			System.err.println("Input was null or had a length of 0! #BlameBene");
			return -1;
		}
		if (input.charAt(2) == Constants.FILLER)
			return -1;
		int to = input.indexOf(Constants.FILLER);
		if (to == -1)
			to = input.length();
		return Integer.parseInt(input.substring(1, to + 1), Character.MAX_RADIX);
	}

}
