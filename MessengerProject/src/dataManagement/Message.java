package dataManagement;

public abstract class Message {

	int from, to;
	String date;
	long pointerFrom, pointerTo;

	public Message(String date, long pointerFrom, long pointerTo) {
		this.date = date;
		this.pointerFrom = pointerFrom;
		this.pointerTo = pointerTo;
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
