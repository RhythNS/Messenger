package dataManagement;

public class UserInfo {

	int tag;
	String username, color;

	public UserInfo(int tag, String username, String color) {
		this.tag = tag;
		this.username = username;
		this.color = color;
	}

	public String getColor() {
		return color;
	}

	public int getTag() {
		return tag;
	}

	public String getUsername() {
		return username;
	}
}
