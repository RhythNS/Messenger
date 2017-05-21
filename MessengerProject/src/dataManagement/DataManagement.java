package dataManagement;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class DataManagement {

	private Users users;
	private final Object usersLock = new Object();
	private SimpleDateFormat dateFormat;

	/**
	 * Standard Constructor for DataManagement
	 *
	 * @param saveDirectory
	 *            - The Directory in which everything will be saved
	 */
	public DataManagement(File saveDirectory) {
		if (saveDirectory == null) {
			saveDirectory = new File(System.getProperty("user.dir") + "/MessengerSaves");
			saveDirectory.mkdir();
		}
		if (!saveDirectory.isDirectory())
			new FileException(saveDirectory);

		File logDir = new File(saveDirectory.getAbsolutePath() + "/logs");
		logDir.mkdirs();
		Logger.getInstance().setFile(logDir);

		users = new Users(saveDirectory);
	}


	/**
	 * Registers a user. If there is already someone with the same username 0 is
	 * returned. Otherwise the tag is returned. It is Important that the
	 * username and password do not go over the byte limit
	 */
	public int registerUser(String userName, String password) {
		synchronized (usersLock) {
			return users.register(userName, password);
		}
	}

	public boolean login(int tag, String password) {
		synchronized (usersLock) {
			return users.login(tag, password);
		}
	}

	/**
	 * Checks if it can delete saved messages and files. Shold be called once a
	 * day
	 */
	public void checkForDelete() {

	}

	public static void main(String[] args) {
		DataManagement dm = new DataManagement(null);
		System.out.println("nowTesting");
		Scanner scan = new Scanner(System.in);
		while (!scan.nextLine().equals("exit")) {
			System.out.println("Username: ");
			String name = scan.nextLine();
			System.out.println("Pw: ");
			String pw = scan.nextLine();
			System.out.println("Tag: " + dm.registerUser(name, pw));
		}
		while (!scan.nextLine().equals("exit")) {
			System.out.println("login tag: ");
			int tag = Integer.valueOf(scan.nextLine());
			System.out.println("pw: ");
			String pw = scan.nextLine();
			System.out.println(dm.login(tag, pw));
		}
		scan.close();
	}

}
