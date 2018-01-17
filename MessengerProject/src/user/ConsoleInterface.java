package user;

import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleInterface {

	private static User user = new User();

	private static String getMenu() {
		return "-3: Exit\n-2: Register\n-1: Login\n0: Send friend request\n1: Answer friend request\n2: Write message\n3: Print all lists"
				+ "\n4: Leave group\n5: Add someone to a group\n6: Remove someone from group\n7: Promoto someone to admin"
				+ "\n8: Remove friend\n9: Create group";
	}

	public static void main(String[] args) {
		System.out.println(getMenu());
		Scanner scan = new Scanner(System.in);
		int selection = 0;
		System.out.print("Print the menu everytime? (y/n) ");
		boolean running = true, menuPls = scan.nextLine().equalsIgnoreCase("y");
		while (running) {
			if (menuPls)
				System.out.println(getMenu());
			selection = scan.nextInt();
			scan.nextLine();
			switch (selection) {
			case -3: // exit
				running = false;
				user.disconnect();
				System.out.println("Hopefully done!");
				break;
			case -2: // register
				while (true) {
					System.out.print("username: ");
					String username = scan.nextLine();
					System.out.print("password: ");
					String password = scan.nextLine();
					System.out.print("color in hex: ");
					String color = scan.nextLine();
					if (user.register(username, password, color))
						break;
					System.out.println("Something went wrong! Try again!");
				}
				System.out.println("Hopefully done!");
				break;
			case -1: // login
				while (true) {
					System.out.print("username: ");
					String username = scan.nextLine();
					System.out.print("password: ");
					String password = scan.nextLine();
					if (user.login(username, password))
						break;
					System.out.println("Something went wrong! Try again!");
				}
				System.out.println("Hopefully done!");
				break;
			case 0: // send friend request
				System.out.print("Enter tag: ");
				int tag = scan.nextInt();
				scan.nextLine();
				user.sendFriendRequest(tag);
				System.out.println("Hopefully done!");
				break;
			case 1: // answer friend request
				Contact friend = contactThings(scan, user.getPendingFriends());
				if (friend == null)
					return;
				System.out.print("accept friend? (y/n)");
				boolean accepted = scan.nextLine().equalsIgnoreCase("y");
				user.answerFriendRequest(friend, accepted);
				System.out.println("Hopefully done!");
				break;
			case 2: // write message
				System.out.print("is it a group? (y/n)");
				boolean isGroup = scan.nextLine().equalsIgnoreCase("y");
				System.out.print("Enter tag");
				tag = scan.nextInt();
				scan.nextLine();
				System.out.print("Message: ");
				String message = scan.nextLine();
				if (isGroup) {
					Group group = getGroup(tag);
					if (group == null) {
						System.out.println("Tag is wrong");
						break;
					}
					user.writeMessage(group, message);
				} else {// is not group
					Contact messageContact = getContact(user.getFriendlist(), tag);
					if (messageContact == null) {
						System.out.println("Tag is wrong");
						break;
					}
					user.writeMessage(messageContact, message);
				}
				System.out.println("Hopefully done!");
				break;
			case 3: // print all lists
				System.out.println(user.getFriendlist() + "\n" + user.getPendingFriends() + "\n"
						+ user.getRequestedFriends() + "\n" + user.getGroups());
				break;
			case 4: // leave group 
				System.out.print("Enter tag");
				tag = scan.nextInt();
				scan.nextLine();
				Group group2 = getGroup(tag);
				if (group2 == null) {
					System.out.println("Group not found");
					break;
				}
				user.leaveGroup(group2);
				System.out.println("Hopefully done!");
				break;
			case 5: // add to group
				Object[] arr = groupThings(scan);
				if (arr == null)
					break;
				user.inviteToGroup((Contact) arr[1], (Group) arr[0]);
				System.out.println("Hopefully done!");
			case 6: // remove from group
				arr = groupThings(scan);
				if (arr == null)
					break;
				user.kickGroupMember((Contact) arr[1], (Group) arr[0]);
				System.out.println("Hopefully done!");
			case 7: // promote someone
				arr = groupThings(scan);
				if (arr == null)
					break;
				user.promoteToGroupLeader((Contact) arr[1], (Group) arr[0]);
				System.out.println("Hopefully done!");
			case 8: // remove friend
				Contact contact = contactThings(scan, user.getFriendlist());
				if (contact == null)
					break;
				user.removeFriend(contact);
				System.out.println("Hopefully done!");
			case 9: // create group
				System.out.print("Enter name: ");
				String name = scan.nextLine();
				ArrayList<Contact> contacts = new ArrayList<>();
				while (true) {
					System.out.println("enter tag (-1 to quit): ");
					tag = scan.nextInt();
					scan.nextLine();
					if (tag == -1)
						break;
					Contact con = contactThings(scan, user.getFriendlist());
					if (con != null) {
						contacts.add(con);
					}
				}
				if (contacts.isEmpty()) {
					System.out.println("Nobody in this list. Not doing anything!");
					return;
				}
				user.createGroup(contacts, name);
				System.out.println("Hopefully done!");
			default:
				break;
			}
		}
		scan.close();
	}

	/**
	 * 
	 * 0 = friendlist, 1 = pending, 2 = requested, 3 = unsortedGroupMembers
	 */
	private static Contact getContact(ArrayList<Contact> contacts, int tag) {
		for (int i = 0; i < contacts.size(); i++)
			if (contacts.get(i).getTag() == tag)
				return contacts.get(i);
		return null;
	}

	private static Group getGroup(int tag) {
		for (int i = 0; i < user.getGroups().size(); i++)
			if (user.getGroups().get(i).getTag() == tag)
				return user.getGroups().get(i);
		return null;
	}

	private static Contact contactThings(Scanner scan, ArrayList<Contact> contacts) {
		System.out.print("Enter tag");
		int tag = scan.nextInt();
		scan.nextLine();
		Contact contact = ConsoleInterface.getContact(contacts, tag);
		if (contact == null) {
			System.out.println("Guy not found in list!");
			return null;
		}
		return contact;
	}

	private static Object[] groupThings(Scanner scan) {
		System.out.print("Enter Group tag: ");
		int tag = scan.nextInt();
		scan.nextLine();
		Group group = getGroup(tag);
		if (group == null) {
			System.out.print("Group not found!");
			return null;
		}
		System.out.print("Enter to add Tag: ");
		tag = scan.nextInt();
		scan.nextLine();
		Contact contact = ConsoleInterface.getContact(user.getFriendlist(), tag);
		if (contact == null) {
			System.out.println("Guy not found!");
			return null;
		}
		Object[] arr = new Object[2];
		arr[0] = group;
		arr[1] = contact;
		return arr;
	}

}
