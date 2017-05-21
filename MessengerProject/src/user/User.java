package user;

import java.io.File;
import java.util.ArrayList;

public class User {

	private String name;
	private String private_key;
	private ArrayList<Contact> contacts;
	private ArrayList<Group> Groupn;
	private Client client;

	public User(String name) {
		this.name = name;
	}

	public void sendMassage(String message, int tag) {
		client.writeMessage(tag, message);
	}

	public void sendData(File file, int tag) {
		client.writeData(tag, file);
	}

	public boolean deleteGroup(Group Group) {
		if (Groupn.contains(Group)) {
			return Groupn.remove(Group);
		}
		System.out.println("Group nicht vorhanden #BlameBenós");
		return false;
	}

	public boolean renameGroup(Group Group, String neuerName) {
		if (!(Group == null || neuerName == null)) {
			for (Group eineGroup : Groupn) {
				if (eineGroup.getGroupnName().equals(Group.getGroupnName())) {
					Group.setGroupnName(neuerName);
					return true;
				}
			}
			System.err.println("Die Group ist nicht in deiner Liste vorhanden");
			return false;
		} else {
			System.err.println("Geben Sie einen gültigen Groupn Namen ein");
		}
		return false;
	}

	public ArrayList<Contact> getContacts() {
		return contacts;
	}
}
