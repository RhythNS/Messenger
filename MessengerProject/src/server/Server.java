package server;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import dataManagement.DataManagement;
import secruity.KeyConverter;
import socketio.ServerSocket;
import socketio.Socket;

public class Server {

	private Account admin;
	private ArrayList<Account> accounts;
	private ArrayList<Group> groups;
	private Thread acceptingThread;
	private ServerSocket server;
	private DataManagement dataMangement;
	private boolean isRunning = false;

	public Server() {

	}

	private void startAcceptingThread() {
		if (!isRunning) {
			isRunning = true;
			acceptingThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (isRunning) {
						Socket socket = null;
						try {
							socket = server.accept();
						} catch (IOException e) {
							System.err.println("Something went wrong with accepting the socket! #BlameBene");
							e.printStackTrace();
							continue;
						}
						// TODO GENERATE PRIVATE AND PUBLIC KEYS
						try {
							PublicKey userPublicKey = KeyConverter.generatePublicKey(socket.readLine());
						} catch (IOException e) {
							System.err.println("Something went wrong with getting the PublicKey! #BlameBene");
							e.printStackTrace();
							continue;
						}
					}
				}
			});
		}
	}

	protected void connectUserAccount(Account account) {

	}

	protected void createGroup(Account[] accounts) {

	}

}
