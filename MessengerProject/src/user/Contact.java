package user;

import java.util.ArrayList;
import java.util.Date;

import dataManagement.DateCalc;

public class Contact {

	private String username, color;
	private int tag;
	private ArrayList<Chat> chats;

	public Contact(String username, String color, int tag) {
		this.username = username;
		this.tag = tag;
		this.color = color;
		chats = new ArrayList<>();
	}

	public Contact(int tag) {
		this.tag = tag;
		chats = new ArrayList<>();
	}

	public String getUsername() {
		return username;
	}

	public int getTag() {
		return tag;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getColor() {
		return color;
	}

	public ArrayList<Chat> getChats() {
		return chats;
	}

	public int getDayNr(Date d) {
		String reqDay = DateCalc.getForYear().format(d);
		for (int i = 0; i < chats.size(); i++)
			if (reqDay.equals(DateCalc.getForYear().format(chats.get(i).getDate())))
				return i;
		return -1;
	}

	@Override
	public String toString() {
		return "{Contact: " + username + ", " + color + ", " + tag + "}";
	}
}