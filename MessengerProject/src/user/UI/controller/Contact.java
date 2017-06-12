package user.UI.controller;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import user.UI.UiHandler;

import java.io.IOException;

public class Contact {

    public Label name;
    public Pane color;
    public AnchorPane root;
    private user.Contact contact;
    private Type type;
    private boolean selected;

    public Contact() {

    }

    public void select(Event event) throws IOException {
        System.out.println(event.getSource());
        if (!selected) {
            selected = true;
            root.getStyleClass().add("selected");
            switch (type) {
                case FRINED:
                    UiHandler.getInstance().setChat(contact.getTag());
                    UiHandler.getInstance().getMainPage().setSelected(root, this);
                    break;
                case PENDING:
                    Label l = new Label("User did not reply your request");
                    l.setAlignment(Pos.CENTER);
                    UiHandler.getInstance().getMainPage().setContent(l);
                    UiHandler.getInstance().getMainPage().setSelected(root, this);
                    break;
                case REQUEST:
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../pages/replyFriendRequest.fxml"));
                    VBox root = loader.load();
                    ((ReplyFriendRequest)loader.getController()).setContact(contact);
                    UiHandler.getInstance().getMainPage().setContent(root);
                    UiHandler.getInstance().getMainPage().setSelected(root, this);
                    break;
            }
        } else {
            unselect();
            UiHandler.getInstance().getMainPage().unselect();
        }
    }

    public void unselect() {
        selected = false;
        root.getStyleClass().remove("selected");
    }

    public user.Contact getContact() {
        return contact;
    }

    public void setContact(user.Contact contact,Type type) {
        this.contact = contact;
        name.setText(contact.getUsername());
        color.setStyle("-fx-background-color: #"+contact.getColor());
        this.type = type;
    }

    public enum  Type{
        FRINED,PENDING, REQUEST
    }

}