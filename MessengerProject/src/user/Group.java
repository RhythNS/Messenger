package user;

import java.util.ArrayList;

public class Group {

	
	//TODO publicKey and Tag?
	
	private String publicKey;
	private int tag;
	private String groupName;
	private Contact admin;
	private ArrayList<Contact> groupList;
	private Chat chat;
	
	public Group(String groupName,ArrayList<Contact> groupList){
		this.groupName=groupName;
		this.admin=groupList.get(0);
		this.groupList=groupList;
		this.chat=new Chat(this);
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
	public ArrayList<Contact> getGroupList() {
		return groupList;
	}
	public Chat getChat() {
		return chat;
	}
	public void kickUser(int tag){
		for (Contact c : groupList) {
			if(c.getTag()==tag){
				groupList.remove(c);
			}
		}
	}
}
