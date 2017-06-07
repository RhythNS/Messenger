package user;

import java.util.ArrayList;

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
}