package user;

import java.text.ParseException;
import java.util.Date;

import userDataManagement.DateCalc;

public class Message implements Comparable<Message> {

	int receiver;
	int transmitter;
	Long pointerFrom, pointerTo;
	String messageContent;
	Date date;

	public Message(int transmitter, int receiver, String messageContent, Date date) {
		this.receiver = receiver;
		this.transmitter = transmitter;
		this.messageContent = messageContent;
		this.date = date;
	}

	public Message(String date, int transmitter, int receiver) {
		try {
			this.date = DateCalc.getWholeYear().parse(date);
		} catch (ParseException e) {
			System.err.println("Could not pass! Setting current day! #BlameBene");
			e.printStackTrace();
			this.date = DateCalc.getTime();
		}
		this.transmitter = transmitter;
		this.receiver = receiver;
	}

	@Override
	public int compareTo(Message m) {
		return this.date.compareTo(m.date);
	}

	public Long getPointerFrom() {
		return pointerFrom;
	}

	public void setPointerFrom(Long pointerFrom) {
		this.pointerFrom = pointerFrom;
	}

	public void setPointerTo(Long pointerTo) {
		this.pointerTo = pointerTo;
	}

	public Long getPointerTo() {
		return pointerTo;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	@Override
	public String toString() {
		return "Message [receiver=" + receiver + ", transmitter=" + transmitter + ", messageContent=" + messageContent
				+ ", date=" + date + "]";
	}

}