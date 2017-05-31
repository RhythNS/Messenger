package dataManagement;

public class TextMessage extends Message{

	private String content;

	public TextMessage(String date, long pointerFrom, long pointerTo) {
		super(date, pointerFrom, pointerTo);
	}

	void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}
}
