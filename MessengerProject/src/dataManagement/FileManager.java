package dataManagement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {

	private File dir;
	private int iterator;

	public FileManager(File dir) {
		this.dir = dir;
		iterator = -1;
	}

	String getFileName() {
		iterator++;
		return iterator + "";
	}

	boolean saveFile(String name, String file) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File(dir, name + ".file")));
		} catch (IOException e) {
			Logger.getInstance().log("Error FM1: Could not init the file! #BlameBene");
			e.printStackTrace();
			return false;
		}
		boolean success = true;
		try {
			bw.write(file);
		} catch (IOException e) {
			Logger.getInstance().log("Error FM2: Could not write! #BlameBene");
			e.printStackTrace();
			success = false;
		}
		try {
			bw.close();
		} catch (IOException e) {
			Logger.getInstance().log("Error FM3: Could not close! #BlameBene");
			e.printStackTrace();
		}
		return success;
	}

	String getFile(String name) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(dir, name + ".file")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String input = "";
		try {
			input = br.readLine();
			if (input == null) {
				Logger.getInstance().log("Error FM6: Could not read. File is null! #BlameBene");
				return null;
			}
			do {
				input += '\n';
				input = br.readLine();
			} while (input != null);
		} catch (IOException e) {
			Logger.getInstance().log("Error FM4: Could not read! #BlameBene");
			e.printStackTrace();
			try {
				br.close();
			} catch (IOException e1) {
				Logger.getInstance().log("Error FM5: Could not close the reader! #BlameBene");
				e1.printStackTrace();
			}
			return null;
		}
		try {
			br.close();
		} catch (IOException e) {
			Logger.getInstance().log("Error FM6: Could not close the reader! #BlameBene");
			e.printStackTrace();
		}
		return input;
	}

}
