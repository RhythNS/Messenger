package dataManagement;

import java.io.File;

public class FileException extends Exception{

	private static final long serialVersionUID = -8436835220273307314L;

	public FileException(File f) {
		super();
		System.err.println("Something is wrong with that file! FileExists: " + f.exists() + ", isDir: "
				+ f.isDirectory() + ", canWrite: " + f.canWrite() + ", canRead: " + f.canRead()
				+ "#BlameBene");
	}
}
