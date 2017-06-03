package user;

import java.util.ArrayList;
import java.util.Date;

public class Chat {

	private Contact contact = null;
	private Group group = null;
	private Message neusteNachricht;
	private int count;
	
	public Chat(Contact contact) {
		this.contact = contact;
	}

	public Chat(Group group) {
		this.group = group;
	}
	
	public ArrayList<Message> getMessages(int count){
	ArrayList<Message> messages=new ArrayList<Message>();
		Message zeiger=neusteNachricht;
		for (int i = 0; i < count; i++) {
			messages.add(zeiger);
			zeiger=zeiger.previous;
		}
		return messages;
	}
	
	public void addMessage(int empf, int sender, String message, Date date) {
		if  (neusteNachricht==null){
			neusteNachricht=new Message(empf, sender, message, date, null);
			count++;
		}
		else{
			if (neusteNachricht.date.compareTo(date)>0) {
				neusteNachricht=new Message(empf, sender, message, date, neusteNachricht);
				count++;
			}
			else {
				Message zeiger= neusteNachricht;
				while(zeiger.previous!=null&&zeiger.previous.date.compareTo(date)<0){
					zeiger=zeiger.previous;
				}
				Message m= new Message(empf, sender, message, date, zeiger.previous);
				zeiger.previous=m;
				count++;
			}
		}
	}
	//TODO Persistenz
	public void fetchMessages(int count){
		//Loading from persistenz 
		//Than add 
		
	}
	public void saveMessages(){
		
	}

	
}
