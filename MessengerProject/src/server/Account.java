package server;

import dataManagement.DateCalc;
import dataManagement.Logger;
import dataManagement.Mailbox;
import dataManagement.Message;

import java.io.IOException;
import java.util.ArrayList;

public class Account {

	private String password;
	private int tag;
	private ArrayList<Client> clients;
	private final Server server;
	private boolean isLoggedIn = false;

	public Account(int tag, Server server) {
		isLoggedIn = true;
		this.tag = tag;
		clients = new ArrayList<>();
		this.password = password;
		this.server = server;
	}


	public void recieveMessage(int from, String message, Client whoGotIt){
		String date = DateCalc.getMessageDate();

		for (Client c : clients) {
			if (!whoGotIt.equals(c))
				c.writeMessage(from, tag, date, message);
		}

		server.recieveMessage(from,this,message,date);
	}

	/**
	 * This Message sends the
	 * @param from
	 * @param message
	 * @param date
	 * @param whoGotIt
	 */
	void dataReceived(int from, byte[] message,String date, Client whoGotIt) {

		for(Client c: clients){
			if(!whoGotIt.equals(c)){
				try {
					//TODO FILENAME NEEDS TO BE ADDED
					c.sendData(from, tag,date,"filename",message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		server.dataRecieved(from,tag,message,date);
	}


	void requestMessage(Client sender, String date){
		Mailbox mb = server.requestMessage(this, date);

		if(mb == null)return;

		for (int i = 0; i < mb.messageSize(); i++) {
			Message tm = mb.getMessage(i);
			sender.writeMessage(tm.getTo(),tm.getFrom(),tm.getDate(),tm.getContent());
		}
		for (int i = 0; i < mb.fileSize(); i++) {
			Message fm = mb.getFile(i);
			String[] split = fm.getContent().split("\n");
			try {
				sender.sendData(fm.getFrom(),fm.getTo(),fm.getDate(),split[0],split[1].getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	void disconnect(Client client, int deviceNumber){
		if(clients.remove(client)) {
			server.disconnectDevice(deviceNumber,this);
			if(clients.size()== 0){
				isLoggedIn = false;
				server.disconnctAccount(this);
			}
		}
	}

	public boolean addClient(Client toAdd){
		if(clients.size() >= Constants.MAX_DEVICES){
			Logger.getInstance().log("Acc002: Too many devices are logged in!");
			return false;
		}
		if(clients.contains(toAdd)){
			Logger.getInstance().log("Acc003: ");
			return false;
		}
		if(clients.add(toAdd)){
			isLoggedIn = true;
			return true;
		}
		return false;
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

}