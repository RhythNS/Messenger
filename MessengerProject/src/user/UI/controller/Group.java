package user.UI.controller;

import javafx.scene.control.Label;

public class Group {

    private user.Group group;
    public Label name;

    public Group() {

    }

    public user.Group getGroup() {
        return group;
    }

    public void setGroup(user.Group group) {
        this.group = group;
        name.setText(group.getGroupName());
    }

    public void select() {

    }

    public void unselect() {

    }
}
