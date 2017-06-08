package user.UI.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import user.UI.UiHandler;

import java.io.IOException;

public class MainPage {

    public TextField search;
    public VBox friends,groups,pending, request;
    public AnchorPane root;
    private Parent content;

    public MainPage() {
        UiHandler.getInstance().setMainPage(this);
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

    public Parent getChat() {
        return content;
    }

    public void addFriend(Parent parent) {
        friends.getChildren().add(parent);
    }

    public void addPending(Parent pending) {
        this.pending.getChildren().add(pending);
    }

    public void addRequest(Parent request) {
        this.request.getChildren().add(request);
    }

    public void addGroup(Label group) {
        this.groups.getChildren().add(group);
    }

    public void findFriend() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../pages/addFriend.fxml"));
        HBox hBox = loader.load();
        root.getChildren().remove(content);
        content = hBox;
        root.getChildren().add(content);
        AnchorPane.setBottomAnchor(hBox, .0);
        AnchorPane.setLeftAnchor(hBox, 202.0);
        AnchorPane.setRightAnchor(hBox, .0);
        AnchorPane.setTopAnchor(hBox,40.0);
    }

    public void removeContent() {
        root.getChildren().remove(content);
    }
}
