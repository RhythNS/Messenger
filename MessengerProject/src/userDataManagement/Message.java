package userDataManagement;

public class Message {

	int from, to;
	String date;
	String content;
	long pointerFrom, pointerTo;

	public Message(String date, int from, int to) {
		this.date = date;
		this.from = from;
		this.to = to;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	public String getDate() {
		return date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
