package user;

import java.util.ArrayList;

public class Group {

	private String publicKey;
	private int tag;
	private String groupName;
	private Contact admin;
	private ArrayList<Contact> contacts;
	private Chat chat;
	
	public Group(String groupName,ArrayList<Contact> contacts){
		this.admin=contacts.get(0);
		
	}

	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String neuerName) {	
		this.groupName=neuerName;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public int getTag() {
		return tag;
	}
	public void setTag(int tag) {
		this.tag = tag;
	}
	public Contact getAdmin() {
		return admin;
	}
	public void setAdmin(Contact admin) {
		this.admin = admin;
	}
	public ArrayList<Contact> getContacts() {
		return contacts;
	}


}
