package dataManagement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

	private static Logger instance;
	private BufferedWriter logWriter;

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
			logWriter = new BufferedWriter(new FileWriter(new File(f, DateCalc.getLoggerDate() + ".txt")));
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
		System.out.println(toLog);
		if (logWriter != null) {
			try {
				logWriter.write(DateCalc.getLoggerDate() + ": " + toLog + System.lineSeparator());
			} catch (IOException e) {
				System.err.println("Could not log! #BlameBene");
				e.printStackTrace();
			}
			try {
				logWriter.flush();
			} catch (IOException e) {
				System.err.println("Could not flush! #BlameBene");
				e.printStackTrace();
			}
		}
	}

}
