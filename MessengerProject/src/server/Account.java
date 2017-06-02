package server;

import com.sun.media.jfxmedia.logging.Logger;
import dataManagement.DateCalc;
import dataManagement.FileMessage;
import dataManagement.Mailbox;
import dataManagement.TextMessage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

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

	public void recieveMessage(Account from, String message, Client whoGotIt){
		synchronized (object) {
			String date = DateCalc.getMessageDate();
			for (Client c : clients) {
				if (!whoGotIt.equals(c))
					c.write(tag, from.tag, date, message);
			}

			server.recieveMessage(from,this,message,date);
		}
	}

	public void requestMessage(Client sender,String date){
		Mailbox mb = server.requestMessage(this, date);
		if(mb == null)return;
		for (int i = 0; i < mb.messageSize(); i++) {
			TextMessage tm = mb.getMessage(i);
			sender.write(tm.getTo(),tm.getFrom(),tm.getDate(),tm.getContent());
		}
		for (int i = 0; i < mb.fileSize(); i++) {
			FileMessage fm = mb.getFile(i);
			try {
				sender.write(fm.getFrom(),fm.getTo(),fm.getDate(),new FileInputStream(fm.getContent()));
			} catch (FileNotFoundException e) {
				dataManagement.Logger.getInstance().log("Acc002: Cannot find the File");
			}
		}

	}

	public void acceptFriend(int tag){
		synchronized (object){
			server.addFriendTo(this,tag);

		}
	}

	public void sendMessage(String message, Account toAccount, Client sender){
		for (Client client: clients) {
			if(!client.equals(sender)){
				client.write(toAccount.getTag(), tag, DateCalc.getMessageDate(),message);
			}
		}

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

	public void addClient(Client client) {
		clients.add(client);
	}
}
