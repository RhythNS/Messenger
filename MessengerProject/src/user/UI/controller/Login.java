package user.UI.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import user.UI.Main;
import user.UI.UiHandler;

import java.io.IOException;

public class Login {

    public TextField username;
    public PasswordField password;
    public CheckBox remindMe;
    private UiHandler uiHandler;

    public Login() {
        uiHandler = UiHandler.getInstance();
    }

    public void switchToRegister() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../pages/register.fxml"));
        Parent root = loader.load();
        Main.switchScene(root);
    }

    public void confirm() throws IOException {
        boolean login = uiHandler.logIn(username.getText(), password.getText());
        if (login) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../pages/mainPage.fxml"));
            Parent root = loader.load();
            Main.switchScene(root);
        } else {
            username.getStyleClass().add("wrong");
            password.getStyleClass().add("wrong");
        }
    }

}
