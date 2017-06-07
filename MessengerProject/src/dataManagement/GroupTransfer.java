package dataManagement;

public class GroupTransfer {

	private int groupTag;
	private int[] tags;
	private String name;

	public GroupTransfer(int groupTag, int[] tags, String name) {
		this.groupTag = groupTag;
		this.tags = tags;
	}

	public int getGroupTag() {
		return groupTag;
	}

	public int[] getTags() {
		return tags;
	}

	public String getName() {
		return name;
	}

}
