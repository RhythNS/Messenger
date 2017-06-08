package userDataManagement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileSaver {

	private File toSaveDir;

	public FileSaver(File f) {
		toSaveDir = f;
	}

	public String write(int fromTag, int toTag, String name, byte[] data) {
		FileOutputStream fos;
		File f = new File(toSaveDir, name);
		try {
			fos = new FileOutputStream(f);
		} catch (IOException e) {
			System.err.println("Could not init buffererdWriter! #BlameBene");
			e.printStackTrace();
			return null;
		}
		try {
			fos.write(data);
		} catch (IOException e) {
			System.err.println("Could not write! #BlameBene");
			e.printStackTrace();
			try {
				fos.close();
			} catch (IOException e1) {
				System.err.println("Could not close the Fos! #BlameBene");
				e1.printStackTrace();
			}
			return null;
		}
		try {
			fos.close();
		} catch (IOException e) {
			System.err.println("Could not close the Fos! #BlameBene");
			e.printStackTrace();
		}
		return f.getAbsolutePath();
	}

}
