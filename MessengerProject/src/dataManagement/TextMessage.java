package dataManagement;

public class TextMessage extends Message{

	private String content;
	long pointerFrom, pointerTo;

	public TextMessage(String date, int from, int to) {
		super(date, from, to);
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}
}
