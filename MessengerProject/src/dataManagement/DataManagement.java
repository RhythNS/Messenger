package dataManagement;

import java.io.File;
import java.time.LocalTime;

import server.Constants;

/**
 * The stuff that Noah did <(^_^<
 *
 * @author RhythNS_
 */
public class DataManagement {

	// USER stuff:
	/**
	 * Arguments: 0 Username, 1 Password, 2 Color
	 */
	private ArgumentRandomAccessFile usersArgument;
	private BinaryTreeFile usersTree;
	private FriendList friendList, usersGroupList, pendingList, requestList;
	private File userDir;

	// GROUP stuff:
	/**
	 * Aguments: 0: name
	 */
	private ArgumentRandomAccessFile groupArguments;
	private BinaryTreeFile groupTree;
	private GroupList groupList;
	private File groupDir;

	private MessageDirector messageDirector;

	private DeviceDates devicesDates;

	// Locks for SYNC Methods
	private final Object usersLock = new Object(), dateLock = new Object(), groupLock = new Object(),
			userGroupLock = new Object();

	// Cleaning variables
	private Thread checkForDelete;
	private int daysNotCleaned;

	/**
	 * Standard Constructor for DataManagement
	 *
	 * @param saveDirectory
	 *            The Directory in which everything will be saved. If null the
	 *            default directory is used
	 */
	public DataManagement(File saveDirectory) {
		if (saveDirectory == null) {
			saveDirectory = new File(System.getProperty("user.dir") + "/MessengerSaves");
			saveDirectory.mkdir();
		}
		if (!saveDirectory.isDirectory())
			new FileException(saveDirectory);

		File logDir = new File(saveDirectory, "logs");
		logDir.mkdirs();
		Logger.getInstance().setFile(logDir);

		userDir = new File(saveDirectory, "users");
		userDir.mkdirs();
		groupDir = new File(saveDirectory, "groups");
		groupDir.mkdirs();

		usersArgument = new ArgumentRandomAccessFile(new File(userDir, "arguments.txt"), Constants.MAX_USERNAME_SIZE,
				Constants.MAX_PASSWORD_SIZE, 5);
		usersTree = new BinaryTreeFile(new File(userDir, "binaryTree.txt"), Constants.MAX_USERNAME_SIZE);
		friendList = new FriendList(new File(userDir, "friendlist.txt"));
		usersGroupList = new FriendList(new File(userDir, "grouplist.txt"));
		pendingList = new FriendList(new File(userDir, "pendinglist.txt"));
		requestList = new FriendList(new File(userDir, "requestlist.txt"));

		groupArguments = new ArgumentRandomAccessFile(new File(groupDir, "arguments.txt"), Constants.MAX_GROUP_NAME);
		groupTree = new BinaryTreeFile(new File(groupDir, "tree.txt"), Constants.MAX_GROUP_NAME);
		groupList = new GroupList(new File(groupDir, "list.txt"));

		messageDirector = new MessageDirector(saveDirectory);

		devicesDates = new DeviceDates(saveDirectory);

		checkForDelete = new Thread(new Runnable() {
			@Override
			public void run() {
				LocalTime lt = LocalTime.now();
				int time = lt.toSecondOfDay(), time2 = time;
				while (true) {
					time = lt.toSecondOfDay();
					if (time < time2) {
						checkForDelete();
					}
					time2 = time;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						Logger.getInstance().log("Error TCFD0: InterrupptedException! #BlameBene");
						e.printStackTrace();
					}
					lt = LocalTime.now();
				}
			}
		});
		checkForDelete.start();
		daysNotCleaned = Constants.MINIMAL_DAYS_UNTIL_FILE_CLEANING + 1;
		cleanFiles();
	}

	/**
	 * Informs DataManagment wheter a user is logged in or not. This is done so
	 * if files need cleaning or are being removed, it is done while there are
	 * no users logged in
	 */
	public void noUsersLoggedIn() {
		cleanFiles();
	}

	/**
	 * Registers a user. If there is already someone with the same username 0 is
	 * returned. Otherwise the tag is returned. It is Important that the
	 * username and password do not go over the length limit!
	 */
	public int registerUser(String username, String password, String color) {
		synchronized (usersLock) {
			if (username != null && password != null && username.length() < Constants.MAX_USERNAME_SIZE + 1
					&& password.length() < Constants.MAX_PASSWORD_SIZE + 1 && color != null) {
				if (usersTree.getTag(username) == -1) {
					int tag = usersArgument.add(username, password, color);
					usersTree.add(tag, username);
					friendList.make(tag);
					synchronized (userGroupLock) {
						usersGroupList.make(tag);
					}
					return tag;
				}
			}
			return 0;
		}
	}

	/**
	 * Tries to login a user. If succeeded true is returned. If failed false is
	 * returned!
	 */
	public boolean login(int tag, String password) {
		synchronized (usersLock) {
			if (tag > 0 && password != null && password.length() < Constants.MAX_PASSWORD_SIZE + 1)
				return usersArgument.isArgumentCorrect(tag, 1, password);
			return false;
		}
	}

	/**
	 * Tries to login a user. If succeeded the tag is returned. If failed -1 is
	 * returned!
	 */
	public int login(String username, String password) {
		synchronized (usersLock) {
			if (username != null && password != null && username.length() < Constants.MAX_USERNAME_SIZE + 1
					&& password.length() < Constants.MAX_PASSWORD_SIZE + 1) {
				int tag = usersTree.getTag(username);
				if (usersArgument.isArgumentCorrect(tag, 1, password))
					return tag;
			}
			return -1;
		}
	}

	/**
	 * Gets a color of a user. Can be null if an error occurred or tag was not
	 * found!
	 *
	 * @param tag
	 *            The requested tag
	 * @return the color can be null
	 */
	public String getColor(int tag) {
		if (tag > 0)
			synchronized (usersLock) {
				String arg = usersArgument.getArgument(tag, 2);
				if (arg == null)
					return null;
				int value = Integer.parseInt(arg, Character.MAX_RADIX);
				return Integer.toHexString(value);
			}
		return null;
	}

	/**
	 * Logout needs to be called when a Device logs itself out or looses
	 * connection!
	 *
	 * @param timeout
	 *            Wheter the Device had a timeout(true) or logged out(false)
	 */
	public void logout(int tag, int deviceNumber, boolean timeout) {
		synchronized (dateLock) {
			if (tag > 0)
				devicesDates.logout(tag, deviceNumber, timeout);
		}
	}

	/**
	 * Adds a tag to a tags friendlist. Returns wheter it successeded or not!
	 *
	 * @param tag
	 *            The Person who sent the request
	 * @param befriendedTag
	 *            The Person to whom the request was sent
	 */
	public boolean addFriend(int tag, int befriendedTag) {
		synchronized (usersLock) {
			if (tag > 0 && befriendedTag > 0) {
				if (pendingList.inList(befriendedTag, tag)) {
					pendingList.deleteFriend(befriendedTag, tag);
					requestList.deleteFriend(tag, befriendedTag);
					friendList.addFriend(tag, befriendedTag);
					return friendList.addFriend(befriendedTag, tag);
				} else {
					pendingList.addFriend(tag, befriendedTag);
					requestList.addFriend(befriendedTag, tag);
				}
			}
			return false;
		}
	}

	/**
	 * Removes a friend from a tag. Returns wheter it successeded or not!
	 *
	 * @param tag
	 *            The Person who wants someone to be removed!
	 * @param removedFriend
	 *            The Person who is going to be removed!
	 */
	public boolean removeFriend(int tag, int removedFriend) {
		synchronized (usersLock) {
			if (tag > 0 && removedFriend > 0) {
				if (friendList.inList(tag, removedFriend)) {
					friendList.deleteFriend(removedFriend, tag);
					return friendList.deleteFriend(tag, removedFriend);
				} else if (pendingList.inList(tag, removedFriend)) {
					pendingList.deleteFriend(tag, removedFriend);
					return requestList.deleteFriend(removedFriend, tag);
				} else {
					pendingList.deleteFriend(removedFriend, tag);
					return requestList.deleteFriend(tag, removedFriend);
				}
			}
			return false;
		}
	}

	/**
	 * Gets all friends from a tag. Can return null if something went wrong or
	 * no friends were found. *Cough Bene Cough*
	 *
	 * @param tag
	 *            The person who wants to have friends!
	 */
	public int[] getFriends(int tag) {
		synchronized (usersLock) {
			if (tag > 0)
				return friendList.getFriends(tag);
			return null;
		}
	}

	/**
	 * Gets the group Name. Can return null if no group with that tag has been
	 * found!
	 *
	 * @param tag
	 *            The Tag of the group.
	 */
	public String getGroupName(int tag) {
		synchronized (groupLock) {
			if (tag < 0) {
				tag = -tag;
				return groupArguments.getArgument(tag, 0);
			}
			return null;
		}
	}

	/**
	 * Gets the group tag. Can return 0 if no group with that name has been
	 * found!
	 *
	 * @param name
	 *            The name of the group
	 */
	public int getGroupTag(String name) {
		synchronized (groupLock) {
			if (name != null && name.length() != 0) {
				return -groupTree.getTag(name);
			}
			return 0;
		}
	}

	/**
	 * Creates a new group with the given name and the tags in a integer arraay.
	 * Returns the new group tag. If failed 0 is returned!
	 *
	 * @param name
	 *            The Name of the group
	 * @param tags
	 *            that were initially invited to the group. Should be like
	 *            2,5,824,235. The first tag is the creator/ admin!
	 */
	public int createGroup(String name, int[] tags) {
		synchronized (groupLock) {
			if (tags != null && name != null && name.length() != 0) {
				int tag = groupTree.getTag(name);
				if (tag == -1) {
					tag = groupArguments.add(name);
					if (groupTree.add(tag, name) && groupList.make(tag, tags)) {
						synchronized (userGroupLock) {
							for (int i = 0; i < tags.length; i++) {
								usersGroupList.addFriend(tags[i], -tag);
							}
						}
						return -tag;
					}
				}
			}
		}
		return -1;
	}

	/**
	 * Deletes a group with the given Tag. Returns if the deletions successeded.
	 *
	 * @param userTag
	 *            The Tag from the user that tried to delete the group
	 * @param groupTag
	 *            The Tag from the group
	 */
	public boolean deleteGroup(int userTag, int groupTag) {
		synchronized (groupLock) {
			groupTag = -groupTag;
			if (userTag > 0 && groupTag > 0 && groupList.getAdmin(groupTag) == userTag) {
				int[] tags = groupList.getTags(groupTag);
				if (tags != null && groupList.deleteGroup(groupTag)) {
					String groupName = groupArguments.getArgument(groupTag, 0);
					if (groupName != null && groupArguments.remove(groupTag) && groupTree.delete(groupName)) {

						synchronized (userGroupLock) {
							for (int i = 0; i < tags.length; i++) {
								usersGroupList.deleteFriend(tags[i], groupTag);
							}
						}
						return true;
					}
				}
			}
			return false;
		}
	}

	/**
	 * Gets the tags and usernames of the members of a group. Can return null if
	 * no group has been found.
	 *
	 * @param group
	 *            the tag of the group
	 */
	public int[] getGroupMembers(int groupTag) {
		synchronized (groupLock) {
			groupTag = -groupTag;
			if (groupTag > 0)
				return groupList.getTags(groupTag);
			return null;
		}
	}

	/**
	 * Returns wheter a User is in a group or not
	 *
	 * @param userTag
	 *            The Tag from the user
	 * @param groupTag
	 *            The Tag from the group
	 * @return
	 */
	public boolean isInGroup(int userTag, int groupTag) {
		synchronized (groupLock) {
			groupTag = -groupTag;
			if (userTag > 0 && groupTag > 0) {
				return groupList.inGroup(groupTag, userTag);
			}
			return false;
		}
	}

	/**
	 * Removes a user from a group. Returns wheter it successeded or not.
	 *
	 * @param userTag
	 *            The Tag of the user that should be kicked
	 * @param groupTag
	 *            The Tag of the group
	 * @return
	 */
	public boolean removeFromGroup(int userTag, int groupTag) {
		synchronized (groupLock) {
			groupTag = -groupTag;
			if (userTag > 0 && groupTag > 0 && groupList.deleteMember(groupTag, userTag)) {
				synchronized (userGroupLock) {
					usersGroupList.deleteFriend(userTag, groupTag);
				}
				return true;
			}
			return false;
		}
	}

	/**
	 * Gets an Admin of a group. Returns 0 if no group has been found!
	 *
	 * @param groupTag
	 *            The tag of the group
	 * @return
	 */
	public int getGroupAdmin(int groupTag) {
		synchronized (groupLock) {
			groupTag = -groupTag;
			if (groupTag > 0)
				return groupList.getAdmin(groupTag);
			return 0;
		}
	}

	public int[] getGroupTags(int tag) {
		if (tag > 0)
			synchronized (userGroupLock) {
				int[] tags = usersGroupList.getFriends(tag);
				for (int i = 0; i < tags.length; i++) {
					tags[i] = -tags[i];
				}
				return tags;
			}
		return null;
	}

	/**
	 * Logs in a Device. Returns the devices' new number. Important returns null
	 * if the input is wrong!
	 */
	public DeviceLogin loginDevice(int tag, int device) {
		synchronized (dateLock) {
			if (tag > 0)
				return devicesDates.login(tag, device);
			return null;
		}
	}

	/**
	 * Checks if it can delete saved messages and files. Also adds a day to the
	 * messageDirector. Shold be called once a day!
	 */
	private void checkForDelete() {
		messageDirector.addDay();
		daysNotCleaned++;
		if (daysNotCleaned > Constants.MAXIMAL_DAYS_UNTIL_FILE_CLEANING)
			cleanFiles();
	}

	/**
	 * Refreshes all files that need cleaning
	 */
	private synchronized void cleanFiles() {
		if (daysNotCleaned > Constants.MINIMAL_DAYS_UNTIL_FILE_CLEANING) {
			daysNotCleaned = 0;
			usersTree = usersTree.refresh();
			groupTree = groupTree.refresh();
		}
	}

	/**
	 * Saves a message to the file system. The from tag is the peson who sent
	 * the message. The toTag is to whom the message should be sent. If it is a
	 * group the toTag is the groupTag. The message can not be null!
	 *
	 * @param fromTag
	 *            From whom the message was sent
	 * @param toTag
	 *            To whom the message is sent
	 * @param date
	 *            Date with Format YYYYMMddHHmmss - DeviceDate in DateCalc
	 * @param message
	 *            The sent message
	 */
	public boolean saveMessage(int fromTag, int toTag, String date, String message) {
		if (fromTag > 0 && toTag != 0 && date != null && date.length() == 14 && message != null
				&& message.length() != 0) {
			return messageDirector.writeMessage(date, fromTag, toTag, message);
		}
		return false;
	}

	/**
	 * Saves a file to the file system. The from tag is the peson who sent the
	 * message. The toTag is to whom the message should be sent. If it is a
	 * group the toTag is the groupTag. The file can not be null! The file
	 * should look like this: [HEADER]\n[DATA]. The header contains the name and
	 * the fileType. The Data contains naturally the data!
	 *
	 * @param fromTag
	 *            From whom the message was sent
	 * @param toTag
	 *            To whom the message is sent
	 * @param date
	 *            Date with Format YYYYMMddHHmmss - DeviceDate in DateCalc
	 * @param file
	 *            The sent file
	 */
	public boolean saveFile(int fromTag, int toTag, String date, String file) {
		if (fromTag > 0 && toTag != 0 && date != null && date.length() == 14 && file != null && file.length() != 0) {
			return messageDirector.writeFile(date, fromTag, toTag, file);
		}
		return false;
	}

	/**
	 * Gets all messages from a specific date. IMPORTANT: The date is given when
	 * a device logs in. GetMessages should be called after loginDevice with the
	 * date that is given back in the DeviceLogin!
	 */
	public Mailbox getMessages(int tag, String date) {
		if (tag > 0 && date != null && date.length() != 0) {
			int[] groupTags = getGroupTags(tag);
			Mailbox mb = messageDirector.getMessages(tag, date, groupTags);
			for (int i = 0; i < groupTags.length; i++) {
				int[] tags = getGroupMembers(groupTags[i]);
				if (tags == null)
					continue;
				mb.groupTransfers.add(new GroupTransfer(groupTags[i], tags, getGroupName(groupTags[i])));
			}
			synchronized (usersLock) {
				int[] tags = pendingList.getFriends(tag);
				if (tags != null)
					for (int i = 0; i < tags.length; i++)
						mb.pending.add(tags[i]);
				tags = requestList.getFriends(tag);
				if (tags != null)
					for (int i = 0; i < tags.length; i++)
						mb.requests.add(tags[i]);
				tags = friendList.getFriends(tag);
				if (tags != null)
					for (int i = 0; i < tags.length; i++) {
						mb.friends.add(tags[i]);
						String color = usersArgument.getArgument(tags[i], 2);
						if (color == null)
							continue;
						int col = Integer.parseInt(color, Character.MAX_RADIX);
						mb.colors.add(new ColorTransfer(Integer.toHexString(col), tag));
					}
			}

			return mb;
		}
		return null;
	}

}
