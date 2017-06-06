package server;

import dataManagement.DeviceLogin;
import socketio.ServerSocket;
import socketio.Socket;

import java.io.IOException;
import java.security.Key;

public class Client implements Runnable {

	private Socket socket;
	private Key key;
	private final Object userLock = new Object();
	private Account account;
	private boolean connected;
	private int deviceNr;
	boolean connectionInUse;

	public Client(Socket socket, Server server) throws IOException {
		this.socket = socket;
		connectionInUse = false;
		authentification(server);
	}

	private void authentification(Server server) throws IOException {
		/*
		//todo: Generate public and private key
		PublicKey userPublicKey;

		try {
			userPublicKey = KeyConverter.generatePublicKeyFromString(getMessage(socket.readLine()));
		} catch (IOException e) {
			System.err.println("Could not read the Key from the server");
		}
		//TODO: generate and encrypt synchronised Key

		write("KEY","",key.toString());
		*/
		String login = read();
		String[] usernamePassword = getMessage(login).split(",");
		if (getHeader(login).equals("REG")) {
			account = server.registerUser(usernamePassword[0],usernamePassword[1]);
		}else {
			account = server.loginAccount(Integer.parseInt(usernamePassword[0]),usernamePassword[1]);
		}

		if (account != null) {

			deviceNr = Integer.parseInt(getInfo(login));

			//TODO verändert bitte prüfen
			DeviceLogin deviceLogin = server.diviceLogin(account.getTag(),deviceNr);		//
			write("OK",deviceLogin.NUMBER+"",account.getTag()+"");		//
			connected = true;
			Thread t = new Thread(this);
			t.start();
			account.requestMessage(this,deviceLogin.DATE);							//

		} else {
			write("NO", "", "");
		}


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
		write("INFO", bytes.length + "", checksum + "");
		socket.getOutputStream().write(bytes);
		if (getHeader(read()).equals("OK"))
			return true;
		else
			return false;
	}

	public void writeMessage(int from, int to, String date, String message) {
		synchronized (userLock) {
			write("MSG", from + "," + to + "," + date, message);
		}
	}

	public boolean sendData(int from, int to, String date, String filename, byte[] data) throws IOException {
		synchronized (userLock) {
			if (data.length <= 1048576) {
				write("DATA", from + "," + to + "," + data, filename);
				while (!send(data));
				write("EOT", "", "");
				return true;
			}
			return false;
		}
	}

	public void sendFriendlist(int[] friendList) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < friendList.length-1; i++) {
			stringBuilder.append(friendList[i]).append(",");
		}
		stringBuilder.append(friendList[friendList.length]);
		write("SFL", friendList.length + "", stringBuilder.toString());
	}

	public void sendFriendRequest(int tag, String username) {
		write("FR", tag + "", username);
	}

	public void replyFriendRequest(int tag, boolean accept) {
		write("RFR", tag + "", accept + "");
	}

	public void removeFriend(int tag) {
		write("RF", tag + "", "");
	}

	public void groupInvite(int groupTag, String groupName, int[] members) {
		write("GI", groupTag + "", groupName);
		updateGroupMembers(groupTag, members);
	}

	public void promoteGroupLeader(int groupTag) {
		write("PGL", groupTag + "", "");
	}

	public void kickGroupMember(int groupTag) {
		write("KGM", groupTag + "", "");
	}

	public void updateGroupMembers(int tag,int[] tags) {
		synchronized (userLock) {
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < tags.length-1; i++) {
				stringBuilder.append(tags[i]).append(",");
			}
			stringBuilder.append(tags[tags.length]);
			write("UGM",tag+"",stringBuilder.toString());
		}
	}

	@Override
	public void run() {
		while (connected) {
			try {
				if (socket.dataAvailable() > 0&&!connectionInUse) {
					String received = read();
					synchronized (userLock) {
						switch (getHeader(received)) {
							case "MSG":
								messageReceived(received);
								break;
							case "DATA":
								dataReceived(received);
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

	private void messageReceived(String received) {
		account.recieveMessage(Integer.parseInt(getInfo(received)), getMessage(received), this);
	}

	private void dataReceived(String received) {
		String info = read();
		byte[] bytes = new byte[Integer.parseInt(getInfo(info))];
		do {
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
			info = read();
		} while (!getHeader(info).equals("EOT"));
		account.dataReceived(Integer.parseInt(getInfo(received)), getMessage(received), bytes);
	}

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(1234);
		Socket socket = serverSocket.accept();
		char[] password = {'a', 'A'};
		Client client = new Client(socket, new Server(password));
	}
}
