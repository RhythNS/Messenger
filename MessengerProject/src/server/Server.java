package server;

import dataManagement.*;
import secruity.Decrypter;
import secruity.Encrypter;
import secruity.KeyStoreSynchron;
import socketio.ServerSocket;
import socketio.Socket;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.ArrayList;

public class Server {

	private final char[] passwordForData;
	private ArrayList<Account> accounts;
	private Thread acceptingThread;
	private ServerSocket server;
	private DataManagement dataMangement;
	private boolean isRunning = false;
	private final SecretKey secretKey;
	private final int port;

	public Server(char[] password, int port) {

		this.port = port;
		dataMangement = new DataManagement(null);
		this.passwordForData = password;
		if(KeyStoreSynchron.getInstance().loadKeyStore(password)){

			this.secretKey = KeyStoreSynchron.getInstance().getKey(password);
			startServer();
		}
		else{
			Logger.getInstance().log("Ser002: Cannot reach the older KeyStore! Server will not start!");
			secretKey= null;
		}
	}

	private void startServer() {
		accounts = new ArrayList<>();
		isRunning = true;
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			Logger.getInstance().log("Ser003: Cannot start the ServerSocket! ");
			e.printStackTrace();
			return;
		}
		startAcceptingThread();
	}

	private void startAcceptingThread() {
		if (!isRunning) {
			isRunning = true;
			Server serverInstance = this;
			acceptingThread = new Thread(() -> {
				while (isRunning) {
					Socket socket;
					try {
						socket = server.accept();
						new Client(socket, serverInstance);
					} catch (IOException e) {
						System.err.println("Something went wrong with accepting the socket! #BlameBene");
						e.printStackTrace();
					}
				}
			});
		}
	}

	/**
	 * Tries to register a new User. This needs to be called
	 * at first when User wants to join.
	 * @Params are the username and the password
	 *
	 * @return returns an Account of the registered User
	 * 			returns null, if something went wrong
	 */

	Account registerUser(String user, String pass,String color) {
		if (user == null|| pass == null) {
			Logger.getInstance().log("Ser001: User cannot register because UserPass is null!");
			return null;
		}
		int tag = dataMangement.registerUser(user, pass, color);
		if(tag == 0){
			return null;
		}
		Account account = new Account(tag,this);
		accounts.add(account);

		return account;
	}


	Account loginAccount(int tag, String passwort){
		if(!dataMangement.login(tag,passwort))return null;

		for (Account a: accounts){
			if(a.getTag() == tag){
				return a;
			}
		}
		Account a = new Account(tag, this);
		accounts.add(a);
		return a;
	}

	/**
	 *
	 * @param username required for login
	 * @param passwort required for login
	 * @return if login can be done returns the Account
	 * 			if the Account is already online it just adds a CLient to this Account --> returns Account
	 *
	 * 		if the login went wrong somehow --> returns null
	 */

	Account loginAccount(String username, String passwort) {

		int tag = dataMangement.login(username, passwort);
		if (tag == -1) {
			Logger.getInstance().log("Ser003: Cannot find the fitting tag to log in @loginAccount()!");
			return null;
		}

		for (Account account: accounts) {
			if(account.getTag() == tag){
				return account;
			}
		}
		Account a = new Account(tag,this);
		accounts.add(a);
		return a;
	}

	int createGroup(String name, Account[] accounts) {

		int[] tags = new int[accounts.length];
		for (int i = 0; i < tags.length; i++) {
			tags[i] = accounts[i].getTag();
		}
		return dataMangement.createGroup(name,tags);
	}



	boolean addFriendTo(Account account, int tagOfAccountToAdd) {
		return dataMangement.addFriend(account.getTag(),tagOfAccountToAdd);
	}

	void recieveMessage(int from, Account to, String message, String date) {
		String encrypted = Encrypter.encryptSynchron(message,secretKey);
		dataMangement.saveMessage(from, to.getTag(), date, encrypted);
	}

	void recieveFile(int from, Account to, byte[] file, String date){
		String encodedFile = Encrypter.encryptSynchron(file,secretKey);
		dataMangement.saveFile(from,to.getTag(),date,encodedFile);
	}

	boolean removeFriend(int tagToRemove, int tagFromWhichAcc) {
		return dataMangement.removeFriend(tagFromWhichAcc, tagToRemove);
	}

	/**
	 * This Method is called when a User wants to have Messages from a specific date.
	 * Gets a Mailbox from dataManagment and decrypts it. Returns it then.
	 *
	 * @param sender The Accound who asks for the Mailbox
	 * @param date	 The specific date the user wants the Messages
	 * @return 		 a decrypted Mailbox
	 */

	Mailbox requestMessage(Account sender, String date) {

		Mailbox returnBox =dataMangement.getMessages(sender.getTag(),date);
		for (int i = 0; i < returnBox.messageSize(); i++) {
			Message tm = returnBox.getMessage(i);
			tm.setContent(Decrypter.decryptSynchronToString(tm.getContent(),secretKey));
		}

		for (int i = 0; i < returnBox.fileSize(); i++) {
			Message fm = returnBox.getFile(i);
			fm.setContent(Decrypter.decryptSynchronToString(fm.getContent(),secretKey));
		}
		return returnBox;
	}

	int[] getFriendList(int account) {
		return dataMangement.getFriends(account);
	}

	boolean leaveGroup(int accTag, int grpTag) {
		return dataMangement.removeFromGroup(accTag, grpTag);
	}

	DeviceLogin diviceLogin(int tag,int deviceNr) {
		return dataMangement.loginDevice(tag,deviceNr);
	}

	void disconnctAccount(Account account) {
		if(accounts.remove(account)) {
			if(accounts.size()== 0) {
				dataMangement.noUsersLoggedIn();
			}
		}
	}

	void disconnectDevice(int deviceNumber, Account account, boolean timeout) {
		dataMangement.logout(account.getTag(),deviceNumber,timeout);
	}

	void dataRecieved(int from, int toAcc, byte[] message, String date) {
		String encrypted = Encrypter.encryptSynchron(message,secretKey);
		dataMangement.saveFile(from,toAcc,date,encrypted);
	}


}