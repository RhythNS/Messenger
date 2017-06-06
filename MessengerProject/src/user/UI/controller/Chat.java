package user.UI.controller;


import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class Chat {

    public TextArea writingArea;
    public VBox root;
    private user.Chat chat;

    public Chat() {

    }

    public user.Chat getChat() {
        return chat;
    }

    public void setChat(user.Chat chat) {
        this.chat = chat;
    }
}
