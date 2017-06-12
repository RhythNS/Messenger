package user.UI.controller;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import user.UI.UiHandler;

public class AddFriend {

    public TextField username;
    public Label success;

    public AddFriend() {

    }

    public void add() {
        success.getStyleClass().removeAll(success.getStyleClass());
        if (!username.getText().equals("")) {
            if (UiHandler.getInstance().addFriend(username.getText())) {
                success.getStyleClass().add("success");
                success.setText("friend successfully added");
            } else {
                success.getStyleClass().add("error");
                success.setText("user not found");
            }
        } else {
            success.getStyleClass().add("error");
            success.setText("type in a username first");
        }
    }

}
