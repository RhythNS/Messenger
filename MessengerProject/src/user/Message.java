package user;

import java.util.Date;

public class Message {

	int empfänger;
	int sender;
	String messageContent;
	Date date;

	public Message(int empfänger, int sender,String messageContent,Date date){
		this.empfänger=empfänger;
		this.sender=sender;
		this.messageContent=messageContent;
		this.date=date;
	}
	
	
}
