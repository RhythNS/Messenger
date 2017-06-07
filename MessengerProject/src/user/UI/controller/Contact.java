package user.UI.controller;

import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import user.UI.UiHandler;

import java.io.IOException;

public class Contact {

    public Label name;
    public Rectangle color;
    private user.Contact contact;

    public Contact() {

    }

    public void select() throws IOException {
        UiHandler.getInstance().setChat(contact.getTag());
    }

    public user.Contact getContact() {
        return contact;
    }

    public void setContact(user.Contact contact) {
        this.contact = contact;
        name.setText(contact.getUsername());
    }
}
