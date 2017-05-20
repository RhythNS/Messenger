package server;

import java.util.ArrayList;

public class Account {

	private String username;
	private String password;
	private String key;
	private ArrayList<Client> clients;
	private int tag;

	public Account(String username, String password, String key, int tag) {
		this.username = username;
		this.password = password;
		this.key = key;
		this.tag = tag;
		clients = new ArrayList<>();
	}



}
