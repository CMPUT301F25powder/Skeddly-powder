package com.example.skeddly.business.user;

import com.example.skeddly.business.database.DatabaseObject;
import com.example.skeddly.business.database.DatabaseObjects;
import com.example.skeddly.business.notification.Notification;
import com.google.firebase.firestore.Exclude;

/**
 * A user that can be serialized into the DB
 */
public class User extends DatabaseObject {
    private PersonalInformation personalInformation;
    private NotificationSettings notificationSettings;
    private UserLevel privilegeLevel;
    private DatabaseObjects<Notification> notifications;

    /**
     * Constructor for the User
     */
    public User() {
        this.personalInformation = new PersonalInformation();
        this.notificationSettings = new NotificationSettings();
        this.privilegeLevel = UserLevel.ENTRANT;
        this.notifications = new DatabaseObjects<>(Notification.class);
    }

    public User(String id) {
        this();
        setId(id);
    }

    /**
     * Gets the personal information of the user
     * @return The personal information of the user
     */
    public PersonalInformation getPersonalInformation() {
        return personalInformation;
    }

    /**
     * Sets the personal information of the user
     * @param personalInformation The personal information to set for the user
     */
    public void setPersonalInformation(PersonalInformation personalInformation) {
        this.personalInformation = personalInformation;
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


    public void addNotification(Notification notification) {
        this.notifications.add(notification);
    }
}
