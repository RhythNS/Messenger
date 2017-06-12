package user.UI.controller;

import javafx.scene.control.Label;
import user.UI.UiHandler;

public class ReplyFriendRequest {

    public Label username;

    public user.Contact contact;

    public ReplyFriendRequest() {

    }

    public void accept() {
        UiHandler.getInstance().replyFriendRequest(contact, true);
    }

    public void decline() {
        UiHandler.getInstance().replyFriendRequest(contact, false);
    }

    public void setContact(user.Contact contact) {
        this.contact = contact;
        username.setText(contact.getUsername());
        username.setStyle("-fx-background-color: #"+contact.getColor());
    }
}
