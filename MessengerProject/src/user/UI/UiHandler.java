package user.UI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import user.Client;
import user.UI.controller.Chat;
import user.UI.controller.MainPage;
import user.User;

import java.io.IOException;

public class UiHandler {

    private MainPage mainPage;
    private Main main;
    private static UiHandler instance;
    private User user;
    private Client client;
    private boolean login;

    private UiHandler() {
        login = false;
    }

    public static UiHandler getInstance() {
        if (instance == null)
            instance = new UiHandler();
        return instance;
    }

    public void setChat(int tag) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../pages/mainPage.fxml"));
        Parent root = loader.load();
        Chat chat = loader.getController();
        mainPage.setChat(chat);
    }

    public boolean logIn(String username,String password) throws IOException {
        user = new User(username);
        //boolean login = user.login(username, password);
        if (!login)
            user = null;
        return login;
    }

    public boolean register(String username, String password, String color) throws IOException {
        user = new User(username);
        boolean register = user.register(username, password, color);
        if (!register)
            user = null;
        return register;
    }



}
