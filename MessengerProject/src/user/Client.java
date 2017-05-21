package user;

import security.KeyConverter;
import socketio.Socket;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

public class Client implements Runnable{

	private Socket socket;
	private PublicKey serverPublicKey;
	private PrivateKey userPrivateKey;
	private PublicKey userPublicKey;
	private String lastOnlineDate;


	public Client(String host, int port,String lastOnlineDate) {
		socket = new Socket(host, port);
		this.lastOnlineDate = lastOnlineDate;
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

			serverPublicKey = KeyConverter.generatePublicKey(getMessage(socket.readLine()));
		} catch (IOException e) {
			System.err.println("Could not read the Key from the server");
			return 0;
		}

		write("REG", username + "," + password);
		String response = "";

		response = read();

		if (getHeader(response).equals("OK")) {
			return Integer.parseInt(getMessage(response));
		}
		return 0;
	}

	public boolean login(String username, int tag, String password) throws IOException {
		if(!connect()) {
			System.err.println("Could not connect!");
			return false;
		}
		// TODO Generate public and private Key
        socket.write("KEY" + "[/]" + userPublicKey.toString());
		try {
			serverPublicKey = KeyConverter.generatePublicKey(getMessage(socket.readLine()));
		} catch (IOException e) {
			System.err.println("Could not read the Key from the server");
			return false;
		}

		// TODO HASH PASSWORD
		write("LOG", tag + "," + password);
		String response = read();
		if (getHeader(response).equals("YES")) {
			write("LOD", lastOnlineDate);
			Thread t = new Thread(this);
			t.start();
			return true;
		}else {
			socket.close();
		}
		return false;
	}

	public void writeMessage(int tag, String message) {
		write("MSG"+ tag,message);
	}

	public boolean sendData(int tag,String filename,FileInputStream stream, boolean directConnection) throws IOException {
		if (stream.available() <= 1048576 || directConnection) {
			write("DATA",tag+","+filename+","+stream.available());
            if (getHeader(read()).equals("OK")) {
                byte[] bytes = new byte[1024];
                int counter = 0;
                while (stream.available() > 0) {
                    stream.read(bytes);
                    while (!send(bytes,counter));
                }
                return true;
            }
		}
		return false;
	}

	public void write(String header, String message) {
		//todo: Encryption
		try {
			socket.write(header+"[/]"+message+"\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean send(byte[] bytes,int blockNr) throws IOException {
        //todo: Encryption
		byte checksum = 0;
		for (byte b:bytes
			 ) {
			checksum ^= b;
		}
		write("Block",blockNr+","+checksum);
		socket.getOutputStream().write(bytes);
		if (getHeader(read()).equals("OK"))
			return true;
		else
			return false;
	}

	private String getHeader(String message) {
		return message.split("[/]", 2)[0];
	}

	private String getMessage(String message) {
		return message.split("[/]", 2)[1];
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

	public static void main(String[] args) throws IOException {
		File file = new File("C:\\Users\\orbit\\OneDrive\\Bilder\\Eigene Aufnahmen\\Background.png");
		FileInputStream fileInputStream = new FileInputStream(file);
		byte[] bytes = new byte[1024];
		while (fileInputStream.available() > 0) {
			System.out.println(fileInputStream.read(bytes));
			System.out.println(Arrays.toString(bytes));
		}
		System.out.println();
	}

	@Override
	public void run() {

	}
}
