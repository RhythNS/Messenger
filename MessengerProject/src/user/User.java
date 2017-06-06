package user;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	private ArrayList<Contact> pendingFriendrequest=new ArrayList<Contact>();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

	public ArrayList<Contact> getFriendlist() {
		return friendlist;
	}

	public ArrayList<Contact> getPendingFriendrequest() {
		return pendingFriendrequest;
	}
	
	public User(String name) {
		this.username = name;
	}

	public void setDeviceNumber(int nr) {
		this.deviceNumber = nr;
	}

	
	
	public boolean replyFriendRequest(int tag, boolean reply) {
		if (reply) {
			System.out.println("You've got a new friend");
			return friendlist.add(new Contact(username, tag));

		}
		System.out.println("Friendship declined");
		return reply;
	}
	public boolean getFriendreply(int tag,boolean reply){
		for (Contact c : getPendingFriendrequest()) {
			if(tag==c.getTag()){
				getPendingFriendrequest().remove(c);
				return friendlist.add(c);
			}
		}
		return reply;
	}

	public void requestFriendship(int tag, String username) {
		if (tag!=0&&username!=null) {
			Contact friend=new Contact(username,tag);
			getPendingFriendrequest().add(friend);
		}
	}

	public void receiveFriendlist(ArrayList<Contact> friendlist) {
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
		System.err.println("User not found in Friendlist #BlameBenůs");
		return null;
	}

	// Anmeldung&Registrierung
	public boolean register(String username, String password) throws IOException {
		int result = client.register(username, password);
		if (result == -1) {
			System.err.println("Registration failed #BlameBenůs");
			return false;
		} else
			this.tag = result;
		System.out.println("Registration succeded");
		return true;
	}

	public boolean login(String username, String password) throws IOException {
		//Nachrichten ins Ram laden
		return client.login(tag, password, deviceNumber);
	}

	// Kommunikation
	public void sendMessage(String message, int tag) {
		searchUserInFriendlist(tag).getChat().addMessage(tag, this.tag, message, new Date());
		client.writeMessage(tag, message);
	}

	public void messageReceived(int sender, int empf, String message, String givenDate) {
		Date date=null;
		try {
			date = dateFormat.parse(givenDate);
		} catch (ParseException e) {
			System.err.println("Could not parse the date!");
			e.printStackTrace();
		}
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
						System.err.println("Fatal Error #BlameBenůs");
					}
				}
			}
		}

	}

	public FileOutputStream dataReceived(int send, int empf, String filename,String infos) {
		
		return fos;
	}

	public void sendData(int tag, String filename, FileInputStream stream) {
		try {
			client.sendData(tag, filename, stream);
		} catch (IOException e) {
			System.err.println("Sending data failed #BlameBenůs");
			e.printStackTrace();
		}
	}

	public ArrayList<Message> getOwnMessages(Contact c, int messageCount) {
		ArrayList<Message> ownMessages = new ArrayList<Message>();
		for (Message m : c.getChat().getMessages(messageCount)) {
			if (m.transmitter == tag) {
				ownMessages.add(m);
			}
		}
		return ownMessages;
	}

	public ArrayList<Message> getContactMessages(Contact c, int messageCount) {
		ArrayList<Message> ownMessages = new ArrayList<Message>();
		for (Message m : c.getChat().getMessages(messageCount)) {
			if (m.transmitter == c.getTag()) {
				ownMessages.add(m);
			}
		}
		return ownMessages;
	}
	// Gruppen Zeug

	public void groupInvite(int groupTag, String groupName, ArrayList<Contact> groupList) {
		Group g = new Group(groupTag,groupName, groupList);
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
		System.err.println("Group not found in your Grouplist #BlameBenůs");
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

	public boolean deleteGroup(Group Group) {
		if (groups.contains(Group)) {
			return groups.remove(Group);
		}
		System.out.println("Group doesn't exsist #BlameBenůs");
		return false;
	}

	public boolean renameGroup(int tag, String neuerName) {
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
