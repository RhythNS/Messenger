package userDataManagement;

import java.util.ArrayList;

public class Mailbox {

	ArrayList<Message> messages;
	ArrayList<Message> files;

	Mailbox() {
		messages = new ArrayList<>();
		files = new ArrayList<>();
	}

	public Message getMessage(int i) {
		return messages.get(i);
	}

	public Message getFile(int i) {
		return files.get(i);
	}

	public int fileSize() {
		return files.size();
	}

	public int messageSize() {
		return messages.size();
	}

}
