package server;

import dataManagement.ColorTransfer;
import dataManagement.DateCalc;
import dataManagement.DeviceLogin;
import dataManagement.GroupTransfer;
import socketio.ServerSocket;
import socketio.Socket;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;

public class Client implements Runnable {

	private Socket socket;
	private Key key;
	private final Object userLock = new Object();
	private Account account;
	private boolean connected;
	private int deviceNr;

	public Client(Socket socket, Server server) throws IOException {
		this.socket = socket;
		authentication(server);
		deviceNr = -1;
	}

	private void authentication(Server server) throws IOException {
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
			account = server.registerUser(usernamePassword[0],usernamePassword[1],getInfo(login));
		}else {
			account = server.loginAccount(usernamePassword[0],usernamePassword[1]);
			deviceNr = Integer.parseInt(getInfo(login));
		}

		if (account != null) {
			account.addClient(this);

			//TODO verändert bitte prüfen
			DeviceLogin deviceLogin = server.diviceLogin(account.getTag(),deviceNr);		//
			write("OK",deviceLogin.NUMBER+"",account.getTag()+"");		//
			connected = true;
			Thread t = new Thread(this);
			t.start();
			account.requestMessage(this,deviceLogin.DATE);
			sendTime(DateCalc.getDeviceDate());
		} else {
			write("NO", "", "");
		}
		write("LOG","","");

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

	private void write(String header, String info, String message) {
		//todo: Encryption
		try {
			socket.write(header+"/"+info+"/"+message+"\n");
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
				write("DATA", from + "," + to + "," + date, filename);
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
		stringBuilder.append(friendList[friendList.length-1]);
		write("SFL", friendList.length + "", stringBuilder.toString());
	}

	public void sendPendinglist(int[] pending) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < pending.length-1; i++) {
			stringBuilder.append(pending[i]).append(",");
		}
		stringBuilder.append(pending[pending.length-1]);
		write("SPL", pending.length + "", stringBuilder.toString());
	}

	public void sendRequestlist(int[] requests) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < requests.length-1; i++) {
			stringBuilder.append(requests[i]).append(",");
		}
		stringBuilder.append(requests[requests.length-1]);
		write("SFL", requests.length + "", stringBuilder.toString());
	}

	public void sendGrouplist(ArrayList<GroupTransfer> groupTransfers) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < groupTransfers.size()-1; i++) {
			stringBuilder.append(groupTransfers.get(i).getGroupTag()).append(",");
		}
		stringBuilder.append(groupTransfers.get(groupTransfers.size()-1).getGroupTag());
		write("SGL", groupTransfers.size() + "", stringBuilder.toString());
		stringBuilder = new StringBuilder();
		for (int i = 0; i < groupTransfers.size()-1; i++) {
			stringBuilder.append(groupTransfers.get(i).getName()).append(",");
		}
		stringBuilder.append(groupTransfers.get(groupTransfers.size()-1).getName());
		write("SGL",groupTransfers.size()+"",stringBuilder.toString());
	}

	public void updateColors(ArrayList<ColorTransfer> colorTransfers) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < colorTransfers.size()-1; i++) {
			stringBuilder.append(colorTransfers.get(i).getTag()).append(",");
		}
		stringBuilder.append(colorTransfers.get(colorTransfers.size() - 1).getTag());
		write("UC","",stringBuilder.toString());
		String tags = stringBuilder.toString();
		stringBuilder = new StringBuilder();
		for (int i = 0; i < colorTransfers.size()-1; i++) {
			stringBuilder.append(colorTransfers.get(i).getColor()).append(",");
		}
		stringBuilder.append(colorTransfers.get(colorTransfers.size() - 1).getColor());
		write("UC","",stringBuilder.toString());
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

	public void sendTime(String date) {
		write("TIME", "", date);
	}

	public void disconnect() {
		write("D", "", "");
		connected = false;
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		account.disconnect(this, this.deviceNr, false);
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
								case "SU":
									searchUser(received);
									break;
								case "SFR":
									friendshipRequested(received);
									break;
								case "RFR":
									friendRequestReplied(received);
									break;
								case "RF":
									friendRemoved(received);
									break;
								case "CG":
									groupCreated(received);
									break;
								case "GI":
									groupInvite(received);
									break;
								case "PGL":
									promoteGroupLeader(received);
									break;
								case "KGM":
									kickGroupMember(received);
									break;
								case "LG":
									leftGroup(received);
									break;
								case "PONG":
									break;
								case "PING":
									write("PONG", "", "");
									break;
								case "D":

								default:
									break;
							}
						}
					}
				}
			} catch (IOException e) {
				//TODO: connection lost
				disconnected(true);
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			counter++;
			if (counter > 500) {
				counter = 0;
				write("PING","","");
			}
		}
	}

	private void disconnected(boolean timeout) {
		connected = false;
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		account.disconnect(this, this.deviceNr, timeout);
	}

	private void messageReceived(String received) {
		account.receiveMessage(Integer.parseInt(getInfo(received)), getMessage(received), this);
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
		account.dataReceived(Integer.parseInt(getInfo(received)), getMessage(received), bytes, this);
	}

	private void searchUser(String received) {
		String response = "";
		String color = "";
		if (getInfo(received).equals("")) {
			String username = getMessage(received);
		} else {
			int tag = Integer.parseInt(getInfo(received));
		}

		if (response.equals("")) {
			write("NO", "", "");
		} else {
			write("OK",color,response);
		}

	}

	private void friendshipRequested(String received) {
		int tag = Integer.parseInt(getInfo(received));
		account.addToFriendlist(tag);
	}

	private void friendRequestReplied(String received) {
		int tag = Integer.parseInt(getInfo(received));
		boolean accept = Boolean.parseBoolean(getMessage(received));
		if (accept)
			account.acceptFriend(tag);
		else
			account.declineFriendShip(tag);
	}

	private void friendRemoved(String received) {
		int tag = Integer.parseInt(getInfo(received));
		account.removeFriend(tag, this);
	}

	private void groupCreated(String received) {
		String[] members = getMessage(received).split(",");
		int[] tags = new int[members.length];
		for (int i = 0; i < members.length; i++) {
			tags[i] = Integer.parseInt(members[i]);
		}
		String groupname = getInfo(received);
		int groupTag = account.createGroup(groupname, tags);
		write("GT", groupTag + "", "");
	}

	private void groupInvite(String received) {
		int groupTag = Integer.parseInt(getInfo(received));
		int userTag = Integer.parseInt(getMessage(received));
		account.addToGroup(groupTag, userTag);
	}

	private void promoteGroupLeader(String received) {
		int groupTag = Integer.parseInt(getInfo(received));
		int userTag = Integer.parseInt(getMessage(received));

	}

	private void kickGroupMember(String received) {
		int groupTag = Integer.parseInt(getInfo(received));
		int userTag = Integer.parseInt(getMessage(received));
		account.removeFromGroup(groupTag, userTag);
	}

	private void leftGroup(String received) {
		int groupTag = Integer.parseInt(getInfo(received));
		account.leaveGroup(groupTag);
	}

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(1234);
		Socket socket = serverSocket.accept();
		char[] password = {'a', 'A'};

	}
}
