package dataManagement;

import java.io.File;

public class FileMessage extends Message{

	private File content;

	public FileMessage(String date, long pointerFrom, long pointerTo) {
		super(date, pointerFrom, pointerTo);
	}

	void setContent(File content) {
		this.content = content;
	}

	public File getContent() {
		return content;
	}

}
