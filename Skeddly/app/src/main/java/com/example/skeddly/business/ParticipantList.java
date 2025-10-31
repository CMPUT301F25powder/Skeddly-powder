package com.example.skeddly.business;

import com.example.skeddly.business.database.DatabaseObject;
import com.example.skeddly.business.database.DatabaseObjects;

import java.util.ArrayList;

/**
 * Class for list of participants in an event.
 */
public class ParticipantList extends DatabaseObject {
    private ArrayList<DatabaseObjects> userIdList = new ArrayList<DatabaseObjects>();
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
     * @param userId User to add to the list
     * @throws IllegalArgumentException If the list is already full
     */
    public void addUser(DatabaseObjects userId) {
        if (userIdList.size() < maxAttend) {
            userIdList.add(userId);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Remove a user from the list
     * @param userId User to remove from the list
     */
    public void remove(String userId) {
        userIdList.remove(userId);
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
    public ArrayList<DatabaseObjects> getUserList() {
        return userIdList;
    }

    public void setUserList(ArrayList<DatabaseObjects> userIdList) {
        this.userIdList = userIdList;
    }

}
