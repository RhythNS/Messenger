package server;

public class Account {

	private String username;
	private String passwort;
	private String key;
	private Client client;
	private int tag;

	public Account(String username, String passwort, String key, int tag) {
		this.username = username;
		this.passwort = passwort;
		this.key = key;
		this.tag = tag;
	}



}
