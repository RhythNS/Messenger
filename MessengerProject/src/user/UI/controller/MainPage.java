package user.UI.controller;

import javafx.application.Platform;
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
	public VBox friends, groups, pending, request;
	public AnchorPane root;
	private Parent content;
	private Contact selectedContact;
	private Parent selected;

	public MainPage() {
		UiHandler.getInstance().setMainPage(this);
	}

	public void setContent(Parent content) {
		if (content != null)
			root.getChildren().remove(this.content);
		AnchorPane.setBottomAnchor(content, .0);
		AnchorPane.setLeftAnchor(content, 202.0);
		AnchorPane.setRightAnchor(content, .0);
		AnchorPane.setTopAnchor(content, 40.0);
		this.content = content;
		root.getChildren().add(this.content);
	}

	public Parent getChat() {
		return content;
	}

	public void addFriend(Parent parent) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				friends.getChildren().add(parent);
			}
		});
	}

	public void addPending(Parent parent) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				pending.getChildren().add(parent);
			}
		});
	}

	public void addRequest(Parent parent) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				request.getChildren().add(parent);
			}
		});
	}

	public void addGroup(Label label) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				groups.getChildren().add(label);
			}
		});
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
		AnchorPane.setTopAnchor(hBox, 40.0);
	}

	public void clearLists() {
		friends.getChildren().removeAll(friends.getChildren());
		pending.getChildren().removeAll(pending.getChildren());
		request.getChildren().removeAll(request.getChildren());
		groups.getChildren().removeAll(groups.getChildren());
	}

	public void removeContent() {
		root.getChildren().remove(content);
	}

	public Parent getSelected() {
		return selected;
	}

	public Contact getSelectedContact() {
		return selectedContact;
	}

	public void setSelected(Parent selected, Contact selectedContact) {
		if (this.selectedContact != null)
			this.selectedContact.unselect();
		this.selected = selected;
		this.selectedContact = selectedContact;
	}

	public void unselect() {
		selected = null;
		selectedContact = null;
		removeContent();
	}
}
