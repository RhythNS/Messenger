package dataManagement;

import java.util.ArrayList;

public class Mailbox {

	ArrayList<Message> messages;
	ArrayList<Message> files;
	ArrayList<Integer> friends, requests, pending;
	ArrayList<ColorTransfer> colors;
	ArrayList<GroupTransfer> groupTransfers;

	Mailbox() {
		messages = new ArrayList<>();
		files = new ArrayList<>();
		friends = new ArrayList<>();
		requests = new ArrayList<>();
		pending = new ArrayList<>();
		colors = new ArrayList<>();
	}

	public Message getMessage(int i) {
		return messages.get(i);
	}

	public Message getFile(int i) {
		return files.get(i);
	}

	public int getFriends(int i) {
		return friends.get(i);
	}

	public int getRequests(int i) {
		return requests.get(i);
	}

	public int getPending(int i) {
		return pending.get(i);
	}

	public ColorTransfer getColor(int i) {
		return colors.get(i);
	}

	public GroupTransfer getGroup(int i){
		return groupTransfers.get(i);
	}

	public int friendSize() {
		return friends.size();
	}

	public int requestSize() {
		return requests.size();
	}

	public int pendingSize() {
		return pending.size();
	}

	public int fileSize() {
		return files.size();
	}

	public int messageSize() {
		return messages.size();
	}

	public int colorSize() {
		return colors.size();
	}

	public int groupSize() {
		return groupTransfers.size();
	}

}
