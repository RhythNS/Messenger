package user;

import socketio.Socket;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

import secruity.KeyConverter;

public class Client {

	private Socket socket;
	private PublicKey userPublicKey;
	private PrivateKey serverPrivateKey;
	private PublicKey serverPublicKey;


	public Client(String host, int port) {
		socket = new Socket(host, port);
	}

	public boolean connect() {
		return socket.connect();
	}

	/**
	 * Returns 0 if the registration process failed. Otherwise the tag is returned
	 */
	public int register(String username, String password) {
		if(!connect()) {
			System.err.println("Could not connect!");
			return 0;
		}
		// TODO Generate public and private Key
		write("KEY", userPublicKey.toString());
		try {
			serverPublicKey = KeyConverter.generatePublicKey(socket.readLine());
		} catch (IOException e) {
			System.err.println("Could not read the Key from the server");
			return 0;
		}

		write("REG", username + "," + password);
		String response = "";

		response = read();

		if (response.contains("OK")) {
			return Integer.parseInt(getMessage(response));
		}
		return 0;
	}

	public boolean login(String username, int tag, String password) {
		if(!connect()) {
			System.err.println("Could not connect!");
			return false;
		}
		// TODO Generate public and private Key
		write("KEY", userPublicKey.toString());
		try {
			serverPublicKey = KeyConverter.generatePublicKey(socket.readLine());
		} catch (IOException e) {
			System.err.println("Could not read the Key from the server");
			return false;
		}

		write("LOG", tag + "," + password);
		String response = read();
		if (getHeader(response).equals("OK")) {

		}
		return false;
	}

	public void writeMessage(int tag, String message) {
		write("MSG"+ tag,message);
	}

	public void write(String header, String message) {
		//todo: Decryption
		try {
			socket.write(header+"[/]"+message+"\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getHeader(String message) {
		return message.split("[/]", 2)[0];
	}

	private String getMessage(String message) {
		return message.split("[/]", 2)[1];
	}

	private String read() {
		//todo: Entcryption
		try {
			return socket.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void writeData(int tag, File file) {
		// TODO Auto-generated method stub

	}


}
