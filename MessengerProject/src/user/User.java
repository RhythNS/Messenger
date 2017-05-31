package user;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class User {

	private String name;
	private String private_key;
	private ArrayList<Contact> contacts;
	private ArrayList<Group> Groups;
	private Client client;

	public User(String name) {
		this.name = name;
	}

	public void sendMassage(String message, int tag) {
		client.writeMessage(tag, message);
	}

	public void sendData(File file, int tag) {
		client.sendData(tag, file, stream, directConnection);
	}

	public boolean löschenGroup(Group Group) {
		if (Groups.contains(Group)) {
			return Groups.remove(Group);
		}
		System.out.println("Group nicht vorhanden #BlameBenós");
		return false;
	}

	public boolean GroupUmbenennen(Group Group, String neuerName) {
		if (!(Group == null || neuerName == null)) {
			for (Group eineGroup : Groups) {
				if (eineGroup.getGroupnName().equals(Group.getGroupnName())) {
					Group.setGroupnName(neuerName);
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

	public ArrayList<Contact> getContacts() {
		return contacts;
	}
}
