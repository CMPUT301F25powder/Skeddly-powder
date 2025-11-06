package com.example.skeddly.business.user;

import android.annotation.SuppressLint;

import com.example.skeddly.business.database.DatabaseObject;
import com.example.skeddly.business.event.Event;

import java.util.ArrayList;

public class User extends DatabaseObject {
    private NotificationSettings notificationSettings;
    private PersonalInformation personalInformation;
    private ArrayList<String> ownedEvents;
    private ArrayList<String> joinedEvents;
    private UserLevel privilegeLevel;
    private ArrayList<String> inbox;

    @SuppressLint("HardwareIds")
    public User() {
        this.ownedEvents = new ArrayList<>();
        this.personalInformation = new PersonalInformation();
        this.notificationSettings = new NotificationSettings();
        this.privilegeLevel = UserLevel.ENTRANT;
        this.inbox = new ArrayList<String>();
    }

    public ArrayList<String> getOwnedEvents() {
        return ownedEvents;
    }

    public void setOwnedEvents(ArrayList<String> ownedEvents) {
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

    public PersonalInformation getPersonalInformation() {
        return personalInformation;
    }

    public void setPersonalInformation(PersonalInformation personalInformation) {
        this.personalInformation = personalInformation;
    }

    public void addOwnedEvent(Event event) {
        ownedEvents.add(event.getId());
    }

    public ArrayList<String> getJoinedEvents() {
        return joinedEvents;
    }

    public void setJoinedEvents(ArrayList<String> joinedEvents) {
        this.joinedEvents = joinedEvents;
    }

    public ArrayList<String> getInbox() {
        return inbox;
    }

    public void setInbox(ArrayList<String> inbox) {
        this.inbox = inbox;
    }
}
