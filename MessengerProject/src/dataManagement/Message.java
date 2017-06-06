package dataManagement;

public class Message {

	int from, to;
	long pointerFrom, pointerTo;
	String date;
	String content;

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
