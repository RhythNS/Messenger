package dataManagement;

public class GroupTransfer {

	private int groupTag;
	private int[] tags;

	public GroupTransfer(int groupTag, int[] tags) {
		this.groupTag = groupTag;
		this.tags = tags;
	}

	public int getGroupTag() {
		return groupTag;
	}

	public int[] getTags() {
		return tags;
	}

}
