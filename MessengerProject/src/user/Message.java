package user;

import java.util.Date;

public class Message implements Comparable<Message>{

	int empf�nger;
	int sender;
	String messageContent;
	Date date;
	Message next;
	Message previous;
	
	
	public Message(int empf�nger, int sender,String messageContent,Date date,Message previous){
		this.empf�nger=empf�nger;
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
		return "Message [empf�nger=" + empf�nger + ", sender=" + sender + ", messageContent=" + messageContent
				+ ", date=" + date + "]";
	}
	
	
}
