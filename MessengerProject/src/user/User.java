package user;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class User {

	private String name;
	private String private_key;
	private ArrayList<Contact> contacts;
	private ArrayList<Group> Groupn;
	private Client client;

	public User(String name) {
		this.name = name;
	}

	public void sendMassage(String message, int tag) {
		client.writeMessage(tag, message);
	}

	public void sendData(File file, int tag) {
		client.writeData(tag, file);
	}

	public boolean l�schenGroup(Group Group) {
		if (Groupn.contains(Group)) {
			return Groupn.remove(Group);
		}
		System.out.println("Group nicht vorhanden #BlameBen�s");
		return false;
	}

	public boolean GroupUmbenennen(Group Group, String neuerName) {
		if (!(Group == null || neuerName == null)) {
			for (Group eineGroup : Groupn) {
				if (eineGroup.getGroupnName().equals(Group.getGroupnName())) {
					Group.setGroupnName(neuerName);
					return true;
				}
			}
			System.err.println("Die Group ist nicht in deiner Liste vorhanden");
			return false;
		} else {
			System.err.println("Geben Sie einen g�ltigen Groupn Namen ein");
		}
		return false;
	}

	public ArrayList<Contact> getContacts() {
		return contacts;
	}
	public static void getMD5Hash() throws NoSuchAlgorithmException{
		String password= "test";
		MessageDigest m= MessageDigest.getInstance("MD5");
		m.reset();
		m.update(password.getBytes());
		byte[] digest= m.digest();
		BigInteger bigInt= new BigInteger(1,digest);
		String hashtext= bigInt.toString(16);	
		while(hashtext.length()<32){
			hashtext="0"+hashtext;
		}
		System.out.println(hashtext);
	}
	public static void main(String[] args) throws NoSuchAlgorithmException {
		getMD5Hash();
		
	}
	
}
