package userDataManagement;

import java.io.File;

import dataManagement.FileException;

public class DataManagement {

	private MessageDirector messageHandler;
	private FriendList friendList;
	private GroupList groupList;

	public DataManagement(File saveDirectory) {
		if (saveDirectory == null) {
			saveDirectory = new File(System.getProperty("user.dir") + "/MessengerSaves");
			saveDirectory.mkdir();
		}
		if (!saveDirectory.isDirectory())
			new FileException(saveDirectory);
		friendList = new FriendList(saveDirectory);
		groupList = new GroupList(saveDirectory);

	}

}
