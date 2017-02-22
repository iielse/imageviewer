package ch.ielse.demo.p04.business.contact;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.List;


public class Group implements Parent<Friend> {
    private String title;
    private List<Friend> friends;

    public Group(String title, List<Friend> friends) {
        this.title = title;
        this.friends = friends;
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

    @Override
    public List<Friend> getChildList() {
        return friends;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
