package com.example.skeddly.business;

import java.util.ArrayList;

public class ParticipantList {
    private final ArrayList<User> userList = new ArrayList<User>();
    private int maxAttend;

    public ParticipantList() {
        maxAttend = Integer.MAX_VALUE;
    }
    public ParticipantList(int maxAttend) {
        this.maxAttend = maxAttend;
    }

    public void addUser(User u) {
        userList.add(u);
    }
    public void remove(User u) {
        userList.remove(u);
    }

    public int getMaxAttend() {
        return maxAttend;
    }

    public ArrayList<User> get() {
        return userList;
    }

}
