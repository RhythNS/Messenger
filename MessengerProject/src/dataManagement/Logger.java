package dataManagement;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	private static Logger instance;
	private RandomAccessFile logWriter;

	public static Logger getInstance() {
		if (instance == null)
			instance = new Logger();
		return instance;
	}

	/**
	 * This needs to be called for the Logger to initlize. The file should be
	 * the root folder of the Messenger file system.
	 */
	public void setFile(File f) {
		if (f == null) {
			System.err.println("File in Logger is null! #BlameBene");
			return;
		}
		try {
			logWriter = new RandomAccessFile(new File(f.getAbsolutePath() + "/" + DateCalc.getLoggerDate() + ".txt"),
					"rw");
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
			try {
				logWriter.writeBytes(DateCalc.getLoggerDate() + ": " + toLog + System.lineSeparator());
			} catch (IOException e) {
				System.err.println("Could not log! #BlameBene");
				e.printStackTrace();
			}
		}
	}

}
