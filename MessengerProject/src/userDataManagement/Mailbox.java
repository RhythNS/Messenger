package userDataManagement;

import java.util.ArrayList;

public class Mailbox {

	ArrayList<TextMessage> messages;
	ArrayList<FileMessage> files;

	Mailbox() {
		messages = new ArrayList<>();
		files = new ArrayList<>();
	}

	public TextMessage getMessage(int i) {
		return messages.get(i);
	}

	public FileMessage getFile(int i) {
		return files.get(i);
	}

	public int fileSize() {
		return files.size();
	}

	public int messageSize() {
		return messages.size();
	}

}
