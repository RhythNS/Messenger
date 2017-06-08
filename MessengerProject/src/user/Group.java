package user;

import java.util.ArrayList;
import java.util.Date;

import dataManagement.DateCalc;

public class Group {

	private int tag;
	private String groupName;
	private ArrayList<Contact> groupList;
	private ArrayList<Chat> chats;
	private Contact admin;

	public Group(int groupTag, String groupName) {
		this.groupName = groupName;
		this.tag = groupTag;
		this.groupList = new ArrayList<>();
		chats = new ArrayList<>();
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String name) {
		this.groupName = name;
	}

	public int getTag() {
		return tag;
	}

	public Contact getAdmin() {
		return admin;
	}

	public ArrayList<Chat> getChats() {
		return chats;
	}

	public void setAdmin(Contact admin) {
		this.admin = admin;
	}

	public ArrayList<Contact> getGroupList() {
		return groupList;
	}

	public void setGroupList(ArrayList<Contact> groupList) {
		this.groupList = groupList;
		this.admin = groupList.get(0);
	}

	public boolean contains(Contact contact) {
		return groupList.contains(contact);
	}

	public boolean kickUser(int tag) {
		for (Contact c : groupList)
			if (c.getTag() == tag)
				return groupList.remove(c);
		return false;
	}

	public boolean addUser(Contact contact) {
		if (!groupList.contains(contact))
			return groupList.add(contact);
		return true;
	}

	public int getDayNr(Date d) {
		String reqDay = DateCalc.getForYear().format(d);
		for (int i = 0; i < chats.size(); i++)
			if (reqDay.equals(DateCalc.getForYear().format(chats.get(i).getDate())))
				return i;
		return -1;
	}

	public void leave() {
		groupName = null;
		groupList = null;
		chats = null;
		admin = null;
	}

}
