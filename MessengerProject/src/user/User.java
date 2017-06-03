package user;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class User {

	private String username;
	private String private_key;
	private ArrayList<Contact> friendlist;
	private ArrayList<Group> groups;
	private Client client;
	private int deviceNumber;
	public FileOutputStream fos;
	private int tag;

	public User(String name) {
		this.username = name;
	}

	public ArrayList<Chat> getAllChats() {
		return null;
	}

	public void setDeviceNumber(int nr) {
		this.deviceNumber = nr;
	}

	/*
	 * request friendship, "recieve" friendlist, remove friend, accept friend
	 * 
	 * group invite, promote group leader, leave group, update groupmember
	 * 
	 * Alles was empfangen wird benötigt Methode
	 */

	public boolean replyFriendRequest(int tag, boolean accept) {
		if (accept) {
			System.out.println("You've got a new friend");
			return friendlist.add(new Contact(username, tag));

		}
		System.out.println("Friendship declined");
		return accept;
	}

	public void requestFriendship(int tag, String username) {
		// if(client.sendFriendRequest(tag){

		// }
	}

	public void recieveFriendlist(ArrayList<Contact> friendlist) {
		this.friendlist = friendlist;
	}

	public boolean removeFriend(int tag) {
		Contact result = searchUserInFriendlist(tag);
		if (result != null) {
			return friendlist.remove(result);
		}
		return false;
	}

	public Contact searchUserInFriendlist(int tag) {
		for (Contact c : friendlist) {
			if (c.getTag() == tag) {
				return c;
			}
		}
		System.err.println("User not found in Friendlist #BlameBenós");
		return null;
	}

	public ArrayList<Contact> getFriendlist() {
		return friendlist;
	}

	// Anmeldung&Registrierung
	public boolean register(String username, String password) throws IOException {
		int result = client.register(username, password);
		if (result == -1) {
			System.err.println("Registration failed #BlameBenós");
			return false;
		} else
			this.tag = result;
		System.out.println("Registration succeded");
		return true;
	}

	public boolean login(String username, String password) throws IOException {
		loadMessages();
		return client.login(tag, password, deviceNumber);
	}

	private void loadMessages() {
		// ersten 10 Chats und Nachrichten laden
	}

	// Kommunikation
	public void sendMassage(String message, int tag) {
		client.writeMessage(tag, message);
	}

	public void messageRecieved(int empf, int sender, String message, Date date) {
		if (sender == tag && empf > 0) {
			// Nachricht an Kontakt
			Contact c = searchUserInFriendlist(empf);
			c.getChat().addMessage(empf, sender, message, date);
			// eigene Nachrichten vor dem Abschicken anzeigen?
		} else {
			if (sender == tag && empf < 0) {
				// Nachricht an Gruppe
				Group g = searchGroupInGrouplist(empf);
				g.getChat().addMessage(empf, sender, message, date);
			} else {
				if (sender > 0 && empf == tag) {
					// Nachricht von Kontakt
					Contact c = searchUserInFriendlist(sender);
					c.getChat().addMessage(empf, sender, message, date);
				} else {
					if (sender != tag && empf < 0) {
						// Nachricht von Gruppe
						Group g = searchGroupInGrouplist(empf);
						g.getChat().addMessage(empf, sender, message, date);
						// Welchen Tag hat Empf wenn an Gruppe?
					} else {
						System.err.println("Fatal Error #BlameBenós");
					}
				}
			}
		}

	}

	public FileOutputStream dataRecieved(int empf, int send, String filename) {
		return fos;
	}

	public void sendData(int tag, String filename, FileInputStream stream, boolean directConnection) {
		try {
			client.sendData(tag, filename, stream, directConnection);
		} catch (IOException e) {
			System.err.println("Sending data failed #BlameBenós");
			e.printStackTrace();
		}
	}

	public ArrayList<Message> getOwnMessages(Contact c, int messageCount) {
		ArrayList<Message> ownMessages = new ArrayList<Message>();
		for (Message m : c.getChat().getMessages(messageCount)) {
			if (m.sender == tag) {
				ownMessages.add(m);
			}
		}
		return ownMessages;
	}

	public ArrayList<Message> getContactMessages(Contact c, int messageCount) {
		ArrayList<Message> ownMessages = new ArrayList<Message>();
		for (Message m : c.getChat().getMessages(messageCount)) {
			if (m.sender == c.getTag()) {
				ownMessages.add(m);
			}
		}
		return ownMessages;
	}
	// Gruppen Zeug

	public void groupInvite(int groupTag, String groupName, ArrayList<Contact> groupList) {
		Group g = new Group(groupName, groupList);
		groups.add(g);
	}

	public boolean promoteGroupLeader(int groupTag, int tag) {
		Group g = searchGroupInGrouplist(tag);
		Contact a = searchUserInGroup(tag, g);
		if (g != null && a != null) {
			g.setAdmin(a);
			return true;
		}
		System.err.println("User or group not found!");
		return false;
	}

	public void kickGroupMember(int groupTag, int tag) {
		// Permission check??
		Group g = searchGroupInGrouplist(tag);
		Contact a = searchUserInGroup(tag, g);
		if (g != null && a != null) {
			g.kickUser(tag);
		}
		System.err.println("User or group not found!");
	}

	public Group searchGroupInGrouplist(int tag) {
		for (Group g : groups) {
			if (g.getTag() == tag) {
				return g;
			}
		}
		System.err.println("Group not found in your Grouplist #BlameBenós");
		return null;
	}

	public Contact searchUserInGroup(int tag, Group g) {
		for (Contact c : g.getGroupList()) {
			if (c.getTag() == tag) {
				return c;
			}
		}
		System.err.println("User not found in Group");
		return null;
	}

	public boolean löschenGroup(Group Group) {
		if (groups.contains(Group)) {
			return groups.remove(Group);
		}
		System.out.println("Group doesn't exsist #BlameBenós");
		return false;
	}

	public boolean GroupUmbenennen(int tag, String neuerName) {
		Group result = searchGroupInGrouplist(tag);
		if (result != null) {
			result.setGroupName(neuerName);
		}
		return false;
	}

	/*
	 * TODO NACHRICHTEN-Management
	 * 
	 */
}
