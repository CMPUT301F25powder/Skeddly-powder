package com.example.skeddly.business;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.skeddly.business.database.DatabaseObject;
import com.example.skeddly.business.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Tracks a user's entry into an event via a Ticket. This gathers who
 * entered the event. when they did, and where they did all in one object.
 */
public class Ticket extends DatabaseObject {
    @NonNull
    private String userId;
    @NonNull
    private long ticketTime;
    @Nullable
    private Location location;

    public Ticket(@NonNull String userId, @Nullable Location location) {
        this.userId = userId;
        this.location = location;

        ZoneId zoneId = ZoneId.systemDefault();
        this.ticketTime = LocalDateTime.now().atZone(zoneId).toEpochSecond();
    }

    public Ticket(@NonNull String entrantId) {
        this(entrantId, null);
    }

    @NonNull
    public String getUser() {
        return userId;
    }

    public void setUser(String userId) {
        this.userId = userId;
    }

    @NonNull
    public long getTicketTime() {
        return ticketTime;
    }

    public void setTicketTime(@NonNull long ticketTime) {
        this.ticketTime = ticketTime;
    }

    @Nullable
    public Location getLocation() {
        return location;
    }

    public void setLocation(@Nullable Location location) {
        this.location = location;
    }
}
