package server;

import java.util.ArrayList;

public class Account {

	private String username;
	private int key;
	private ArrayList<Client> clients;
	private int tag;

	public Account(String username, int key, int tag) {
		this.username = username;
		this.key = key;
		this.tag = tag;
		clients = new ArrayList<>();
	}

	public int getTag() {
		return tag;
	}

	public void addClient(Client client) {
		clients.add(client);
	}
}
