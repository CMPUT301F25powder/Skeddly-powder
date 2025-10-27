package com.example.skeddly.business.user;

import android.annotation.SuppressLint;

import com.example.skeddly.business.Event;
import com.example.skeddly.business.database.DatabaseObject;

import java.util.ArrayList;

public class User extends DatabaseObject {
    private NotificationSettings notificationSettings;
    private PersonalInformation personalInformation;
    private ArrayList<Event> ownedEvents;
    private ArrayList<Event> joinedEvents;
    private UserLevel privilegeLevel;

    @SuppressLint("HardwareIds")
    public User() {
        this.ownedEvents = new ArrayList<>();
        this.personalInformation = new PersonalInformation();
        this.notificationSettings = new NotificationSettings();
        this.privilegeLevel = UserLevel.ENTRANT;
    }

    public ArrayList<Event> getOwnedEvents() {
        return ownedEvents;
    }

    public void setOwnedEvents(ArrayList<Event> ownedEvents) {
        this.ownedEvents = ownedEvents;
    }

    public NotificationSettings getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(NotificationSettings notificationSettings) {
        this.notificationSettings = notificationSettings;
    }

    public UserLevel getPrivilegeLevel() {
        return privilegeLevel;
    }

    public void setPrivilegeLevel(UserLevel privilegeLevel) {
        this.privilegeLevel = privilegeLevel;
    }

    public PersonalInformation getExtraInformation() {
        return personalInformation;
    }

    public void setExtraInformation(PersonalInformation personalInformation) {
        this.personalInformation = personalInformation;
    }
}
