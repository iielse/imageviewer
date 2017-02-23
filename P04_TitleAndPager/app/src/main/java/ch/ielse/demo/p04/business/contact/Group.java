package ch.ielse.demo.p04.business.contact;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;
import java.util.List;


public class Group implements Parent<Friend> {
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_NORMAL = 0;
    private int type = TYPE_NORMAL;
    private boolean hasTopSpace;

    private String title;
    private List<Friend> friends;

    public Group(String title, List<Friend> friends) {
        this.title = title;
        this.friends = friends;

        if (friends != null) {
            for (Friend f : friends) f.setGroup(this);
        }
    }

    public Group(String title, List<Friend> friends, int type) {
        this(title, friends);
        this.type = type;
    }

    public boolean isHasTopSpace() {
        return hasTopSpace;
    }

    public Group setHasTopSpace(boolean hasTopSpace) {
        this.hasTopSpace = hasTopSpace;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public List<Friend> getChildList() {
        return friends;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
