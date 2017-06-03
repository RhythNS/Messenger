package dataManagement;

public abstract class Message {

	int from, to;
	String date;

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

}
