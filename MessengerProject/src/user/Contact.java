package user;

public class Contact {

	private String username, color;
	private int tag;
	private Chat chat;

	public Contact(String username, String color, int tag) {
		this.username = username;
		this.tag = tag;
		this.color = color;
	}

	public String getUsername() {
		return username;
	}

	public int getTag() {
		return tag;
	}

	public Chat getChat() {
		return chat;
	}

	public String getColor() {
		return color;
	}
}