package user;

import java.util.ArrayList;

public class Chat {

	private ArrayList<Message> messages=new ArrayList<Message>();
	private ArrayList<Message> ownMessages=new ArrayList<Message>();
	private Contact contact=null;
	private Group group=null;
	
	public Chat(Contact contact){
		this.contact=contact;
	}
	public Chat(Group group){
		this.group=group;	
	}
	public boolean addMessage(Contact contact,Message m){	
		return messages.add(m);
	}
	public boolean addOwnMessage(Message m){
		return ownMessages.add(m);
	}
}
