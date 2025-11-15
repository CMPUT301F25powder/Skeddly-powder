package com.example.skeddly.business.user;

import com.example.skeddly.business.database.DatabaseObject;
import com.example.skeddly.business.database.DatabaseObjects;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.notification.Notification;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;

/**
 * A user that can be serialized into the DB
 */
public class User extends DatabaseObject {
    private NotificationSettings notificationSettings;
    private PersonalInformation personalInformation;
    private ArrayList<String> ownedEvents;
    private ArrayList<String> joinedEvents;
    private UserLevel privilegeLevel;
    private DatabaseObjects<Notification> notifications;

    /**
     * Constructor for the User
     */
    public User() {
        this.ownedEvents = new ArrayList<>();
        this.personalInformation = new PersonalInformation();
        this.notificationSettings = new NotificationSettings();
        this.privilegeLevel = UserLevel.ENTRANT;
        this.notifications = new DatabaseObjects<>(Notification.class);
    }

    /**
     * Gets the events the user owns
     * @return The events the user owns
     */
    public ArrayList<String> getOwnedEvents() {
        return ownedEvents;
    }

    /**
     * Sets the events the user owns
     * @param ownedEvents The events the user owns
     */
    public void setOwnedEvents(ArrayList<String> ownedEvents) {
        this.ownedEvents = ownedEvents;
    }

    /**
     * Gets the notification settings
     * @return The notification settings
     */
    public NotificationSettings getNotificationSettings() {
        return notificationSettings;
    }

    /**
     * Sets the notification settings
     * @param notificationSettings The notification settings
     */
    public void setNotificationSettings(NotificationSettings notificationSettings) {
        this.notificationSettings = notificationSettings;
    }

    /**
     * Gets the privilege level
     * @return The privilege level
     */
    public UserLevel getPrivilegeLevel() {
        return privilegeLevel;
    }

    /**
     * Sets the privilege level
     * @param privilegeLevel The privilege level
     */
    public void setPrivilegeLevel(UserLevel privilegeLevel) {
        this.privilegeLevel = privilegeLevel;
    }

    /**
     * Gets the personal information
     * @return The personal information
     */
    public PersonalInformation getPersonalInformation() {
        return personalInformation;
    }

    /**
     * Sets the personal information
     * @param personalInformation The personal information
     */
    public void setPersonalInformation(PersonalInformation personalInformation) {
        this.personalInformation = personalInformation;
    }

    /**
     * Add to user's owned events array (add events id)
     * @param event The event to add
     */
    public void addOwnedEvent(Event event) {
        ownedEvents.add(event.getId());
    }

    /**
     * Getss the list of events the user has joined.
     * @return An array list of event ids.
     */
    @Exclude // Exclude so we can handle ourselves
    public ArrayList<String> getJoinedEvents() {
        return joinedEvents;
    }

    /**
     * Sets which events the user has joined.
     * @param joinedEvents An array list of event ids
     */
    @Exclude // Exclude so we can handle ourselves
    public void setJoinedEvents(ArrayList<String> joinedEvents) {
        this.joinedEvents = joinedEvents;
    }

    /**
     * Gets the list of notifications that the user has
     * @return DatabaseObjects list of notifications.
     */
    @Exclude // Exclude so we can handle ourselves
    public DatabaseObjects<Notification> getNotifications() {
        return notifications;
    }

    /**
     * Sets the list of notifications
     * @param notifications DatabaseObjects list of notifications.
     */
    @Exclude // Exclude so we can handle ourselves
    public void setNotifications(DatabaseObjects<Notification> notifications) {
        this.notifications = notifications;
    }
}
