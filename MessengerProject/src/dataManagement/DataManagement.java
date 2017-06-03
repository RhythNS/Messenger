package dataManagement;

import java.io.File;
import java.time.LocalTime;

import server.Constants;

public class DataManagement {

	/**
	 * Arguments: 0 Username, 1 Password
	 */
	private ArgumentRandomAccessFile usersArgument;
	private BinaryTreeFile usersTree;
	private FriendList friendList;
	private File userDir;

	/**
	 * Aguments: 0: name
	 */
	private ArgumentRandomAccessFile groupArguments;
	private BinaryTreeFile groupTree;
	private GroupList groupList;
	private File groupDir;

	private DeviceDates devicesDates;
	private final Object usersLock = new Object(), dateLock = new Object(), groupLock = new Object();
	private Thread checkForDelete;

	public boolean usersLoggedIn = false;

	/**
	 * Standard Constructor for DataManagement
	 *
	 * @param saveDirectory
	 *            The Directory in which everything will be saved
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
				Constants.MAX_PASSWORD_SIZE);
		usersTree = new BinaryTreeFile(new File(userDir, "binaryTree.txt"), Constants.MAX_USERNAME_SIZE);
		friendList = new FriendList(new File(userDir, "list.txt"));

		groupArguments = new ArgumentRandomAccessFile(new File(groupDir, "arguments.txt"), Constants.MAX_GROUP_NAME);
		groupTree = new BinaryTreeFile(new File(groupDir, "tree.txt"), Constants.MAX_GROUP_NAME);
		groupList = new GroupList(new File(groupDir, "list.txt"));

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
	}

	/**
	 * Registers a user. If there is already someone with the same username 0 is
	 * returned. Otherwise the tag is returned. It is Important that the
	 * username and password do not go over the length limit!
	 */
	public int registerUser(String username, String password) {
		synchronized (usersLock) {
			if (username != null && password != null && username.length() < Constants.MAX_USERNAME_SIZE + 1
					&& password.length() < Constants.MAX_PASSWORD_SIZE + 1) {
				if (usersTree.getTag(username) == -1) {
					int tag = usersArgument.add(username, password);
					usersTree.add(tag, username);
					friendList.make(tag);
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
			if (tag > 0 && befriendedTag > 0)
				return friendList.addFriend(tag, befriendedTag);
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
			if (tag > 0 && removedFriend > 0)
				return friendList.deleteFriend(tag, removedFriend);
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
					if (groupTree.add(tag, name) && groupList.make(tag, tags))
						return -tag;
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
			System.out.println(userTag + " " + groupTag);
			if (userTag > 0 && groupTag > 0) {
				if (groupList.getAdmin(groupTag) == userTag && groupList.deleteGroup(groupTag)) {
					String groupName = groupArguments.getArgument(groupTag, 0);
					return groupName != null && groupArguments.remove(groupTag) && groupTree.delete(groupName);
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

	public boolean removeFromGroup(int userTag, int groupTag) {
		synchronized (groupLock) {
			groupTag = -groupTag;
			if (userTag > 0 && groupTag > 0)
				return groupList.deleteMember(groupTag, userTag);
			return false;
		}
	}

	public int getGroupAdmin(int groupTag) {
		synchronized (groupLock) {
			groupTag = -groupTag;
			if (groupTag > 0)
				return groupList.getAdmin(groupTag);
			return 0;
		}
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
	 * Checks if it can delete saved messages and files. Shold be called once a
	 * day!
	 */
	private void checkForDelete() {
		// TODO
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
	 *            Date with Format YYYYMMddHHmmss - DeviceDate in DeviceCalc
	 * @param message
	 *            The sent message
	 */
	public void saveMessage(int fromTag, int toTag, String date, String message) {
		//TODO
	}

	/**
	 * Saves a file to the file system. The from tag is the peson who sent the
	 * message. The toTag is to whom the message should be sent. If it is a
	 * group the toTag is the groupTag. The file can not be null! The file
	 * should look like this: [HEADER]\n[DATA]. The header contains the name and
	 * the fileType. The Data contains natrually the data!
	 *
	 * @param fromTag
	 *            From whom the message was sent
	 * @param toTag
	 *            To whom the message is sent
	 * @param date
	 *            Date with Format YYYYMMddHHmmss - DeviceDate in DeviceCalc
	 * @param file
	 *            The sent file
	 */
	public void saveFile(int fromTag, int toTag, String date, String file) {
		// TODO
	}

	/**
	 * Gets all messages from a specific date. IMPORTANT: The date is given when
	 * a device logs in. GetMessages should be called after loginDevice with the
	 * date that is given back in the DeviceLogin!
	 */
	public Mailbox getMessages(int tag, String date) {
		// TODO
		return null;
	}

}
