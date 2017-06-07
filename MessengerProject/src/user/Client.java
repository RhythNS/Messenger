package user;

import socketio.Socket;
import userDataManagement.DateCalc;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.PublicKey;

public class Client implements Runnable {

	private Socket socket;
	private final Object userLock = new Object();
	private User user;
	private boolean connected, isLoggedIn;

	public Client(String host, int port, User user) {
		socket = new Socket(host, port);
		this.user = user;
	}

	public boolean connect() {
		return socket.connect();
	}

	/**
	 * Returns 0 if the registration process failed. Otherwise the tag is
	 * returned
	 */
	public int register(String username, String password, String color) throws IOException {
		if (!connect()) {
			System.err.println("Could not connect!");
			return 0;
		}
		// TODO Generate public and private Key

		PublicKey userPublicKey = null;
		write("KEY", "", userPublicKey.toString());
		String synchronisedKey = getMessage(socket.readLine());
		// TODO: decode Key
		// TODO: generate synchronised Key

		write("REG", color, username + "," + password);

		String response = read();

		if (getHeader(response).equals("OK")) {
			user.setDeviceNumber(Integer.parseInt(getInfo(response)));
			connected = true;
			Thread t = new Thread(this);
			t.start();
			return Integer.parseInt(getMessage(response));
		}
		return 0;
	}

	public boolean login(String username, String password, int deviceNr) throws IOException {
		if (!connect()) {
			System.err.println("Could not connect!");
			return false;
		}
		/*
		 * // TODO Generate public and private Key
		 *
		 * PublicKey userPublicKey = null; write("KEY","",
		 * userPublicKey.toString()); String synchronisedKey =
		 * getMessage(socket.readLine()); // TODO: decode Key // TODO: generate
		 * synchronised Key
		 *
		 * // TODO HASH PASSWORD
		 */
		write("LOG", deviceNr + "", username + "," + password);
		String response = read();

		if (getHeader(response).equals("OK")) {
			user.setDeviceNumber(Integer.parseInt(getInfo(response)));
			connected = true;
			Thread t = new Thread(this);
			t.start();
			return true;
		} else {
			socket.close();
		}
		return false;
	}

