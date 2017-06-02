package dataManagement;

import java.io.File;
import java.util.ArrayList;

import server.Constants;

public class FriendList extends ListFiles {

	FriendList(File file) {
		super(Constants.MAX_FRIENDS, Constants.POINTER_SIZE, file);
	}

	boolean addFriend(int tag, int friendTag) {
		int number = getFirstNull(tag);
		if (number == -1) {
			Logger.getInstance().log("Error FL0: Too many friends. What a problem... #BlameBene");
			return false;
		}
		return set(tag, number, Integer.toString(friendTag, Character.MAX_RADIX));
	}

	boolean deleteFriend(int tag, int friendTag) {
		int position = find(tag, Integer.toString(friendTag, Character.MAX_RADIX));
		if (position == -1) {
			Logger.getInstance().log("Error FL1: Could not find friend. Story of my life. #BlameBene");
			return false;
		}
		return set(tag, position, "");
	}

	int[] getFriends(int tag) {
		String[] friends = getAll(tag);
		ArrayList<Integer> tags = new ArrayList<>();
		for (int i = 0; i < friends.length; i++) {
			if (friends[i] != null && friends[i].length() != 0)
				tags.add(Integer.parseInt(friends[i], Character.MAX_RADIX));
		}
		int[] retTags = new int[tags.size()];
		for (int i = 0; i < retTags.length; i++)
			retTags[i] = tags.get(i);
		return retTags;
	}
}
