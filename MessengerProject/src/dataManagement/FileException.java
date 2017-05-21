package dataManagement;

import java.io.File;

public class FileException extends Exception{

	public FileException(File f) {
		super();
		System.err.println("Something is wrong with that file! FileExists: " + f.exists() + ", isDir: "
				+ f.isDirectory() + ", canWrite: " + f.canWrite() + ", canRead: " + f.canRead()
				+ "#BlameBene");
	}
}
