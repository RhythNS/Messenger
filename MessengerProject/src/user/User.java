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
	private ArrayList<Group> Groups;
	private Client client;
	private int deviceNumber;
	public FileOutputStream fos;
	private int tag;

	public User(String name) {
		this.username = name;
	}

	public ArrayList<Chat> getAllChats(){
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
		if (accept){
			return friendlist.add(new Contact(username, tag));
		}
		return accept;
	}

	public void requestFriendship(int tag, String username) {
		
	}

	public void recieveFriendlist(ArrayList<Contact>friendlist) {
		this.friendlist=friendlist;
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
//	Anmeldung&Registrierung
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

	public boolean login(String username, String password) {
		try {
			return client.login(tag, password,deviceNumber);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
//	Kommunikation
	public void sendMassage(String message, int tag) {
		client.writeMessage(tag, message);
	}

	public void messageRecieved(int empf, int sender, String message, Date date) {
		if (sender == tag) {
			// eigene Nachricht -> nach empf ordnen
		}
		if (empf < 0) {

			// gruppe

		}
		if (empf > 0) {

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

	//Gruppen Zeug

	public void groupInvite(int groupTag, String groupName) {
	}

	public void promoteGroupLeader(int groupTag) {
	}

	public void kickGroupMember(int groupTag) {
	}
	
	public boolean löschenGroup(Group Group) {
		if (Groups.contains(Group)) {
			return Groups.remove(Group);
		}
		System.out.println("Group doesn't exsist #BlameBenós");
		return false;
	}

	public boolean GroupUmbenennen(Group Group, String neuerName) {
		if (!(Group == null || neuerName == null)) {
			for (Group eineGroup : Groups) {
				if (eineGroup.getGroupName().equals(Group.getGroupName())) {
					Group.setGroupName(neuerName);
					return true;
				}
			}
			System.err.println("Group doesn't exsist");
			return false;
		} else {
			System.err.println("Type in Groupname");
		}
		return false;
	}
	
	/*TODO 
	 * NACHRICHTEN-Management 
	 * 
	 */
}
