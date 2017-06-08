package user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import dataManagement.DateCalc;

public class Chat implements Comparable<Chat> {

	private Date date;
	private ArrayList<Message> messages;

	public Chat(Date date) {
		messages = new ArrayList<>();
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

	public String getFormattedDay() {
		if (date != null)
			DateCalc.getForYear().format(date);
		return null;
	}

	/**
	 * New messages are at the front
	 */
	public ArrayList<Message> getMessages() {
		return messages;
	}

	public void setMessages(ArrayList<Message> messages) {
		this.messages = messages;
	}

	@Override
	public int compareTo(Chat o) {
		return -this.date.compareTo(o.date);
	}


}
