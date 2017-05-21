package dataManagement;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	private static Logger instance;
	private RandomAccessFile logWriter;
	private SimpleDateFormat dateFormat;

	public static Logger getInstance() {
		if (instance == null)
			instance = new Logger();
		return instance;
	}

	public void setFile(File f) {
		dateFormat = new SimpleDateFormat("dd-MM-YYYY_HH.mm.ss");
		try {
			logWriter = new RandomAccessFile(
					new File(f.getAbsolutePath() + "/" + dateFormat.format(new Date()) + ".txt"), "rw");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Logger() {
		// Just a private Constructor
	}

	/**
	 * Logs a String to the log file
	 */
	public synchronized void log(String toLog) {
		if (logWriter != null) {
			System.out.println("logging");
			try {
				logWriter.writeBytes(dateFormat.format(new Date()) + ": " + toLog + System.lineSeparator());
			} catch (IOException e) {
				System.err.println("Could not log! #BlameBene");
				e.printStackTrace();
			}
		}
	}

}
