package server;

import com.sun.media.jfxmedia.logging.Logger;
import dataManagement.DateCalc;
import dataManagement.FileMessage;
import dataManagement.Mailbox;
import dataManagement.TextMessage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class Account {

	private String password;
	private int tag;
	private ArrayList<Client> clients;
	private Server server;
	private boolean isLoggedIn = false;
	private final Object object = new Object();

	public Account(int tag, Server server) {
		this.tag = tag;
		clients = new ArrayList<>();
		this.password = password;
	}


	public void recieveMessage(int from, String message, Client whoGotIt){
		String date = DateCalc.getMessageDate();

		for (Client c : clients) {
			if (!whoGotIt.equals(c))
				c.writeMessage(tag, from, date, message);
		}

		server.recieveMessage(from,this,message,date);
	}
	public void requestMessage(Client sender,String date){
		Mailbox mb = server.requestMessage(this, date);

		if(mb == null)return;

		for (int i = 0; i < mb.messageSize(); i++) {
			TextMessage tm = mb.getMessage(i);
			sender.writeMessage(tm.getTo(),tm.getFrom(),tm.getDate(),tm.getContent());
		}
		for (int i = 0; i < mb.fileSize(); i++) {
			FileMessage fm = mb.getFile(i);
			try {
				sender.writeMessage(fm.getFrom(),fm.getTo(),fm.getDate(),fm.getContent());
			} catch (FileNotFoundException e) {
				dataManagement.Logger.getInstance().log("Acc002: Cannot find the File");
			}
		}
	}

	public void disconnect(Client client, int deviceNumber){
		if(clients.remove(client)) {
			server.disconnectDevice(deviceNumber,this);
			if(clients.size()== 0){
				server.disconnctAccount(this);
			}
		}
	}

	public boolean addClient(Client toAdd){
		return clients.size() > 20;
	}
	public void acceptFriend(int tag){
		server.addFriendTo(this,tag);
	}


	public void addToFriendlist(int tagToAdd){
		server.addFriendTo(this, tagToAdd );
	}

	public String getPassword() {
		return password;
	}

	public int createGroup(String nameOfGroup, Account[] accounts) {
		if (nameOfGroup == null || accounts == null) {
			dataManagement.Logger.getInstance().log("Acc001: ToCreate Group there is something null in createGroup()");
			return 0;
		}
		return server.createGroup(nameOfGroup, accounts);
	}

	public int getTag() {
		return tag;
	}

	public int[] getFriendList(){
		return server.getFriendList(tag);
	}

	public boolean leaveGroup(int grpTag){
		return server.leaveGroup(tag ,grpTag);
	}

	public FileOutputStream dataReceived(int tag, String message) {
		//TODO
		return null;
	}
}
