package com.example.skeddly.business.user;

import android.annotation.SuppressLint;

import com.example.skeddly.business.Event;
import com.example.skeddly.business.database.DatabaseObject;

import java.util.ArrayList;

public class User extends DatabaseObject {
    private boolean admin;
    private NotificationSettings notificationSettings;

    private ArrayList<Event> ownedEvents;
    public ArrayList<Event> joinedEvents;

    @SuppressLint("HardwareIds")
    public User() {
        this.ownedEvents = new ArrayList<Event>();
        this.notificationSettings = new NotificationSettings();

//        this.isAdmin = false; // Enforced in realtime db rules.
    }

    public ArrayList<Event> getOwnedEvents() {
        return ownedEvents;
    }

    public void setOwnedEvents(ArrayList<Event> ownedEvents) {
        this.ownedEvents = ownedEvents;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public NotificationSettings getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(NotificationSettings notificationSettings) {
        this.notificationSettings = notificationSettings;
    }
}
