package user;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;
import java.util.ArrayList;

import secruity.MD5Hash;
import user.UI.UiHandler;
import userDataManagement.DataManagement;

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
			client.login(username, MD5Hash.getMD5(password), dataManagement.getDeviceNr());
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


	public void dataReceived(int from, int to, String info, byte[] bytes) {
		dataManagement.saveFile(from, to, info, bytes);
		uiComm.messageReceived(from);
		Contact c = getContact(to);
		if (c == null) {
			System.err.println("You got a file from someone who is not in RAM! #BlameBene");
			return;
		}
	}

	public void messageReceived(int sender, int empf, String message, String givenDate) {

	}

	private void addMessage(int otherGuy, Message message) {
		if (message != null) {
			Contact contact = getContact(otherGuy);
			if (contact == null) {
				System.err.println("Somehow sent a message to someone who is not here! #BlameBene");
				return;
			}
		}
	}

	public Contact getContact(int tag) {
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

	public void deleteContact(int tag) {
		for (int i = 0; i < friendList.size(); i++) {
			if (friendList.get(i).getTag() == tag) {
				friendList.get(i);
				return;
			}
		}
		for (int i = 0; i < pendingFriends.size(); i++) {
			if (pendingFriends.get(i).getTag() == tag) {
				pendingFriends.get(i);
				return;
			}
		}
		for (int i = 0; i < requestedFriends.size(); i++) {
			if (requestedFriends.get(i).getTag() == tag) {
				requestedFriends.remove(i);
				return;
			}
		}
		for (int i = 0; i < unsortedGroupMembers.size(); i++) {
			if (unsortedGroupMembers.get(i).getTag() == tag) {
				unsortedGroupMembers.remove(i);
				return;
			}
		}
	}

	public void deleteGroup(int tag) {
		for (int i = 0; i < groups.size(); i++) {
			if (groups.get(i).getTag() == tag) {
				groups.remove(i);
				return;
			}
		}
	}

	public void addToFriends(Contact contact) {
		if (contact != null)
			if (!friendList.contains(contact))
				friendList.add(contact);
	}

	public void addToPending(Contact contact) {
		if (contact != null)
			if (!pendingFriends.contains(contact))
				pendingFriends.add(contact);
	}

	public void addToRequested(Contact contact) {
		if (contact != null)
			if (!requestedFriends.contains(contact))
				requestedFriends.add(contact);
	}

	public void addToUnsortedGroupMembers(Contact contact) {
		if (contact != null)
			if (!unsortedGroupMembers.contains(contact))
				unsortedGroupMembers.add(contact);
	}

	public boolean removeFriends(Contact contact) {
		if (contact != null)
			return friendList.remove(contact);
		return true;
	}

	public boolean removeRequsted(Contact contact) {
		if (contact != null)
			return requestedFriends.remove(contact);
		return true;
	}

	public boolean removePending(Contact contact) {
		if (contact != null)
			return pendingFriends.remove(contact);
		return true;
	}

	public boolean removeUnsortedGroupMembers(Contact contact) {
		if (contact != null)
			return unsortedGroupMembers.remove(contact);
		return true;
	}

	public Group getGroup(int tag) {
		return groups.get(tag);
	}

	public void addGroup(Group group) {
		if (group != null)
			groups.add(group);
	}

	public boolean amIAdmin(Group group) {
		return group.getAdmin() == self;
	}

}
