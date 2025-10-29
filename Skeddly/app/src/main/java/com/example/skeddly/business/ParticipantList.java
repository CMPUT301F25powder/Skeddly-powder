package com.example.skeddly.business;

import com.example.skeddly.business.user.User;

import java.util.ArrayList;

/**
 * Class for list of participants in an event.
 */
public class ParticipantList {
    private final ArrayList<User> userList = new ArrayList<User>();
    private int maxAttend;

    /**
     * Constructor for unlimited length participant list.
     */
    public ParticipantList() {
        maxAttend = Integer.MAX_VALUE;
    }

    /**
     * Constructor for constrained participant list
     * @param maxAttend Maximum amount of people that can attend the event.
     */
    public ParticipantList(int maxAttend) {
        this.maxAttend = maxAttend;
    }

    /**
     * Add a user to the participant list
     * @param u User to add to the list
     * @throws IllegalArgumentException If the list is already full
     */
    public void addUser(User u) {
        if (userList.size() < maxAttend) {
            userList.add(u);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Remove a user from the list
     * @param u User to remove from the list
     */
    public void remove(User u) {
        userList.remove(u);
    }

    /**
     * Find the maximum amount of people that are allowed in the list
     * @return Maximum number of attendees
     */
    public int getMaxAttend() {
        return maxAttend;
    }

    /**
     * Update the maximum number of attendees
     * @param maxAttend The new maximum amount of attendees
     */
    void setMaxAttend(int maxAttend) {
        this.maxAttend = maxAttend;
    }

    /**
     * Return the {@link ArrayList} of users
     * @return The user list
     */
    public ArrayList<User> get() {
        return userList;
    }

}
