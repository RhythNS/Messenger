package userDataManagement;

import java.io.BufferedWriter;
import java.io.File;
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
			System.err.println("Could not init the file! #BlameBene");
			e.printStackTrace();
			return false;
		}
		boolean success = true;
		try {
			bw.write(file);
		} catch (IOException e) {
			System.err.println("Could not write! #BlameBene");
			e.printStackTrace();
			success = false;
		}
		try {
			bw.close();
		} catch (IOException e) {
			System.err.println("Could not close! #BlameBene");
			e.printStackTrace();
		}
		return success;
	}

	File getFile(String name) {
		return new File(dir, name + ".file");
	}
}
