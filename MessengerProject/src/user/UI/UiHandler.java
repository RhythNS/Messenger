package user.UI;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import user.Client;
import user.Contact;
import user.Group;
import user.UI.controller.Chat;
import user.UI.controller.MainPage;
import user.User;

import java.io.IOException;
import java.util.ArrayList;

public class UiHandler {

    private MainPage mainPage;
    private Main main;
    private static UiHandler instance;
    private User u;
    private Client client;
    private boolean login;


    private UiHandler() {
        login = true;
    }

    public static UiHandler getInstance() {
        if (instance == null)
            instance = new UiHandler();
        return instance;
    }

    public void setChat(int tag) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("pages/mainPage.fxml"));
        Parent root = loader.load();
        Chat chat = loader.getController();
        mainPage.setContent(chat.root);
    }

    public void changedList() {
        try {
            loadChatlist();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("updated");
    }

    public boolean logIn(String username,String password) throws IOException {
        u = new User();
        boolean login = u.login(username, password);
        if (!login)
            u = null;
        else
            Main.setTitle(username);
        return login;
    }

    public boolean register(String username, String password, String color) throws IOException {
        u = new User();
        System.out.println(color);
        boolean register = u.register(username, password, color);
        if (!register)
            u = null;
        else
            Main.setTitle(username);
        return register;
    }

    public void messageReceived(int tag) {
        Parent content = mainPage.getChat();
    }

    public void loadChatlist() throws IOException {
        ArrayList<Contact> contacts = u.getFriendlist();
        ArrayList<Contact> pendings = u.getPendingFriends();
        ArrayList<Contact> requests = u.getRequestedFriends();
        ArrayList<Group> groups = u.getGroups();
        mainPage.clearLists();
        for (Contact c : contacts) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("pages/contact.fxml"));
            Parent friend = loader.load();
            user.UI.controller.Contact contact = loader.getController();
            contact.setContact(c, user.UI.controller.Contact.Type.FRIEND);
            mainPage.addFriend(friend);
        }
        for (Contact c: pendings) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("pages/contact.fxml"));
            Parent pending = loader.load();
            user.UI.controller.Contact contact = loader.getController();
            contact.setContact(c, user.UI.controller.Contact.Type.PENDING);
            mainPage.addPending(pending);
        }
        for (Contact c: requests) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("pages/contact.fxml"));
            Parent request = loader.load();
            user.UI.controller.Contact contact = loader.getController();
            contact.setContact(c, user.UI.controller.Contact.Type.REQUEST);
            mainPage.addRequest(request);
        }
        for (Group g:groups) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("pages/Group.fxml"));
            Label group = loader.load();
            user.UI.controller.Group groupController = loader.getController();
            groupController.setGroup(g);
            mainPage.addGroup(group);
        }
        System.out.println(contacts);
        System.out.println(pendings);
        System.out.println(requests);
        System.out.println(groups);
    }

    public void setMainPage(MainPage mainPage) {
        this.mainPage = mainPage;
    }

    public boolean addFriend(String username) {
        Contact c = u.findFriend(username);
        if (c != null) {
            u.sendFriendRequest(c);
            return true;
        }
        return false;
    }

    public void disconnect() {
        if (u != null) {
            u.disconnect();
        }
        System.exit(0);
    }

    public MainPage getMainPage() {
        return mainPage;
    }

    public void replyFriendRequest(Contact contact, boolean accept) {

    }
}
