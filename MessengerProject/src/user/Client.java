package user;

import socketio.Socket;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.PublicKey;

public class Client implements Runnable{

	private Socket socket;
	private final Object userLock = new Object();
	private User user;
	private boolean connected;
	private boolean connectionInUse;


	public Client(String host, int port, User user) {
		socket = new Socket(host, port);
		connectionInUse = false;
		this.user = user;
	}

	public boolean connect() {
		return socket.connect();
	}

	/**
	 * Returns 0 if the registration process failed. Otherwise the tag is returned
	 */
	public int register(String username, String password) throws IOException {
		if(!connect()) {
			System.err.println("Could not connect!");
			return 0;
		}
		// TODO Generate public and private Key

		PublicKey userPublicKey = null;
		write("KEY","", userPublicKey.toString());
		String synchronisedKey = getMessage(socket.readLine());
		// TODO: decode Key
		// TODO: generate synchronised Key

		write("REG", -1+"",username + "," + password);

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

	public boolean login(int tag, String password,int deviceNr) throws IOException {
		if(!connect()) {
			System.err.println("Could not connect!");
			return false;
		}
		/*
		// TODO Generate public and private Key

		PublicKey userPublicKey = null;
		write("KEY","", userPublicKey.toString());
		String synchronisedKey = getMessage(socket.readLine());
		// TODO: decode Key
		// TODO: generate synchronised Key

		// TODO HASH PASSWORD
		*/
		write("LOG", deviceNr+"", tag+","+password);
		String response = read();

		if (getHeader(response).equals("OK")) {
			connected = true;
			Thread t = new Thread(this);
			t.start();
			return true;
		}else {
			socket.close();
		}
		return false;
	}

	private void write(String header, String info, String message) {
		//todo: Encryption
		try {
			socket.write(header+"|"+info+"|"+message+"\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean send(byte[] bytes) throws IOException {
        //todo: Encryption
		byte checksum = 0;
		for (byte b:bytes
				) {
			checksum ^= b;
		}
		write("DATA", "", checksum+"");
		socket.getOutputStream().write(bytes);
		if (getHeader(read()).equals("OK"))
			return true;
		else
			return false;
	}

	private String getHeader(String message) {
		return message.split("|", 3)[0];
	}

	private String getInfo(String message) {
		return message.split("|", 3)[1];
	}

	private String getMessage(String message) {
		return message.split("|", 3)[2];
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
	 * @param tag tag of the User
	 * @param message
	 */
	public void writeMessage(int tag, String message) {
		synchronized (userLock) {
			write("MSG",tag+"",message);
		}
	}

	/**
	 * sends a data stream to an other User
	 *
	 * @param tag
	 * @param filename
	 * @param stream
	 * @return returns false if file size is to big
	 * @throws IOException
	 */
	public boolean sendData(int tag, String filename, FileInputStream stream) throws IOException {
		synchronized (userLock) {
			if (stream.available() <= 1048576) {
				write("DATA", tag + "", filename);
				byte[] bytes = new byte[stream.available()];
				stream.read(bytes);
				while (!send(bytes)) ;
				write("EOT", "", "");
				return true;
			}
			return false;
		}
	}

	/**
	 * requests message from server from a specific date until now
	 * @param date
	 */
	public void mailboxRequest(String date) {
		synchronized (userLock) {
			write("MR", date, "");
		}
	}

	/**
	 * request an up to date version of the friends list
	 */
	public void requestFriendslist() {
		synchronized (userLock) {
			write("RFL","","");
		}
	}

	/**
	 * sends the local friends lis to the server
	 * @param friendList
	 */
	public void sendFriendlist(int[] friendList) {
		synchronized (userLock) {
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < friendList.length-1; i++) {
				stringBuilder.append(friendList[i]).append(",");
			}
			stringBuilder.append(friendList[friendList.length-1]);
			write("SFL", friendList.length + "", stringBuilder.toString());
		}
	}

	/**
	 * search for an other User
	 * @param username
	 * @return returns a contact if user is found else returns null
	 */
	public Contact searchUser(String username) {
		synchronized (userLock) {
			write("SU","",username);
			String response = read();
			Contact contact = null;
			String color = getInfo(response);
			if (getHeader(response).equals("OK"))
				contact = new Contact(username, Integer.parseInt(getMessage(response)));
			return contact;
		}
	}

	/**
	 * search for an other User
	 * @param tag
	 * @return returns a contact if user is found else returns null
	 */
	public Contact searchFriend(int tag) {
		synchronized (userLock) {
			write("SF",tag+"","");
			String response = read();
			Contact contact = null;
			if (getHeader(response).equals("OK"))
				contact = new Contact(getMessage(response), tag);
			return contact;
		}
	}

	/**
	 * sends a friend request to an other user
	 * @param tag tag of the User
	 */
	public void sendFriendRequest(int tag) {
		synchronized (userLock) {
			write("FR", tag + "", "");
		}
	}

	/**
	 * replies a friend request
	 * @param tag
	 * @param accept
	 */
	public void replyFriendRequest(int tag, boolean accept) {
		synchronized (userLock) {
			write("RFR", tag + "", accept + "");
		}
	}

	/**
	 * removes a friend from your friend list and remove you from his friend list
	 * @param tag
	 */
	public void removeFriend(int tag) {
		synchronized (userLock) {
			write("RF", tag + "", "");
		}
	}

	/**
	 * creates a chat group with other Users
	 * @param groupName
	 * @param tags list of other users
	 * @return
	 */
	public int createGroup(String groupName, int[] tags) {
		synchronized (userLock) {
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < tags.length-1; i++) {
				stringBuilder.append(tags[i]).append(",");
			}
			stringBuilder.append(tags[tags.length-1]);
			write("CG", groupName,stringBuilder.toString());
			return Integer.parseInt(getInfo(read()));
		}
	}

	/**
	 * invites a user to your group
	 * @param groupTag
	 * @param userTag
	 */
	public void groupInvite(int groupTag, int userTag) {
		synchronized (userLock) {
			write("GI", groupTag + "", userTag + "");
		}
	}

	/**
	 * promotes user to group leader
	 * works only if you are group leader
	 * @param groupTag
	 * @param userTag
	 */
	public void promoteGroupLeader(int groupTag, int userTag) {
		synchronized (userLock) {
			write("PGL", groupTag + "", userTag + "");
		}
	}

	/**
	 * kicks user from group
	 * works only if you are group leader
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
	 * @param groupTag
	 */
	public void leaveGroup(int groupTag) {
		synchronized (userLock) {
			write("LG", groupTag + "", "");
		}
	}

	@Override
	public void run() {
		while (connected) {
			try {
				if (socket.dataAvailable() > 0 && !connectionInUse) {
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
							default:
								break;
						}
					}
                }
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateGroupMembers(String received) {
		int groupTag = Integer.parseInt(getInfo(received));
		String[] members = getMessage(received).split(",");
		int[] tags = new int[members.length];
		for (int i = 0; i < members.length; i++) {
			tags[i] = Integer.parseInt(members[i]);
		}
	}

	private void friendRequestReplied(String received) {
		int tag = Integer.parseInt(getInfo(received));
		boolean accept = Boolean.parseBoolean(getMessage(received));
	}

	private void friendRemoved(String received) {
		int tag = Integer.parseInt(getInfo(received));
	}

	private void promotedToGroupLeader(String received) {
		int groupTag = Integer.parseInt(getInfo(received));
	}

	private void invitedToGroup(String received) {
		int groupTag = Integer.parseInt(getInfo(received));
		String groupName = getMessage(received);
		updateGroupMembers(read());
	}

	private void friendListReceived(String received) {
		String[] friends = getMessage(received).split(",");
		int[] tags = new int[friends.length];
		for (int i = 0; i < friends.length; i++) {
			tags[i] = Integer.parseInt(friends[i]);
		}
	}

	private void pendingListReceived(String received) {
		String[] friends = getMessage(received).split(",");
		int[] tags = new int[friends.length];
		for (int i = 0; i < friends.length; i++) {
			tags[i] = Integer.parseInt(friends[i]);
		}
	}

	private void requestListReceived(String received) {
		String[] friends = getMessage(received).split(",");
		int[] tags = new int[friends.length];
		for (int i = 0; i < friends.length; i++) {
			tags[i] = Integer.parseInt(friends[i]);
		}
	}

	private void groupListReceived(String received) {
		String[] groups = getMessage(received).split(",");
		int[] tags = new int[groups.length];
		for (int i = 0; i < groups.length; i++) {
			tags[i] = Integer.parseInt(groups[i]);
		}
	}

	private void friendRequestReceived(String received) {
		int userName = Integer.parseInt(getInfo(received));
		String username = getMessage(received);
	}

	private void messageReceived(String received) {
		String fromTo = getInfo(received);
		String[] infos = fromTo.split(",");
		user.messageReceived(Integer.parseInt(infos[0]), Integer.parseInt(infos[1]), getMessage(received),infos[2]);
	}

	private void dataReceived(String received) {
<<<<<<< HEAD
=======
		String fromTo = getInfo(received);
		String[] infos = fromTo.split(",");
		FileOutputStream fileOutputStream = user.dataReceived(Integer.parseInt(infos[0]), Integer.parseInt(infos[1]), getMessage(received), infos[2]);
>>>>>>> 20d8ddcc3f992677428816bbf3786a438245a24b
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
				for (byte b : bytes
						) {
					checkSumme ^= b;
				}
				if (checkSumme == 0) {
					write("OK","","");
				}else
					write("NOK","","");
			} while (checkSumme != 0);
			info = read();
		} while (!getHeader(info).equals("EOT"));
		String[] fromTo = getInfo(received).split(",");
		user.dataReceived(Integer.parseInt(fromTo[0]),Integer.parseInt(fromTo[1]), getMessage(received), bytes);
	}

	public static void main(String[] args) throws IOException {
		User user = new User("Horst");
		Client client = new Client("localhost", 1234, user);
		client.login(1256, "password", 5);
		client.writeMessage(1234,"lol");
	}
}
