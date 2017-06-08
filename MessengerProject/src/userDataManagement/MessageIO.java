package userDataManagement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import user.Message;

public class MessageIO {

	private RandomAccessFile raf;

	public MessageIO(File f) {
		try {
			raf = new RandomAccessFile(f, "rw");
		} catch (FileNotFoundException e) {
			System.err.println("Could not init RandomAccessFile! #BlameBene");
			new FileException(f);
			e.printStackTrace();
		}
		if (f.length() > 0) {
			System.err
					.println("File " + f.getName() + " already has data in it. Writing to the end of it!");
			try {
				raf.seek(raf.length());
			} catch (IOException e) {
				System.err.println("Could not seek or get the file length! #BlameBene");
				e.printStackTrace();
			}
		}
	}

	long[] write(String message) {
		long pointerFrom;
		try {
			pointerFrom = raf.length();
		} catch (IOException e1) {
			System.err.println("Could not get the file length! #BlameBene");
			e1.printStackTrace();
			return null;
		}
		try {
			raf.seek(pointerFrom);
		} catch (IOException e) {
			System.err.println("Could not seek! #BlameBene");
			e.printStackTrace();
			return null;
		}
		for (int i = 0; i < message.length(); i++) {
			try {
				raf.write(message.charAt(i));
			} catch (IOException e) {
				System.err.println("Could not write! #BlameBene");
				e.printStackTrace();
				return null;
			}
		}
		try {
			long[] ret = { pointerFrom, raf.length() - 1 };
			return ret;
		} catch (IOException e) {
			System.err.println("Could not get the file length! #BlameBene");
			e.printStackTrace();
			return null;
		}
	}

	Message read(Message msg) {
		try {
			raf.seek(msg.getPointerFrom());
		} catch (IOException e) {
			System.err.println("Could not seek! #BlameBene");
			e.printStackTrace();
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (long i = msg.getPointerFrom(); i < msg.getPointerTo() + 1; i++) {
			try {
				sb.append((char) raf.read());
			} catch (IOException e) {
				System.err.println("Could not read! #BlameBene");
				e.printStackTrace();
				return null;
			}
		}
		msg.setMessageContent(sb.toString());
		return msg;
	}

	boolean close() {
		try {
			raf.close();
		} catch (IOException e) {
			System.err.println("Could not close! #BlameBene");
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