	public void disconnect() {
		write("D", "", "");
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void write(String header, String info, String message) {
		//todo: Encryption
		try {
			socket.write(header + "/" + info + "/" + message + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean send(byte[] bytes) throws IOException {
		//todo: Encryption
		byte checksum = 0;
		for (byte b : bytes) {
			checksum ^= b;
		}
		write("DATA", bytes.length + "", checksum + "");
		socket.getOutputStream().write(bytes);
		if (getHeader(read()).equals("OK"))
			return true;
		else
			return false;
	}

	private String getHeader(String message) {
		return message.split("/", 3)[0];
	}

	private String getInfo(String message) {
		return message.split("/", 3)[1];
	}

	private String getMessage(String message) {
		return message.split("/", 3)[2];
	}

	private String read() {
		//todo: Decryption
		try {
			return socket.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * writes a message to an other User
	 *
	 * @param tag
	 *            tag of the User
	 * @param message
	 */
	public void writeMessage(int tag, String message) {
		synchronized (userLock) {
			write("MSG", tag + "", message);
		}
	}

	/**
	 * sends a data stream to an other User
	 *
	 * @param tag
	 * @param filename
	 * @param bytes
	 * @return returns false if file size is to big
	 * @throws IOException
	 */
	public boolean sendData(int tag, String filename, byte[] bytes) throws IOException {
		synchronized (userLock) {
			if (bytes.length <= 1048576) {
				write("DATA", tag + "", filename);
				while (!send(bytes))
					;
				write("EOT", "", "");
				return true;
			}
			return false;
		}
	}

	/**
	 * search for an other User
	 *
	 * @param username
	 * @return returns a contact if user is found else returns null
	 */
	public Contact searchUser(String username) {
		synchronized (userLock) {
			write("SU", "", username);
			String response = read();
			Contact contact = null;
			String color = getInfo(response);
			if (getHeader(response).equals("OK"))
				contact = new Contact(username, color, Integer.parseInt(getMessage(response)));
			return contact;
		}
	}

	/**
	 * search for an other User
	 *
	 * @param tag
	 * @return returns a contact if user is found else returns null
	 */
	public Contact searchUser(int tag) {
		synchronized (userLock) {
			write("SF", tag + "", "");
			String response = read();
			Contact contact = null;
			String color = getInfo(response);
			if (getHeader(response).equals("OK"))
				contact = new Contact(getMessage(response), color, tag);
			return contact;
		}
	}

	/**
	 * sends a friend request to an other user
	 *
	 * @param tag
	 *            tag of the User
	 */
	public void sendFriendRequest(int tag) {
		synchronized (userLock) {
			write("FR", tag + "", "");
		}
	}

	/**
	 * replies a friend request
	 *
	 * @param tag
	 * @param accept
	 */
	public void replyFriendRequest(int tag, boolean accept) {
		synchronized (userLock) {
			write("RFR", tag + "", accept + "");
		}
	}

	/**
	 * removes a friend from your friend list and remove you from his friend
	 * list
	 *
	 * @param tag
	 */
	public void removeFriend(int tag) {
		synchronized (userLock) {
			write("RF", tag + "", "");
		}
	}

	/**
	 * creates a chat group with other Users
	 *
	 * @param groupName
	 * @param tags
	 *            list of other users
	 * @return
	 */
	public int createGroup(String groupName, int[] tags) {
		synchronized (userLock) {
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < tags.length - 1; i++) {
				stringBuilder.append(tags[i]).append(",");
			}
			stringBuilder.append(tags[tags.length - 1]);
			write("CG", groupName, stringBuilder.toString());
			return Integer.parseInt(getInfo(read()));
		}
	}

	/**
	 * invites a user to your group
	 *
	 * @param groupTag
	 * @param userTag
	 */
	public void groupInvite(int groupTag, int userTag) {
		synchronized (userLock) {
			write("GI", groupTag + "", userTag + "");
		}
	}

	/**
	 * promotes user to group leader works only if you are group leader
	 *
	 * @param groupTag
	 * @param userTag
	 */
	public void promoteGroupLeader(int groupTag, int userTag) {
		synchronized (userLock) {
			write("PGL", groupTag + "", userTag + "");
		}
	}

	/**
	 * kicks user from group works only if you are group leader
	 *
	 * @param groupTag
	 * @param userTag
	 */
	public void kickGroupMember(int groupTag, int userTag) {
		synchronized (userLock) {
			write("KGM", groupTag + "", userTag + "");
		}
	}

	/**
	 * leave the group
	 *
	 * @param groupTag
	 */
	public void leaveGroup(int groupTag) {
		synchronized (userLock) {
			write("LG", groupTag + "", "");
		}
	}

	@Override
	public void run() {
		int counter = 0;
		while (connected) {
			try {
				synchronized (userLock) {
					if (socket.dataAvailable() > 0) {
						String received = read();
						synchronized (userLock) {
							switch (getHeader(received)) {
							case "MSG":
								messageReceived(received);
								break;
							case "DATA":
								dataReceived(received);
								break;
							case "FR":
								friendRequestReceived(received);
								break;
							case "SFL":
								friendListReceived(received);
								break;
							case "SPL":
								pendingListReceived(received);
								break;
							case "SRL":
								requestListReceived(received);
								break;
							case "SGL":
								groupListReceived(received);
								break;
							case "GI":
								invitedToGroup(received);
								break;
							case "PGL":
								promotedToGroupLeader(received);
								break;
							case "RF":
								friendRemoved(received);
								break;
							case "RFR":
								friendRequestReplied(received);
								break;
							case "UGM":
								updateGroupMembers(received);
								break;
							case "TIME":
								timeReceived(received);
								break;
							case "PONG":
								break;
							case "PING":
								write("PONG", "", "");
								break;
							case "LOG":
								isLoggedIn = false;
								break;
							case "D":
								socket.close();
								user.disconnect();
								break;
							case "UC":
								updateColors(received);
								break;
							default:
								break;
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				//TODO: connection lost
				connected = false;
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			counter++;
			if (counter > 500) {
				counter = 0;
				write("PING", "", "");
			}
		}
	}

	private void updateColors(String received) {
		String[] users = getMessage(received).split(",");
		int[] tags = new int[users.length];
		for (int i = 0; i < users.length; i++) {
			tags[i] = Integer.parseInt(users[i]);
		}
		String[] colors = getMessage(read()).split(",");
		for (int i = 0; i < colors.length; i++) {
			Contact contact = user.getContact(tags[i]);
			if (contact == null) {
				System.err.println(
						"Fatal error! Contact not found! This means everything is not working in user! #BlameBene");
				contact = new Contact(tags[i]);
			}
			contact.setColor(colors[i]);
		}
	}

	private void updateGroupMembers(String received) {
		int groupTag = Integer.parseInt(getInfo(received));
		String[] members = getMessage(received).split(",");
		Group group = user.getGroup(groupTag);
		if (group == null) {
			System.err.println("I told you so bene! Group has no name! #BlameBene");
			return;
		}
		for (int i = 0; i < members.length; i++) {
			int tag = Integer.parseInt(members[i]);
			Contact contact = user.getContact(tag);
			user.deleteContact(tag);
			if (contact == null) {
				if (isLoggedIn)
					contact = searchUser(tag);
				else
					contact = new Contact(tag);
				user.addToUnsortedGroupMembers(contact);
				group.addUser(contact);
			}
		}
	}

	private void friendRequestReplied(String received) {
		int tag = Integer.parseInt(getInfo(received));
		boolean accept = Boolean.parseBoolean(getMessage(received));
		user.deleteContact(tag);
		if (accept) {
			Contact contact = searchUser(tag);
			user.addToFriends(contact);
		}
	}

	private void friendRemoved(String received) {
		int tag = Integer.parseInt(getInfo(received));
		user.deleteContact(tag);
	}

	private void promotedToGroupLeader(String received) {
		int groupTag = Integer.parseInt(getInfo(received));
		Group group = user.getGroup(groupTag);
		if (group == null) {
			System.err.println("You have been promoted to a group that you are not in! #BlameBene");
			return;
		}
		group.setAdmin(user.getSelf());
	}

	private void invitedToGroup(String received) {
		int groupTag = Integer.parseInt(getInfo(received));
		String groupName = getMessage(received);
		user.deleteGroup(groupTag);
		Group group = new Group(groupTag, groupName);
		group.setGroupName(groupName);
		user.addGroup(group);
		updateGroupMembers(read());
	}

	private void friendListReceived(String received) {
		String[] friends = getMessage(received).split(",");
		for (int i = 0; i < friends.length; i++) {
			int tag = Integer.parseInt(friends[i]);
			Contact contact = user.getContact(tag);
			user.deleteContact(tag);
			if (contact == null) {
				contact = new Contact(tag);
				if (!isLoggedIn)
					contact = searchUser(tag);
			}
			user.addToFriends(contact);
		}
	}

	private void pendingListReceived(String received) {
		String[] friends = getMessage(received).split(",");
		for (int i = 0; i < friends.length; i++) {
			int tag = Integer.parseInt(friends[i]);
			Contact contact = user.getContact(tag);
			user.deleteContact(tag);
			if (contact == null) {
				contact = new Contact(tag);
				if (!isLoggedIn)
					contact = searchUser(tag);
			}
			user.addToPending(contact);
		}
	}

	private void requestListReceived(String received) {
		String[] friends = getMessage(received).split(",");
		for (int i = 0; i < friends.length; i++) {
			int tag = Integer.parseInt(friends[i]);
			Contact contact = user.getContact(tag);
			user.deleteContact(tag);
			if (contact == null) {
				contact = new Contact(tag);
				if (!isLoggedIn)
					contact = searchUser(tag);
			}
			user.addToRequested(contact);
		}
	}

	private void groupListReceived(String received) {
		String[] groups = getMessage(received).split(",");
		String[] groupNames = getMessage(read()).split(",");
		for (int i = 0; i < groups.length; i++)
			user.addGroup(new Group(Integer.parseInt(groups[i]), groupNames[i]));
	}

	private void friendRequestReceived(String received) {
		int tag = Integer.parseInt(getInfo(received));
		Contact contact = user.getContact(tag);
		user.deleteContact(tag);
		if (contact == null) {
			contact = searchUser(tag);
		}
		user.addToRequested(contact);
	}

	private void messageReceived(String received) {
		String fromTo = getInfo(received);
		String[] infos = fromTo.split(",");
		user.messageReceived(Integer.parseInt(infos[0]), Integer.parseInt(infos[1]), getMessage(received), infos[2]);
	}

	private void dataReceived(String received) {
		String info = read();
		byte[] bytes = new byte[Integer.parseInt(getInfo(info))];
		do {
			byte checkSumme = Byte.parseByte(getMessage(info));
			do {
				try {
					socket.read(bytes, bytes.length);
				} catch (IOException e) {
					e.printStackTrace();
				}
				//Todo: decryption
				for (byte b : bytes) {
					checkSumme ^= b;
				}
				if (checkSumme == 0) {
					write("OK", "", "");
				} else
					write("NOK", "", "");
			} while (checkSumme != 0);
			info = read();
		} while (!getHeader(info).equals("EOT"));
		String[] fromTo = getInfo(received).split(",");
		// 0 from 1 to 2 wann
		user.dataReceived(Integer.parseInt(fromTo[0]), Integer.parseInt(fromTo[1]), info, bytes);
	}

	private void timeReceived(String received) {
		DateCalc.setOfSet(getMessage(received));
	}

	public static void main(String[] args) throws IOException {
		User user = new User("Horst");
		Client client = new Client("localhost", 1234, user);
		client.login("Horst", "password", 5);
		client.writeMessage(1234, "lol");
	}
}
