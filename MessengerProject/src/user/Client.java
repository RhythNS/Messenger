package user;

import socketio.Socket;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PublicKey;

public class Client implements Runnable{

	private Socket socket;
	private final Object userLock = new Object();
	private User user;
	private boolean connected;


	public Client(String host, int port, User user) {
		socket = new Socket(host, port);
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
		// TODO Generate public and private Key

		PublicKey userPublicKey = null;
		write("KEY","", userPublicKey.toString());
		String synchronisedKey = getMessage(socket.readLine());
		// TODO: decode Key
		// TODO: generate synchronised Key

		// TODO HASH PASSWORD
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

	private boolean send(byte[] bytes,int blockNr) throws IOException {
        //todo: Encryption
		byte checksum = 0;
		for (byte b:bytes
			 ) {
			checksum ^= b;
		}
		write("Block", blockNr+"", checksum+"");
		socket.getOutputStream().write(bytes);
		if (getHeader(read()).equals("OK"))
			return true;
		else
			return false;
	}

	/**
	 * This method does x
	 * @param message the sent message
	 * @return null
	 */
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

	public void writeMessage(int tag, String message) {
		synchronized (userLock) {
			write("MSG",tag+"",message);
		}
	}

	public boolean sendData(int tag,String filename,FileInputStream stream, boolean directConnection) throws IOException {
		synchronized (userLock) {
			if (stream.available() <= 1048576 || directConnection) {
				write("DATA",tag+"",filename);
				byte[] bytes = new byte[1024];
				int counter = 0;
				while (stream.available() > 0) {
					stream.read(bytes);
					while (!send(bytes,counter));
					counter++;
				}
				write("EOT","","");
				return true;
			}
			return false;
		}
	}

	public void messageRequest(String date) {
		synchronized (userLock) {
			write("MSGR",date,"");
		}
	}

	public void requestFriendslist() {
		synchronized (userLock) {
			write("RFL","","");
		}
	}

	public void sendFriendlist(int[] friendList) {
		synchronized (userLock) {
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < friendList.length-1; i++) {
				stringBuilder.append(friendList[i]).append(",");
			}
			stringBuilder.append(friendList[friendList.length]);
			write("SFL", friendList.length + "", stringBuilder.toString());
		}
	}

	public Contact[] searchFriends(String username) {
		synchronized (userLock) {
			write("SF","",username);
			String list = read();
			String[] conatactStrings = getMessage(list).split("(/)");
			Contact[] contacts = new Contact[conatactStrings.length];
			for (int i = 0; i < conatactStrings.length; i++) {
				String[] s = conatactStrings[i].split(",");
				contacts[i] = new Contact(s[0],Integer.parseInt(s[1]));
			}
			return contacts;
		}
	}

	public void sendFriendRequest(int tag) {
		synchronized (userLock) {
			write("FR", tag + "", "");
		}
	}

	public void replyFriendRequest(int tag, boolean accept) {
		synchronized (userLock) {
			write("RFR", tag + "", accept + "");
		}
	}

	public void removeFriend(int tag, String message) {
		synchronized (userLock) {
			write("RF", tag + "", message);
		}
	}

	public int createGroup(String groupName, int[] tags) {
		synchronized (userLock) {
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < tags.length-1; i++) {
				stringBuilder.append(tags[i]).append(",");
			}
			stringBuilder.append(tags[tags.length]);
			write("CG", groupName,stringBuilder.toString());
			return Integer.parseInt(getInfo(read()));
		}
	}

	public void groupInvite(int groupTag, int userTag) {
		synchronized (userLock) {
			write("GI", groupTag + "", userTag + "");
		}
	}

	public void promoteGroupLeader(int groupTag, int userTag) {
		synchronized (userLock) {
			write("PGL", groupTag + "", userTag + "");
		}
	}

	public void kickGroupMember(int groupTag, int userTag) {
		synchronized (userLock) {
			write("KGM", groupTag + "", userTag + "");
		}
	}

	public void leaveGroup(int groupTag) {
		synchronized (userLock) {
			write("LG", groupTag + "", "");
		}
	}

	@Override
	public void run() {
		while (connected) {
			String recieved = read();
			synchronized (userLock) {
				switch (getHeader(recieved)) {
					case "MSG":
						String fromTo = getInfo(recieved);
						String[] tags = fromTo.split(",");
						user.messageRecieved(Integer.parseInt(tags[0]), Integer.parseInt(tags[1]), getMessage(recieved));
						break;
					case "DATA":
						fromTo = getInfo(recieved);
						tags = fromTo.split(",");
						FileOutputStream fileOutputStream = user.dataReceived(Integer.parseInt(tags[0]), Integer.parseInt(tags[1]),getMessage(recieved));
						String info = read();
						do {
							byte[] bytes = new byte[1024];
							byte checkSumme = Byte.parseByte(getMessage(info));
							do {
								try {
									socket.read(bytes, 1024);
								} catch (IOException e) {
									e.printStackTrace();
								}
								for (byte b : bytes
										) {
									checkSumme ^= b;
								}
								if (checkSumme == 0) {
									write("OK","","");
								}else
									write("NOK","","");
							} while (checkSumme != 0);
							try {
								fileOutputStream.write(bytes);
							} catch (IOException e) {
								e.printStackTrace();
							}
							info = read();
						} while (!getHeader(info).equals("EOT"));
						try {
							fileOutputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					case "FR":
						break;
					case "SFL":
						break;
					case "GI":
						break;
					case "PGL":
						break;
					case "RF":
						break;
					case "RFR":
						break;
					case "UGM":
						break;
					default:
						break;
				}
			}
		}
	}
}
