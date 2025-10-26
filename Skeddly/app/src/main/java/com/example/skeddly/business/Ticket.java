package com.example.skeddly.business;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.skeddly.business.user.User;

import java.time.LocalDateTime;

/**
 * Tracks a user's entry into an event via a Ticket. This gathers who
 * entered the event. when they did, and where they did all in one object.
 */
public class Ticket {
    @NonNull
    private final User user;
    @NonNull
    private final LocalDateTime ticketTime;
    @Nullable
    private final Location location;

    public Ticket(@NonNull User user, @Nullable Location location) {
        this.user = user;
        this.location = location;
        this.ticketTime = LocalDateTime.now();
    }

    public Ticket(@NonNull User entrant) {
        this(entrant, null);
    }

    @NonNull
    public User getUser() {
        return user;
    }

    @NonNull
    public LocalDateTime getTicketTime() {
        return ticketTime;
    }

    @Nullable
    public Location getLocation() {
        return location;
    }
}
