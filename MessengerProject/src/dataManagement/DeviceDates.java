package dataManagement;

import java.io.File;
import server.Constants;

public class DeviceDates extends ListFiles {

	DeviceDates(File saveDir) {
		super(Constants.MAX_DEVICES, DateCalc.getDeviceDate().length(), new File(saveDir, "devices.txt"));
	}

	/**
	 * Logs a Tag in. Tag must be above 0. Returns the new deviceNr and last
	 * login date in form of DeviceLogin. If something went wrong null is
	 * returned!
	 */
	DeviceLogin login(int tag, int device) {
		if (!tagRegisterd(tag))
			make(tag);

		if (device == -1) {
			String currentDate = DateCalc.getDeviceDate(Constants.DAYS_GET_FOR_NEW_DEVICE_LOGIN, 0, 0, 0);
			device = getFirstNull(tag);
			if (device != -1) {
				set(tag, device, currentDate);
				return new DeviceLogin(currentDate, device);
			}
			String[] dates = getAll(tag);
			int nr = DateCalc.getLowestDateDevice(dates);
			set(tag, nr, currentDate);
			return new DeviceLogin(currentDate, nr);
		}
		return new DeviceLogin(get(tag, device), device);
	}

	void logout(int tag, int device, boolean timeout) {
		String date = "";
		if (timeout)
			date = DateCalc.getDeviceDate(0, 0, 0, Constants.MAX_TIMEOUT_IN_SECONDS);
		else
			date = DateCalc.getDeviceDate();
		set(tag, device, date);
	}
}
