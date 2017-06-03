package user;

import java.util.Date;

public class Message implements Comparable<Message>{

	int empfänger;
	int sender;
	String messageContent;
	Date date;
	Message next;
	Message previous;
	
	
	public Message(int empfänger, int sender,String messageContent,Date date,Message previous){
		this.empfänger=empfänger;
		this.sender=sender;
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
		return "Message [empfänger=" + empfänger + ", sender=" + sender + ", messageContent=" + messageContent
				+ ", date=" + date + "]";
	}
	
	
}
