package user;

import java.util.ArrayList;
import java.util.Date;

import dataManagement.DateCalc;

public class Group {

	private int tag;
	private String name;
	private ArrayList<Contact> list;
	private ArrayList<Chat> chats;
	private Contact admin;

	public Group(int tag, String name) {
		this.name = name;
		this.tag = tag;
		this.list = new ArrayList<>();
		chats = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public ArrayList<Contact> getList() {
		return list;
	}

	public void setList(ArrayList<Contact> list) {
		this.list = list;
		this.admin = list.get(0);
	}

	public boolean contains(Contact contact) {
		return list.contains(contact);
	}

	public boolean kickUser(int tag) {
		for (Contact c : list)
			if (c.getTag() == tag)
				return list.remove(c);
		return false;
	}

	public boolean addUser(Contact contact) {
		if (!list.contains(contact))
			return list.add(contact);
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
		name = null;
		list = null;
		chats = null;
		admin = null;
	}

	@Override
	public String toString() {
		return "[Group: " + name + ", " + tag + ", " + list + "]";
	}

}
