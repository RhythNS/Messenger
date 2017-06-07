package user;

import java.util.ArrayList;
import java.util.Collections;

public class Chat {

	private String date;
    private ArrayList<Message> messages;

    public Chat(ArrayList<Message> messages, String date) {
        this.messages = messages;
        this.date = date;
        Collections.sort(messages);
    }

    public void addMessage(Message newMessage) {
    	if (newMessage != null)
    		messages.add(newMessage);
    }

    public String getDate() {
		return date;
	}

}
