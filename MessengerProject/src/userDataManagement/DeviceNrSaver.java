package userDataManagement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DeviceNrSaver {

	static boolean save(int nr, File f) {
		if (nr < 0 || f == null) {
			System.err.println("Wrong parameters! #BlameBene");
			return false;
		}
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(f));
		} catch (IOException e) {
			System.err.println("Could not init BufferedWriter! #BlameBene");
			new FileException(f);
			e.printStackTrace();
			return false;
		}
		boolean suc = true;
		try {
			bw.write(Integer.toString(nr, Character.MAX_RADIX));
			bw.flush();
		} catch (IOException e) {
			System.err.println("Could not write! #BlameBene");
			e.printStackTrace();
			suc = false;
		}
		try {
			bw.close();
		} catch (IOException e) {
			System.err.println("Could not close! #BlameBene");
			e.printStackTrace();
		}
		return suc;
	}

	static int load(File f) {
		if (f == null || f.length() < 1) {
			System.err.println("Given File is null or empty! #BlameBene");
			return -1;
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException e) {
			System.err.println("Could not init BufferedReader! #BlameBene");
			e.printStackTrace();
			return -1;
		}
		String loadedNr = "";
		try {
			loadedNr = br.readLine();
		} catch (IOException e) {
			System.err.println("Could not read! #BlameBene");
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			System.err.println("Could not close! #BlameBene");
			e.printStackTrace();
		}
		return loadedNr == null ? -1 : Integer.parseInt(loadedNr, Character.MAX_RADIX);
	}

}
