package server;

import dataManagement.DeviceLogin;
import socketio.Socket;
import user.KeyConverter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.PublicKey;

public class Client implements Runnable {

	private Socket socket;
	private Key key;
	private final Object userLock = new Object();
	private Account account;
	private boolean connected;
	private int deviceNr;

	public Client(Socket socket, Server server) throws IOException {
		this.socket = socket;
		authentification(server);
	}

	private void authentification(Server server) throws IOException {
		//todo: Generate public and private key
		PublicKey userPublicKey;

		try {
			userPublicKey = KeyConverter.generatePublicKeyFromString(getMessage(socket.readLine()));
		} catch (IOException e) {
			System.err.println("Could not read the Key from the server");
		}
		//TODO: generate and encrypt synchronised Key

		write("KEY","",key.toString());

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

	public void writeMessage(int from, int to, String date, String message) {
		synchronized (userLock) {
			write("MSG", from + "," + to, date + ":" + message);
		}
	}

	public boolean sendData(int from, int to, String filename, FileInputStream stream, boolean directConnection) throws IOException {
		synchronized (userLock) {
			if (stream.available() <= 1048576 || directConnection) {
				write("DATA", from + "," + to, filename);
				byte[] bytes = new byte[1024];
				int counter = 0;
				while (stream.available() > 0) {
					stream.read(bytes);
					while (!send(bytes, counter)) ;
					counter++;
				}
				write("EOT", "", "");
				return true;
			}
			return false;
		}
	}

	public void sendFriendlist(int[] friendlist) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int f : friendlist
				) {
			stringBuilder.append(f + ",");
		}
		write("SFL", friendlist.length + "", stringBuilder.toString());
	}

	public void sendFriendRequest(int tag, String username) {
		write("FR", tag + "", username);
	}

	public void replyFriendRequest(int tag, boolean accept) {
		write("RFR", tag + "", accept + "");
	}

	public void removeFriend(int tag, String message) {
		write("RF", tag + "", message);
	}

	public void groupInvite(int groupTag, String groupName) {
		write("GI", groupTag + "", groupName);
	}

	public void promoteGroupLeader(int groupTag) {
		write("PGL", groupTag + "", "");
	}

	public void kickGroupMember(int groupTag) {
		write("KGM", groupTag + "", "");
	}

	@Override
	public void run() {
		while (connected) {
			String received = read();
			switch (getHeader(received)) {
				case "MSG":
					account.recieveMessage(Integer.parseInt(getInfo(received)), getMessage(received), this);
					break;
				case "DATA":
					FileOutputStream fileOutputStream = account.dataReceived(Integer.parseInt(getInfo(received)),getMessage(received));
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
				default:
					break;
			}
		}
	}
}
