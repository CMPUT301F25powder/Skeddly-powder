package com.example.skeddly.business;

import java.util.ArrayList;

/**
 * Class to represent a user's notification inbox
 */
public class Inbox {
    private final ArrayList<Notification> notifications;

    /**
     * Constructor for inbox. Simply initializes an empty list
     */
    public Inbox() {
        notifications = new ArrayList<Notification>();
    }

    /**
     * Adds a {@link Notification} to the inbox.
     * @param notification Notification to add
     */
    public void addNotification(Notification notification) {
        notifications.add(notification);
    }

    /**
     * Removes a {@link Notification} from the inbox
     * @param index Position of notification to remove
     */
    public void removeNotification(int index) {
        notifications.remove(index);
    }
}
