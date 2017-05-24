package dataManagement;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class DataManagement {

	private Users users;
	private DeviceDates devicesDates;
	private final Object usersLock = new Object(), dateLock = new Object();

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
		devicesDates = new DeviceDates(saveDirectory);
	}

	/**
	 * Registers a user. If there is already someone with the same username 0 is
	 * returned. Otherwise the tag is returned. It is Important that the
	 * username and password do not go over the byte limit
	 */
	public int registerUser(String username, String password) {
		synchronized (usersLock) {
			if (username != null && password != null)
				return users.register(username, password);
			return 0;
		}
	}

	public boolean login(int tag, String password) {
		synchronized (usersLock) {
			if (tag > 0)
				return users.login(tag, password);
			return false;
		}
	}

	public boolean login(String username, String password) {
		synchronized (usersLock) {
			if (username != null && password != null)
				return users.login(username, password);
			return false;
		}
	}

	/**
	 * Logout needs to be called when a Device logs itself out or looses
	 * connection
	 */
	public void logout(int tag, int deviceNumber) {
		synchronized (dateLock) {
			devicesDates.logout(tag, deviceNumber);
		}
	}

	/**
	 * Logs in a Device. Returns the devices' new number. Important returns null
	 * if the input is wrong!
	 */
	public DeviceLogin loginDevice(int tag, int device) {
		synchronized (dateLock) {
			if (tag > 0)
				return devicesDates.login(tag, device);
			return null;
		}
	}

	/**
	 * Checks if it can delete saved messages and files. Shold be called once a
	 * day
	 */
	public void checkForDelete() {

	}

	public String[] getMessages(int tag) {
		return null;
	}

	/**
	 * Ze testing of the programms that Noah wrote
	 */
	public static void main(String[] args) {
		DataManagement dm = new DataManagement(null);
		System.out.println("nowTesting");
		Scanner scan = new Scanner(System.in);
		System.out.print("What to do: ");
		String whatToDo = scan.nextLine();
		if (whatToDo.equalsIgnoreCase("users")) {
			while (!scan.nextLine().equals("exit")) {
				System.out.print("Username: ");
				String name = scan.nextLine();
				System.out.print("Pw: ");
				String pw = scan.nextLine();
				System.out.println("Tag: " + dm.registerUser(name, pw));
			}
			while (!scan.nextLine().equals("exit")) {
				System.out.print("login tag: ");
				int tag = Integer.valueOf(scan.nextLine());
				System.out.print("pw: ");
				String pw = scan.nextLine();
				System.out.println(dm.login(tag, pw));
			}
		} else if (whatToDo.equalsIgnoreCase("date")) {
			System.out.print("Exit?: ");
			while (!scan.nextLine().equals("exit")) {
				System.out.print("Tag: ");
				int tag = scan.nextInt();
				System.out.print("Device Nr: ");
				int deviceNr = scan.nextInt();
				DeviceLogin dl = dm.loginDevice(tag, deviceNr);
				System.out.println(dl.getDate() + " : " + dl.getNumber());
				System.out.print("Exit?: ");
			}
		}
		scan.close();
	}

}
