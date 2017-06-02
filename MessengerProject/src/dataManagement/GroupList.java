package dataManagement;

import java.io.File;
import java.util.ArrayList;

import server.Constants;

public class GroupList extends ListFiles {

	GroupList(File file) {
		super(Constants.MAX_GROUP_MEMBERS, Constants.POINTER_SIZE, file);
	}

	boolean make(int tag, int[] tags) {
		if (tags.length < Constants.MAX_GROUP_MEMBERS + 1)
			if (make(tag)) {
				for (int i = 0; i < tags.length; i++) {
					if (!set(tag, i, Integer.toString(tags[i], Character.MAX_RADIX)))
						return false;
				}
				return true;
			}
		return false;
	}

	boolean addMember(int tag, int memberTag) {
		if (!exists(tag)) {
			Logger.getInstance().log("Error FL2: This group does not exist! #BlameBene");
			return false;
		}
		int number = getFirstNull(tag);
		if (number == -1) {
			Logger.getInstance().log("Error FL0: Too many friends. What a problem... #BlameBene");
			return false;
		}
		return set(tag, number, Integer.toString(memberTag, Character.MAX_RADIX));
	}

	boolean deleteMember(int tag, int memberTag) {
		if (!exists(tag)) {
			Logger.getInstance().log("Error FL3: This group does not exist! #BlameBene");
			return false;
		}
		int position = find(tag, Integer.toString(memberTag, Character.MAX_RADIX));
		if (position == -1) {
			Logger.getInstance().log("Error FL1: Could not find that tag. #BlameBene");
			return false;
		}
		return set(tag, position, "");

	}

	int getAdmin(int tag) {
		if (!exists(tag)) {
			Logger.getInstance().log("Error FL4: This group does not exist! #BlameBene");
			return 0;
		}
		String sTag = get(tag, 0);
		return (sTag != null && sTag.length() != 0) ? Integer.parseInt(sTag, Character.MAX_RADIX) : 0;
	}

	boolean inGroup(int tag, int memberTag) {
		if (!exists(tag)) {
			Logger.getInstance().log("Error FL7: This group does not exist! #BlameBene");
			return false;
		}
		return find(tag, Integer.toString(memberTag, Character.MAX_RADIX)) != -1;
	}

	int[] getTags(int tag) {
		if (!exists(tag)) {
			Logger.getInstance().log("Error FL5: This group does not exist! #BlameBene");
			return null;
		}
		String[] members = getAll(tag);
		ArrayList<Integer> tags = new ArrayList<>();
		for (int i = 0; i < members.length; i++) {
			if (members[i] != null && members[i].length() != 0)
				tags.add(Integer.parseInt(members[i], Character.MAX_RADIX));
		}
		int[] retTags = new int[tags.size()];
		for (int i = 0; i < retTags.length; i++)
			retTags[i] = tags.get(i);
		return retTags;
	}

	boolean deleteGroup(int tag) {
		if (!exists(tag)) {
			Logger.getInstance().log("Error FL6: This group does not exist! #BlameBene");
			return false;
		}
		return deleteAll(tag);
	}

	boolean exists(int tag) {
		String sTag = get(tag, 0);
		return sTag != null && sTag.length() != 0;
	}

}
