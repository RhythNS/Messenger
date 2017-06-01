package user;

public class Contact {

	private int tag;
	private String username;

	public Contact(String username, int tag) {
		this.username = username;
		this.tag = tag;
	}

	public int getTag() {
		return tag;
	}

	public String getUsername() {
		return username;
	}
}
