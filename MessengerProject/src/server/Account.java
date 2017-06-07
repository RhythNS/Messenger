package server;

import dataManagement.*;

import java.io.IOException;
import java.util.ArrayList;

public class Account {

	private String password;
	private int tag;
	private ArrayList<Client> clients;
	private final Server server;
	private boolean isLoggedIn = false;

	Account(int tag, Server server) {
		isLoggedIn = true;
		this.tag = tag;
		clients = new ArrayList<>();
		this.password = password;
		this.server = server;
	}


	void receiveMessage(int from, String message, Client whoGotIt){
		String date = DateCalc.getMessageDate();

		for (Client c : clients) {
			if (!whoGotIt.equals(c))
				c.writeMessage(from, tag, date, message);
		}

		server.receiveMessage(from,this,message,date);
	}

	/**
	 * This Message sends the data first to all of your devices, then to the server
	 * @param from
	 * @param message
	 * @param filename
	 * @param whoGotIt
	 */
	void dataReceived(int from,String filename, byte[] message, Client whoGotIt) {

		String date = DateCalc.getMessageDate();
		for(Client c: clients){
			if(!whoGotIt.equals(c)){
				try {
					c.sendData(from, tag,date,filename,message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		server.dataReceived(from,tag,message,filename,date);
	}


	void requestMessage(Client sender, String date){
		Mailbox mb = server.requestMessage(this, date);

		if(mb == null)return;

		int[] friends = new int[mb.friendSize()];
		for (int i = 0; i < mb.friendSize(); i++) {
			friends[i] = mb.friends.get(i);
		}
		sender.sendFriendlist(friends);



		int[] requests = new int[mb.requestSize()];
		for (int i = 0; i < mb.requestSize(); i++) {
			requests[i] = mb.requests.get(i);
		}
		sender.sendRequestlist(requests);


		int[] pending = new int[mb.pendingSize()];
		for (int i = 0; i < mb.pendingSize(); i++) {
			pending[i] = mb.pending.get(i);
		}
		sender.sendPendinglist(pending);

		sender.sendGrouplist(mb.groupTransfers);

		sender.updateColors(mb.colors);


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

	void disconnect(Client client, int deviceNumber, boolean timeout){
		if(clients.remove(client)) {
			server.disconnectDevice(deviceNumber,this, timeout);
			if(clients.size()== 0){
				isLoggedIn = false;
				server.disconnctAccount(this);
			}
		}
	}

	boolean addClient(Client toAdd){
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

	UserInfo searchUser(int tag){
		return searchUser(tag);
	}

	UserInfo searchUser(String username){
		return server.searchUser(username);
	}

	public void acceptFriend(int tag){
		server.addFriendTo(this,tag);
	}


	void sendFriendRequest(int toWhomTag){
		server.sendFriendRequest(toWhomTag, tag);
	}

	public void declineFriendShip(int tagtoAdd){
		server.declineFriendShip(this,tagtoAdd);
	}


	void addToFriendlist(int tagToAdd){
		server.addFriendTo(this, tagToAdd );
	}

	public boolean removeFriend(int tagToRemove, Client sender){
		boolean res = server.removeFriend(tagToRemove,tag);
		if(res){
			for (Client c: clients) {
				if(!c.equals(sender))
					c.removeFriend(tagToRemove);
			}
		}
		return res;
	}

	void gotInvitedToGroup(int groupTag, String groupname, int[] member){
		for (Client c :clients) {
			c.groupInvite(groupTag,"",member);
		}
	}
	boolean addToGroup(int groupTag, int toAddTag){
		return server.sendGroupInvite(groupTag, toAddTag);
	}

	void updateGroupMemberForAllClients(int grouptag, int[] memberTags){
		for (Client c: clients) {
			c.updateGroupMembers(grouptag,memberTags);
		}
	}


	public String getPassword() {
		return password;
	}

	int createGroup(String nameOfGroup, int[] accounts) {
		if (nameOfGroup == null || accounts == null) {
			dataManagement.Logger.getInstance().log("Acc001: ToCreate Group there is something null in createGroup()");
			return 0;
		}
		return server.createGroup(nameOfGroup, accounts);
	}

	boolean promoteGroupMember(int grpTag, int userWhoWantsToGetAdminTag){

		boolean res = server.promoteGroupMember(grpTag,userWhoWantsToGetAdminTag, tag);

		if(!res) return false;
		for (Client c :
				clients) {
			c.promoteGroupLeader(grpTag);
		}
		return true;
	}

	boolean removeFromGroup(int groupTag, int whoGetsRemoved){
		return server.removeFromGroup(groupTag, whoGetsRemoved, tag);
	}

	public int getTag() {
		return tag;
	}

	int[] getFriendList(){
		return server.getFriendList(tag);
	}

	boolean leaveGroup(int grpTag){
		return server.leaveGroup(tag ,grpTag);
	}

	void sendMessage(int from, String message, String date) {
		for (Client c :clients) {
			c.writeMessage(from,tag,date,message);
		}
	}

	void sendData(int from, byte[] message, String filename, String date) {
		for (Client c :clients) {
			try {
				c.sendData(from, tag,date,filename,message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	void sendBlocked(Account accountWhoDeclines, boolean acceptOrDecline) {
		for (Client c : clients) {
			c.replyFriendRequest(accountWhoDeclines.getTag(),acceptOrDecline);
		}
	}


	void gotRemovedFromGroup(int grpTag) {
		for (Client c: clients) {
			c.kickGroupMember(grpTag);
		}
	}

	public void receiveFriendRequest(int fromWhomTag,String username) {
		for (Client c :
				clients) {
			c.sendFriendRequest(fromWhomTag, username);
		}
	}

	public void updateFriends(int tagFromWhichAcc) {
		for (Client c :
				clients) {
			c.removeFriend(tagFromWhichAcc);
		}
	}
}