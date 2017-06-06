package user.UI.controller;

import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class MainPage {

    public TextField search;
    public VBox friends,groups,pending, request;
    public AnchorPane root;
    private Parent content;

    public MainPage() {

    }

    public void setChat(Chat chat) {
        if (content!=null)
            root.getChildren().remove(this.content);
        AnchorPane.setBottomAnchor(chat.root, .0);
        AnchorPane.setLeftAnchor(chat.root, 202.0);
        AnchorPane.setRightAnchor(chat.root, .0);
        AnchorPane.setTopAnchor(chat.root,40.0);
        this.content = chat.root;
        root.getChildren().add(this.content);
    }

}
