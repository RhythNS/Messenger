package user;

import secruity.MD5Hash;
import user.UI.UiHandler;
import userDataManagement.DataManagement;
import userDataManagement.DateCalc;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class User {

	private String host = "localhost";
	private ArrayList<Contact> friendList, pendingFriends, requestedFriends, unsortedGroupMembers;
	private ArrayList<Group> groups;
	private Client client;
	private int port = 25565;
	private Contact self;
	private DataManagement dataManagement;
	private UiHandler uiComm = UiHandler.getInstance();
	private Thread nextDay;

	public User() {
		dataManagement = new DataManagement(null);
		friendList = new ArrayList<>();
		pendingFriends = new ArrayList<>();
		requestedFriends = new ArrayList<>();
		unsortedGroupMembers = new ArrayList<>();
		groups = new ArrayList<>();
		client = new Client(host, port, this);
		nextDay = new Thread(new Runnable() {
			@Override
			public void run() {
				LocalTime lt = LocalTime.now();
				int time = lt.toSecondOfDay(), time2 = time;
				while (true) {
					time = lt.toSecondOfDay();
					if (time < time2) {
						dataManagement.addDay();
					}
					time2 = time;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						System.err.println("Error TCFD0: InterrupptedException! #BlameBene");
						e.printStackTrace();
					}
					lt = LocalTime.now();
				}
			}
		});
		nextDay.start();
	}

	public Contact getSelf() {
		return self;
	}

	public String getUsername() {
		return self.getUsername();
	}

	public String getColor() {
		return self.getColor();
	}

	public int getTag() {
		return self.getTag();
	}

	public ArrayList<Contact> getFriendlist() {
		return friendList;
	}

	public ArrayList<Contact> getPendingFriends() {
		return pendingFriends;
	}

	public ArrayList<Contact> getRequestedFriends() {
		return requestedFriends;
	}

	public ArrayList<Group> getGroups() {
		return groups;
	}

	public boolean register(String username, String password, String color) {
		if (username == null || username.length() == 0 || password == null || password.length() == 0 || color == null
				|| color.length() == 0) {
			System.err.println("Something is null or has a length of 0. I refuse to do anything with it! #BlameBene");
			return false;
		}
		int tag = 0;
		try {
			tag = client.register(username, MD5Hash.getMD5(password), color);
		} catch (IOException | NoSuchAlgorithmException e) {
			System.err.println("IOException or NosuchAlgorithmException \\(>.<)/ #BlameBene");
			e.printStackTrace();
			return false;
		}
		if (tag == 0) {
			System.err.println("Tag was 0 meaing something went wrong! #BlameBene");
			return false;
		}
		self = new Contact(tag);
		self.setColor(color);
		self.setUsername(username);
		return true;
	}

	public boolean login(String username, String password) {
		try {
			if (!client.login(username, MD5Hash.getMD5(password), dataManagement.getDeviceNr())) return false;
		} catch (IOException | NoSuchAlgorithmException e) {
			System.err.println("IOException or NosuchAlgorithmException \\(>.<)/ #BlameBene");
			e.printStackTrace();
			return false;
		}
		self = client.searchUser(username);
		return true;
	}

	public void setDeviceNumber(int number) {
		dataManagement.saveDeviceNr(number);
	}

	public void disconnect() {
		client.disconnect();
	}

	public void createGroup(ArrayList<Contact> contacts, String name) {
		int tags[] = new int[contacts.size() + 1];
		tags[0] = self.getTag();
		for (int i = 0; i < contacts.size(); i++) {
			tags[i + 1] = contacts.get(i).getTag();
		}
		int groupTag = client.createGroup(name, tags);
		if (groupTag != 0) {
			Group g = new Group(groupTag, name);
			groups.add(g);
		}
	}

	public void leaveGroup(Group group) {
		if (groups != null) {
			group.leave();
			groups.remove(group);
			client.leaveGroup(group.getTag());
		}
	}

	public void writeMessage(Group group, String message) {
		if (group != null && message != null) {
			Message m = new Message(self.getTag(), group.getTag(), message, DateCalc.getTime());
			int number = group.getDayNr(m.date);
			group.getChats().get(number).addMessage(m);
			client.writeMessage(group.getTag(), message);
			dataManagement.saveMessage(DateCalc.getWholeYear().format(m.date), self.getTag(), group.getTag(), message);
		}
	}

	public void writeMessage(Contact contact, String message) {
		if (contact != null && message != null) {
			Message m = new Message(self.getTag(), contact.getTag(), message, DateCalc.getTime());
			int number = contact.getDayNr(m.date);
			contact.getChats().get(number).addMessage(m);
			client.writeMessage(contact.getTag(), message);
			dataManagement.saveMessage(DateCalc.getWholeYear().format(m.date), self.getTag(), contact.getTag(),
					message);
		}
	}

	public void removeFriend(Contact contact) {
		removeFriends(contact);
		client.removeFriend(contact.getTag());
	}

	public void sendFriendRequest(int tag) {
		Contact contact = getContact(tag);
		Contact contactServerside = client.searchUser(tag);
		if (contactServerside == null) {
			System.err.println("Guy not found! #BlameBene");
			return;
		}
		contact.setColor(contactServerside.getColor());
		contact.setUsername(contact.getUsername());
		removeUnsortedGroupMembers(contact);
		addToPending(contact);
		client.sendFriendRequest(tag);
	}

	public void answerFriendRequest(Contact contact, boolean accepted) {
		removePending(contact);
		if (accepted) {
			client.replyFriendRequest(contact.getTag(), accepted);
			addToFriends(contact);
		} else
			client.replyFriendRequest(contact.getTag(), accepted);
	}

	public void sendData(Group group, String filename, byte[] bytes) {
		if (group != null && filename != null && bytes != null) {
			group.getChats().get(group.getDayNr(DateCalc.getTime())).addMessage(
					new Message(self.getTag(), group.getTag(), "I have sent the file " + filename, DateCalc.getTime()));
			try {
				client.sendData(group.getTag(), filename, bytes);
			} catch (IOException e) {
				System.err.println("IOException \\(>.<)/ #BlameBene");
				e.printStackTrace();
				return;
			}
		}
	}

	public void inviteToGroup(Contact contact, Group group) {
		if (contact != null && group != null) {
			group.addUser(contact);
			client.groupInvite(group.getTag(), contact.getTag());
		}
	}

	public void kickGroupMember(Contact contact, Group group) {
		if (contact != null && group != null && group.getAdmin() == self) {
			group.kickUser(contact.getTag());
			client.kickGroupMember(group.getTag(), contact.getTag());
		}
	}

	public void promoteToGroupLeader(Contact contact, Group group) {
		if (contact != null && group != null && group.contains(contact) && group.getAdmin() == self) {
			group.setAdmin(contact);
			client.promoteGroupLeader(group.getTag(), contact.getTag());
		}
	}

	void dataReceived(int from, int to, String info, byte[] bytes) {
		String file = dataManagement.saveFile(from, to, info, bytes);
		Contact c = getContact(to);
		if (c == null) {
			System.err.println("You got a file from someone who is not in RAM! #BlameBene");
			return;
		}
		Message m = new Message(from, to, "I have sent you a message. Here is the path to it: " + file,
				DateCalc.getTime());
		addMessage(to, m);
	}

	void messageReceived(int from, int to, String message, String givenDate) {
		Message m = new Message(givenDate, from, to);
		m.messageContent = message;
		addMessage(from, m);
		dataManagement.saveMessage(givenDate, from, to, message);
	}

	void addMessage(int otherGuy, Message message) {
		if (message != null) {
			Contact contact = getContact(otherGuy);
			if (contact == null) {
				System.err.println("Somehow sent a message to someone who is not here! #BlameBene");
				return;
			}
			int where = addDay(message.date, contact);
			contact.getChats().get(where).addMessage(message);
			uiComm.messageReceived(otherGuy);
		}
	}

	int addDay(Date day, Contact guy) {
		int nr = guy.getDayNr(day);
		if (nr != -1)
			return nr;
		ArrayList<Message> messages = dataManagement.readAllTag(DateCalc.getWholeYear().format(day), guy.getTag());
		Collections.sort(messages);
		Chat chat = new Chat(day);
		chat.setMessages(messages);
		guy.getChats().add(chat);
		return guy.getChats().size() - 1;
	}

	Contact getContact(int tag) {
		for (int i = 0; i < friendList.size(); i++) {
			if (friendList.get(i).getTag() == tag)
				return friendList.get(i);
		}
		for (int i = 0; i < pendingFriends.size(); i++) {
			if (pendingFriends.get(i).getTag() == tag)
				return pendingFriends.get(i);
		}
		for (int i = 0; i < requestedFriends.size(); i++) {
			if (requestedFriends.get(i).getTag() == tag)
				return requestedFriends.get(i);
		}
		for (int i = 0; i < unsortedGroupMembers.size(); i++) {
			if (unsortedGroupMembers.get(i).getTag() == tag)
				return unsortedGroupMembers.get(i);
		}
		return null;
	}

	void deleteContact(int tag) {
		for (int i = 0; i < friendList.size(); i++) {
			if (friendList.get(i).getTag() == tag) {
				friendList.get(i);
				uiComm.changedList();
				return;
			}
		}
		for (int i = 0; i < pendingFriends.size(); i++) {
			if (pendingFriends.get(i).getTag() == tag) {
				pendingFriends.get(i);
				uiComm.changedList();
				return;
			}
		}
		for (int i = 0; i < requestedFriends.size(); i++) {
			if (requestedFriends.get(i).getTag() == tag) {
				requestedFriends.remove(i);
				uiComm.changedList();
				return;
			}
		}
		for (int i = 0; i < unsortedGroupMembers.size(); i++) {
			if (unsortedGroupMembers.get(i).getTag() == tag) {
				unsortedGroupMembers.remove(i);
				uiComm.changedList();
				return;
			}
		}
	}

	void deleteGroup(int tag) {
		for (int i = 0; i < groups.size(); i++) {
			if (groups.get(i).getTag() == tag) {
				groups.get(i).leave();
				groups.remove(i);
				uiComm.changedList();
				return;
			}
		}
	}

	void addToFriends(Contact contact) {
		if (contact != null)
			if (!friendList.contains(contact)) {
				uiComm.changedList();
				friendList.add(contact);
			}
	}

	void addToPending(Contact contact) {
		if (contact != null)
			if (!pendingFriends.contains(contact)) {
				uiComm.changedList();
				pendingFriends.add(contact);
			}
	}

	void addToRequested(Contact contact) {
		if (contact != null)
			if (!requestedFriends.contains(contact)) {
				requestedFriends.add(contact);
				uiComm.changedList();
			}
	}

	void addToUnsortedGroupMembers(Contact contact) {
		if (contact != null)
			if (!unsortedGroupMembers.contains(contact)) {
				unsortedGroupMembers.add(contact);
				uiComm.changedList();
			}
	}

	boolean removeFriends(Contact contact) {
		if (contact != null) {
			uiComm.changedList();
			return friendList.remove(contact);
		}
		return true;
	}

	boolean removeRequsted(Contact contact) {
		if (contact != null) {
			uiComm.changedList();
			return requestedFriends.remove(contact);
		}
		return true;
	}

	boolean removePending(Contact contact) {
		if (contact != null) {
			uiComm.changedList();
			return pendingFriends.remove(contact);
		}
		return true;
	}

	boolean removeUnsortedGroupMembers(Contact contact) {
		if (contact != null)
			return unsortedGroupMembers.remove(contact);
		return true;
	}

	Group getGroup(int tag) {
		return groups.get(tag);
	}

	void addGroup(Group group) {
		if (group != null)
			groups.add(group);
		uiComm.changedList();
	}

	public boolean amIAdmin(Group group) {
		return group.getAdmin() == self;
	}

}
