package user;

import java.util.Date;

public class Message implements Comparable<Message>{

	int receiver;
	int transmitter;
	String messageContent;
	Date date;
	Message next;
	Message previous;
	
	
	public Message(int receiver, int sender,String messageContent,Date date,Message previous){
		this.receiver=receiver;
		this.transmitter=sender;
		this.messageContent=messageContent;
		this.date=date;
		this.previous=previous;
		
	}

	@Override
	public int compareTo(Message m) {
		return this.date.compareTo(m.date);
	}

	@Override
	public String toString() {
		return "Message [receiver=" + receiver + ", transmitter=" + transmitter + ", messageContent=" + messageContent
				+ ", date=" + date + "]";
	}
	
	
}
