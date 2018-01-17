package dataManagement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import server.Constants;

public class Index {

	private File dir, indexFile;
	private final Object lock = new Object();
	private MessageIO messageIO;
	private FileManager fileManager;
	private String date;

	Index(String date, File saveDir) {
		this.date = date;
		dir = new File(saveDir, date);
		dir.mkdirs();

		indexFile = new File(dir, "index.txt");
		try {
			indexFile.createNewFile();
		} catch (IOException e) {
			new FileException(indexFile);
			Logger.getInstance().log("Error I17: Could not make File! #BlameBene");
			e.printStackTrace();
		}

		messageIO = new MessageIO(new File(dir, "randomAccessFile.txt"));

		File fileManagerFile = new File(dir, "files");
		fileManagerFile.mkdirs();
		fileManager = new FileManager(fileManagerFile);
	}

	Mailbox readAll(String date, int tag, int[] groupTags) {
		BufferedReader br = null;
		Mailbox mb = new Mailbox();
		try {
			br = new BufferedReader(new FileReader(indexFile));
		} catch (FileNotFoundException e1) {
			Logger.getInstance().log("Error I11: Could not init the Reader! #BlameBene");
			e1.printStackTrace();
			return mb;
		}
		try {
			String s = br.readLine();
			String seperator = Constants.SEPERATOR + "";
			while (s != null) {
				String[] arr = s.split(seperator);
				int tag1 = 0, tag2 = 0;
				if (DateCalc.forDayIsBelow(date, arr[0])) {
					boolean found = (tag1 = Integer.parseInt(arr[1], Character.MAX_RADIX)) == tag
							|| (tag2 = Integer.parseInt(arr[2], Character.MAX_RADIX)) == tag;
					for (int i = 0; i < groupTags.length; i++) {
						if (found)
							break;
						found = tag2 == groupTags[i];
					}
					if (found) {
						if (arr.length == 4) {
							Message fm = new Message(this.date + arr[0], tag1, tag2);
							fm.setContent(fileManager.getFile(arr[3]));
							if (fm != null)
								mb.files.add(fm);
						} else {
							Message tm = new Message(this.date + arr[0], tag1, tag2);
							tm.pointerFrom = Long.parseLong(arr[3], Character.MAX_RADIX);
							tm.pointerTo = Long.parseLong(arr[4], Character.MAX_RADIX);
							tm = messageIO.read(tm);
							if (tm != null)
								mb.messages.add(tm);
						}
					}
				}
				s = br.readLine();
			}
		} catch (IOException e) {
			Logger.getInstance().log("Error I12: Could not read! #BlameBene");
			e.printStackTrace();
			try {
				br.close();
			} catch (IOException e1) {
				Logger.getInstance().log("Error I13: Could not close the reader! #BlameBene");
				e1.printStackTrace();
			}
			return mb;
		}
		try {
			br.close();
		} catch (IOException e) {
			Logger.getInstance().log("Error I14: Could not close the reader! #BlameBene");
			e.printStackTrace();
		}
		return mb;
	}

	boolean write(int fromTag, int toTag, String message, String date) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(indexFile, true));
		} catch (IOException e) {
			Logger.getInstance().log("Error I0: Could not init bufferdWriter! #BlameBene");
			e.printStackTrace();
			return false;
		}
		long[] values = messageIO.write(message);
		if (values == null) {
			Logger.getInstance().log("Error I1: Values are null! #BlameBene");
			try {
				bw.close();
			} catch (IOException e) {
				Logger.getInstance().log("Error I4: Could not close the writer! #BlameBene");
				e.printStackTrace();
			}
			return false;
		}
		try {
			bw.write(date + Constants.SEPERATOR + Integer.toString(fromTag, Character.MAX_RADIX) + Constants.SEPERATOR
					+ Integer.toString(toTag, Character.MAX_RADIX) + Constants.SEPERATOR
					+ Long.toString(values[0], Character.MAX_RADIX) + Constants.SEPERATOR
					+ Long.toString(values[1], Character.MAX_RADIX) + "\n");
		} catch (IOException e) {
			Logger.getInstance().log("Error I2: Can not write! #BlameBene");
			e.printStackTrace();
			try {
				bw.close();
			} catch (IOException e1) {
				Logger.getInstance().log("Error I10: Could not close the writer! #BlameBene");
				e1.printStackTrace();
				return false;
			}
			return false;
		}
		try {
			bw.close();
		} catch (IOException e) {
			Logger.getInstance().log("Error I3: Could not close the writer! #BlameBene");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	boolean saveFile(int fromTag, int toTag, String message, String date) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(indexFile, true));
		} catch (IOException e) {
			Logger.getInstance().log("Error I5: Could not init writer! #BlameBene");
			e.printStackTrace();
			return false;
		}
		String fileName = fileManager.getFileName();
		if (fileName != null) {
			Logger.getInstance().log("Error I8: Filename is null! #BlameBene");
			try {
				bw.close();
			} catch (IOException e) {
				Logger.getInstance().log("Error I9: Could not close the writer! #BlameBene");
				e.printStackTrace();
			}
			return false;
		}
		try {
			bw.write(date + Constants.SEPERATOR + Integer.toString(fromTag, Character.MAX_RADIX) + Constants.SEPERATOR
					+ Integer.toString(toTag, Character.MAX_RADIX) + Constants.SEPERATOR + fileName);
		} catch (IOException e) {
			Logger.getInstance().log("Error I6: Could not write! #BlameBene");
			e.printStackTrace();
		}
		try {
			bw.close();
		} catch (IOException e) {
			Logger.getInstance().log("Error I7: Could not close the Writer! #BlameBene");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	boolean kill() {
		return messageIO.close() ? delete(dir) : false;
	}

	private boolean delete(File file) {
		if (!file.exists()) {
			Logger.getInstance().log("Error I15: File not found for deletion! #BlameBene");
			return false;
		}
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				delete(files[i]);
			}
		}
		if (!file.delete()) {
			Logger.getInstance().log("Error I16: Could not delete! #BlameBene");
			return false;
		}
		return true;
	}

	String getDate() {
		return date;
	}

	Object getLock() {
		return lock;
	}

	@Override
	public String toString() {
		return "Index: " + getDate();
	}

}
