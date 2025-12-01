package com.example.skeddly.business.user;

import com.example.skeddly.business.database.DatabaseObject;
import com.example.skeddly.business.event.Event;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;

/**
 * A user that can be serialized into the DB
 */
public class User extends DatabaseObject {
    private NotificationSettings notificationSettings;
    private PersonalInformation personalInformation;
    private UserLevel privilegeLevel;
    private String fcmToken;

    /**
     * Constructor for the User
     */
    public User() {
        this.personalInformation = new PersonalInformation();
        this.notificationSettings = new NotificationSettings();
        this.privilegeLevel = UserLevel.ENTRANT;
        this.fcmToken = null;
    }

    public User(PersonalInformation personalInformation, UserLevel privilegeLevel) {
        this.personalInformation = personalInformation;
        this.notificationSettings = new NotificationSettings();
        this.privilegeLevel = privilegeLevel;
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
     * Retrieves the FCM Token tied to this user. This token is used for sending push notifications.
     * @return The FCM token tied to this user.
     */
    public String getFcmToken() {
        return fcmToken;
    }

    /**
     * Sets the FCM token tied to this user. This token is used for sending push notifications.
     * @param fcmToken The new FCM token for this user
     */
    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
