package user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Chat implements Comparable<Chat> {

	private Date date;
	private ArrayList<Message> messages;

	public Chat(ArrayList<Message> messages, Date date) {
		this.messages = messages;
		this.date = date;
		Collections.sort(messages);
	}

	public void addMessage(Message newMessage) {
		if (newMessage != null)
			messages.add(newMessage);
	}

	public Date getDate() {
		return date;
	}

	@Override
	public int compareTo(Chat o) {
		return this.date.compareTo(o.date);
	}

}
