package server;

import java.io.IOException;
import java.util.ArrayList;

import dataManagement.DataManagement;
import dataManagement.DeviceLogin;
import dataManagement.Logger;
import dataManagement.Mailbox;
import secruity.KeyStoreSynchron;
import socketio.ServerSocket;
import socketio.Socket;

import javax.crypto.SecretKey;

public class Server {

	private final char[] passwordForData;
	private ArrayList<Account> accounts;
	private Thread acceptingThread;
	private ServerSocket server;
	private DataManagement dataMangement;
	private boolean isRunning = false;
	private final Object userlock = new Object();
	private final SecretKey secretKey;

	public Server(char[] password) {
		dataMangement = new DataManagement(null);
		this.passwordForData = password;
		if(KeyStoreSynchron.getInstance().loadKeyStore(password)){
			this.secretKey = KeyStoreSynchron.getInstance().getKey(password);
			startServer();
		}
		else{
			secretKey= null;
		}
	}

	private void startServer() {
		accounts = new ArrayList<>();
		isRunning = true;
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
	 * Tries to register a new User
	 *
	 * @return returns an Account of the registered User
	 * 			returns null, if something went wrong
	 */

	Account registerUser(String user, String pass) {
		synchronized (userlock) {
			if (user == null|| pass == null) {
				Logger.getInstance().log("Ser001: User cannot register because UserPass is null!");
				return null;
			}
			int tag = dataMangement.registerUser(user, pass);
			if(tag == 0){
				return null;
			}
			Account account = new Account(tag,this);
			accounts.add(account);

			return account;
		}
	}


	Account loginAccount(int tag, String passwort){
		synchronized (userlock){

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

		synchronized (userlock) {
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
		synchronized (userlock) {
			dataMangement.saveMessage(from, to.getTag(), date, message);
		}
	}

	public boolean removeFriend(int tagToRemove, int tagFromWhichAcc) {
		synchronized (userlock) {
			return dataMangement.removeFriend(tagFromWhichAcc, tagToRemove);
		}
	}

	Mailbox requestMessage(Account sender, String date) {
		synchronized (userlock) {
			return dataMangement.getMessages(sender.getTag(), date);
		}
	}

	int[] getFriendList(int account) {
		synchronized (userlock) {
			return dataMangement.getFriends(account);
		}
	}

	boolean leaveGroup(int accTag, int grpTag) {
		synchronized (userlock) {
			return dataMangement.removeFromGroup(accTag, grpTag);
		}
	}

	public DeviceLogin diviceLogin(int tag,int deviceNr) {
		return dataMangement.loginDevice(tag,deviceNr);
	}
}
