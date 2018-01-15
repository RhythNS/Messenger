package server;

import dataManagement.*;
import secruity.Decrypter;
import secruity.Encrypter;
import secruity.KeyConverter;
import secruity.KeyStoreSynchron;
import socketio.ServerSocket;
import socketio.Socket;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {

	private final char[] passwordForData;
	private ArrayList<Account> accounts;
	private ServerSocket server;
	private DataManagement dataMangement;
	private boolean isRunning = false;
	private final SecretKey secretKey;
	private final int port;

	public Server(char[] password, int port, boolean firsttime) {

		this.port = port;
		dataMangement = new DataManagement(null);
		this.passwordForData = password;
		if (KeyStoreSynchron.getInstance().loadKeyStore(password, firsttime)) {

			if (firsttime) {
				secretKey = KeyConverter.generateSynchronKey();
				KeyStoreSynchron.getInstance().saveKey(password, secretKey);
			} else {
				this.secretKey = KeyStoreSynchron.getInstance().getKey(password);
			}
			startServer();
		} else {
			Logger.getInstance().log("Ser002: Cannot reach the older KeyStore! Server will not start!");
			secretKey = null;
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
		while (isRunning) {
			Socket socket;
			try {
				socket = server.accept();
				System.out.println("A new Client is now accepted");
				new Client(socket, this);
			} catch (IOException e) {
				System.err.println("Something went wrong with accepting the socket! #BlameBene");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Tries to register a new User. This needs to be called at first when User
	 * wants to join.
	 * 
	 * @Params are the username and the password
	 *
	 * @return returns an Account of the registered User returns null, if
	 *         something went wrong
	 */

	Account registerUser(String user, String pass, String color) {
		if (user == null || pass == null) {
			Logger.getInstance().log("Ser001: User cannot register because UserPass is null!");
			return null;
		}
		int tag = dataMangement.registerUser(user, pass, color);
		if (tag == 0) {
			return null;
		}
		Account account = new Account(tag, this);
		accounts.add(account);

		return account;
	}

	Account loginAccount(int tag, String passwort) {
		if (!dataMangement.login(tag, passwort))
			return null;

		for (Account a : accounts) {
			if (a.getTag() == tag) {
				return a;
			}
		}
		Account a = new Account(tag, this);
		accounts.add(a);
		return a;
	}

	/**
	 *
	 * @param username
	 *            required for login
	 * @param passwort
	 *            required for login
	 * @return if login can be done returns the Account if the Account is
	 *         already online it just adds a CLient to this Account --> returns
	 *         Account
	 *
	 *         if the login went wrong somehow --> returns null
	 */

	Account loginAccount(String username, String passwort) {

		int tag = dataMangement.login(username, passwort);
		if (tag == -1) {
			Logger.getInstance().log("Ser003: Cannot find the fitting tag to log in @loginAccount()!");
			return null;
		}

		for (Account account : accounts) {
			if (account.getTag() == tag) {
				return account;
			}
		}
		Account a = new Account(tag, this);
		accounts.add(a);
		return a;
	}

	int createGroup(String name, int[] accounts) {

		return dataMangement.createGroup(name, accounts);
	}

	boolean removeFromGroup(int grouptag, int toRemove, int whoWantsToRemoveTag) {
		if (dataMangement.getGroupAdmin(grouptag) == whoWantsToRemoveTag) {
			boolean res = dataMangement.removeFromGroup(toRemove, grouptag);
			if (res) {
				int[] member = dataMangement.getGroupMembers(grouptag);
				for (Account a : accounts) {
					if (a.getTag() != toRemove) {
						if (dataMangement.isInGroup(a.getTag(), grouptag)) {
							a.updateGroupMemberForAllClients(grouptag, member);
						}
					} else {
						a.gotRemovedFromGroup(grouptag);
					}
				}

			}
			return res;

		}
		return false;
	}

	boolean sendGroupInvite(int groups, int usertag) {
		boolean res = dataMangement.addToGroup(usertag, groups);
		if (res) {
			int[] member = dataMangement.getGroupMembers(groups);
			for (Account a : accounts) {
				if (dataMangement.isInGroup(a.getTag(), groups)) {
					if (a.getTag() != usertag)
						a.updateGroupMemberForAllClients(groups, member);
					else {
						a.gotInvitedToGroup(groups, dataMangement.getGroupName(groups), member);
					}
				}
			}
		}
		return false;
	}

	void receiveMessage(int from, Account to, String message, String date) {
		String encrypted = Encrypter.encryptSynchron(message, secretKey);
		dataMangement.saveMessage(from, to.getTag(), date, encrypted);
		for (Account a : accounts) {
			if (a.equals(to)) {
				a.sendMessage(from, message, date);
			}
		}
	}

	boolean removeFriend(int tagToRemove, int tagFromWhichAcc) {
		boolean res = dataMangement.removeFriend(tagFromWhichAcc, tagToRemove);
		if (res) {
			for (Account a : accounts) {
				if (a.getTag() == tagToRemove) {
					a.updateFriends(tagFromWhichAcc);
				}
			}
		}
		return res;
	}

	/**
	 * This Method is called when a User wants to have Messages from a specific
	 * date. Gets a Mailbox from dataManagment and decrypts it. Returns it then.
	 *
	 * @param sender
	 *            The Accound who asks for the Mailbox
	 * @param date
	 *            The specific date the user wants the Messages
	 * @return a decrypted Mailbox
	 */

	Mailbox requestMessage(Account sender, String date) {

		Mailbox returnBox = dataMangement.getMessages(sender.getTag(), date);
		if (returnBox == null)
			return null;
		for (int i = 0; i < returnBox.messageSize(); i++) {
			Message tm = returnBox.getMessage(i);
			tm.setContent(Decrypter.decryptSynchronToString(tm.getContent(), secretKey));
		}

		for (int i = 0; i < returnBox.fileSize(); i++) {
			Message fm = returnBox.getFile(i);
			fm.setContent(Decrypter.decryptSynchronToString(fm.getContent(), secretKey));
		}
		return returnBox;
	}

	int[] getFriendList(int account) {
		return dataMangement.getFriends(account);
	}

	boolean leaveGroup(int accTag, int grpTag) {
		boolean res = dataMangement.removeFromGroup(accTag, grpTag);
		if (res) {
			int[] member = dataMangement.getGroupMembers(grpTag);
			for (Account a : accounts) {
				if (dataMangement.isInGroup(a.getTag(), grpTag)) {
					a.updateGroupMemberForAllClients(grpTag, member);
				}
				if (a.getTag() == accTag) {
					a.gotRemovedFromGroup(grpTag);
				}
			}
		}
		return res;
	}

	DeviceLogin diviceLogin(int tag, int deviceNr) {
		return dataMangement.loginDevice(tag, deviceNr);
	}

	void disconnctAccount(Account account) {
		if (accounts.remove(account)) {
			if (accounts.size() == 0) {
				dataMangement.noUsersLoggedIn();
			}
		}
	}

	void disconnectDevice(int deviceNumber, Account account, boolean timeout) {
		dataMangement.logout(account.getTag(), deviceNumber, timeout);
	}

	// Gruppen Kicken
	// Freund entfernen (Account)
	// Gruppen einladungen versenden

	// Search User

	void dataReceived(int from, int toAcc, byte[] message, String filename, String date) {
		String encrypted = Encrypter.encryptSynchron(message, secretKey);
		dataMangement.saveFile(from, toAcc, date, encrypted);
		for (Account a : accounts) {
			if (a.getTag() == toAcc) {
				a.sendData(from, message, filename, date);
			}
		}
	}

	/**
	 * searches for a User
	 * 
	 * @param username
	 *            the User who is searched
	 * @return UserInfo where all Infos like Color Name and Tag are saved
	 */

	UserInfo searchUser(String username) {
		return dataMangement.getUserInfo(username);
	}

	/**
	 * searches for a User
	 * 
	 * @param tag
	 *            the User who is searched
	 * @return UserInfo where all Infos like Color Name and Tag are saved
	 */
	UserInfo searchUser(int tag) {
		return dataMangement.getUserInfo(tag);
	}

	boolean addFriendTo(Account account, int tagOfAccountToAdd) {
		boolean res = dataMangement.addFriend(account.getTag(), tagOfAccountToAdd);
		if (res) {
			for (Account a : accounts) {
				if (a.getTag() == tagOfAccountToAdd) {
					a.sendBlocked(account, true);
					return res;
				}
			}
		}
		return false;
	}

	//TODO Kein FriendRequest an USer der Erhalten ist

	void declineFriendShip(Account accountWhoDeclines, int tagWhoGetsBlocked) {
		dataMangement.removeFriend(accountWhoDeclines.getTag(), tagWhoGetsBlocked);
		for (Account a : accounts) {
			if (a.getTag() == tagWhoGetsBlocked) {
				a.sendBlocked(accountWhoDeclines, false);
				return;
			}
		}
	}

	public boolean promoteGroupMember(int grpTag, int userWhoWantsToGetAdminTag, int userWhoIsAdminTag) {
		if (dataMangement.getGroupAdmin(grpTag) == userWhoIsAdminTag) {
			boolean res = dataMangement.promoteToAdmin(grpTag, userWhoWantsToGetAdminTag);
			if (!res)
				return false;

			int member[] = dataMangement.getGroupMembers(grpTag);
			for (Account a : accounts) {
				if (dataMangement.isInGroup(a.getTag(), grpTag)) {
					a.updateGroupMemberForAllClients(grpTag, member);
				}
			}

		}
		return false;
	}

	public boolean sendFriendRequest(int toWhomTag, int fromWhomTag) {
		//TODO speichern in DataManagement?

		boolean res = dataMangement.addFriend(fromWhomTag, toWhomTag);
		if (!res)
			return false;
		for (Account a : accounts) {
			if (a.getTag() == toWhomTag) {
				a.receiveFriendRequest(fromWhomTag, dataMangement.getUserName(toWhomTag));
			}
		}
		return true;
	}

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.print("What is the password: ");
		String password = scan.nextLine();
		System.out.print("Is it your first time loggin in (y/n): ");
		String bool = scan.nextLine();
		new Server(password.toCharArray(), 25565, bool.equalsIgnoreCase("y"));
	}
}