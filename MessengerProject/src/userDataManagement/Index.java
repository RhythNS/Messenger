package userDataManagement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
			System.err.println("Could not make File! #BlameBene");
			e.printStackTrace();
		}

		messageIO = new MessageIO(new File(dir, "randomAccessFile.txt"));

		File fileManagerFile = new File(dir, "files");
		fileManagerFile.mkdirs();
		fileManager = new FileManager(fileManagerFile);
	}

	Mailbox readAll(int tag) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(indexFile));
		} catch (FileNotFoundException e1) {
			System.err.println("Could not init the Reader! #BlameBene");
			e1.printStackTrace();
			return null;
		}
		Mailbox mb = new Mailbox();
		boolean isGroup = tag < 0;
		try {
			String s = br.readLine();
			String seperator = DataConstants.SEPERATOR + "";
			while (s != null) {
				String[] arr = s.split(seperator);
				int tag1 = 0, tag2 = 0;
				tag1 = Integer.parseInt(arr[1], Character.MAX_RADIX);
				tag2 = Integer.parseInt(arr[2], Character.MAX_RADIX);
				if (isGroup && tag2 == tag || (!isGroup && ((tag1 == tag && tag2 > 0) || (tag2 == tag && tag1 > 0)))) {
					if (arr.length == 4) {
						Message fm = new Message(this.date + arr[0], tag1, tag2);
//						fm.setContent(fileManager.getFile(arr[3]));
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
				s = br.readLine();
			}
		} catch (IOException e) {
			System.err.println("Could not read! #BlameBene");
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
		return mb;
	}

	boolean write(int fromTag, int toTag, String message, String date) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(indexFile, true));
		} catch (IOException e) {
			System.err.println("Could not init bufferdWriter! #BlameBene");
			e.printStackTrace();
			return false;
		}
		long[] values = messageIO.write(message);
		if (values == null) {
			System.err.println("Values are null! #BlameBene");
			try {
				bw.close();
			} catch (IOException e) {
				System.err.println("Could not close the writer! #BlameBene");
				e.printStackTrace();
			}
			return false;
		}
		try {
			bw.write(date + DataConstants.SEPERATOR + Integer.toString(fromTag, Character.MAX_RADIX)
					+ DataConstants.SEPERATOR + Integer.toString(toTag, Character.MAX_RADIX) + DataConstants.SEPERATOR
					+ Long.toString(values[0], Character.MAX_RADIX) + DataConstants.SEPERATOR
					+ Long.toString(values[1], Character.MAX_RADIX) + "\n");
		} catch (IOException e) {
			System.err.println("Can not write! #BlameBene");
			e.printStackTrace();
			try {
				bw.close();
			} catch (IOException e1) {
				System.err.println("Could not close the writer! #BlameBene");
				e1.printStackTrace();
				return false;
			}
			return false;
		}
		try {
			bw.close();
		} catch (IOException e) {
			System.err.println("Could not close the writer! #BlameBene");
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
			System.err.println("Could not init writer! #BlameBene");
			e.printStackTrace();
			return false;
		}
		String fileName = fileManager.getFileName();
		if (fileName != null) {
			System.err.println("Filename is null! #BlameBene");
			try {
				bw.close();
			} catch (IOException e) {
				System.err.println("Could not close the writer! #BlameBene");
				e.printStackTrace();
			}
			return false;
		}
		try {
			bw.write(date + DataConstants.SEPERATOR + Integer.toString(fromTag, Character.MAX_RADIX)
					+ DataConstants.SEPERATOR + Integer.toString(toTag, Character.MAX_RADIX) + DataConstants.SEPERATOR
					+ fileName);
		} catch (IOException e) {
			System.err.println("Could not write! #BlameBene");
			e.printStackTrace();
		}
		try {
			bw.close();
		} catch (IOException e) {
			System.err.println("Could not close the Writer! #BlameBene");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	boolean close() {
		return messageIO.close();
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
