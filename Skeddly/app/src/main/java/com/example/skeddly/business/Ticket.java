package com.example.skeddly.business;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.skeddly.business.database.DatabaseObject;
import com.example.skeddly.business.location.CustomLocation;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Tracks a user's entry into an event via a Ticket. This gathers who
 * entered the event. when they did, and where they did all in one object.
 */
public class Ticket extends DatabaseObject {
    private String userId;
    private long ticketTime;
    @Nullable
    private CustomLocation location;
    private TicketStatus status;

    /**
     * No arg constructor for a Ticket. Required by Firebase.
     */
    public Ticket() {}

    /**
     * Constructor for a Ticket.
     * @param userId The id of the user that this ticket is associated with
     * @param location The location that the user is casting the ticket from, or NULL if not given.
     */
    public Ticket(@NonNull String userId, @Nullable CustomLocation location) {
        this.userId = userId;
        this.location = location;
        this.status = TicketStatus.INVITED;

        ZoneId zoneId = ZoneId.systemDefault();
        this.ticketTime = LocalDateTime.now().atZone(zoneId).toEpochSecond();
    }

    /**
     * Constructor for a Ticket without location.
     * @param userId The id of the user that this ticket is associated with
     */
    public Ticket(@NonNull String userId) {
        this(userId, null);
    }

    /**
     * Gets the user who entered the event.
     * @return The user id which entered the event
     */
    @NonNull
    public String getUser() {
        return userId;
    }

    /**
     * Sets the user who entered the event.
     * @param userId The new id of the user that this ticket is associated with
     */
    public void setUser(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the time the user entered the event.
     * @return The time the user entered the event as a unix epoch
     */
    public long getTicketTime() {
        return ticketTime;
    }

    /**
     * Sets the time the user entered the event.
     * @param ticketTime The time the user entered the event as a unix epoch
     */
    public void setTicketTime(long ticketTime) {
        this.ticketTime = ticketTime;
    }

    /**
     * Gets the location the user entered the event.
     * @return The location the user entered the event or NULL if not stored.
     */
    @Nullable
    public CustomLocation getLocation() {
        return location;
    }

    /**
     * Sets the location the user entered the event.
     * @param location The location the user entered the event
     */
    public void setLocation(@Nullable CustomLocation location) {
        this.location = location;
    }

    /**
     * Gets the status of the ticket.
     * @return The status of the ticket
     */
    public TicketStatus getStatus() {return status;}

    /**
     * Sets the status of the ticket.
     * @param status The status of the ticket
     */
    public void setStatus(TicketStatus status) {this.status = status;}
}
