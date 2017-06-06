package user;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dataManagement.DateCalc;

public class Chat {

	private Contact contact = null;
	private Group group = null;
	private Message neusteNachricht;
	private int count;
	userDataManagement.DataManagement dm;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

	public Chat(Contact contact) {
		this.contact = contact;
		this.dm=new userDataManagement.DataManagement(null);	
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
	
	public void addMessage(int receiver, int transmitter, String message, Date date,boolean save) {
		if (save) {
		dm.saveMessage(userDataManagement.DateCalc.getTime(), receiver, transmitter, message);
		}
		if  (neusteNachricht==null){
			neusteNachricht=new Message(transmitter, receiver, message, date, null);
			count++;
		}
		else{
			if (neusteNachricht.date.compareTo(date)>0) {
				neusteNachricht=new Message(transmitter,receiver , message, date, neusteNachricht);
				count++;
			}
			else {
				Message zeiger= neusteNachricht;
				while(zeiger.previous!=null&&zeiger.previous.date.compareTo(date)<0){
					zeiger=zeiger.previous;
				}
				Message m= new Message(transmitter, receiver, message, date, zeiger.previous);
				zeiger.previous=m;
				count++;
				}
			}
		}
	
	//TODO Persistenz
	public void loadMessages(String forDate,int tag){
	
		ArrayList<userDataManagement.Message>messagesfromTag=dm.readAllTag(forDate, tag);
		for (userDataManagement.Message m : messagesfromTag) {
			Date date=null;
			try {
				date = dateFormat.parse(m.getDate());
			} catch (ParseException e) {
				System.err.println("Could not parse the date!");
				e.printStackTrace();
			}
			addMessage(m.getFrom(), m.getTo(), m.getContent(),date, false);
		}
	}
}
