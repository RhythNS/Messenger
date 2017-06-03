package user;

public class Contact {

	
	private String username;
	private int tag;
	private Chat chat;
	
	public Contact(String username,int tag){
		this.username=username;
		this.tag=tag;
		this.chat=new Chat(this);
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
	public void setChat(Chat chat) {
		this.chat = chat;
	}
}