package dataManagement;

import java.io.File;

public class FileMessage extends Message{

	private File content;

	public FileMessage(String date, int from, int to) {
		super(date, from, to);
	}

	void setContent(File content) {
		this.content = content;
	}

	public File getContent() {
		return content;
	}

}
