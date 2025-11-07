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
    @NonNull
    private String userId;
    @NonNull
    private long ticketTime;
    @Nullable
    private CustomLocation location;

    private TicketStatus status;

    /**
     * No arg constructor for a Ticket.
     */
    public Ticket() {}

    /**
     * Constructor for a Ticket.
     * @param userId
     * @param location
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
     * @param entrantId
     */
    public Ticket(@NonNull String entrantId) {
        this(entrantId, null);
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
     * @param userId
     */
    public void setUser(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the time the user entered the event.
     * @return The time the user entered the event
     */
    @NonNull
    public long getTicketTime() {
        return ticketTime;
    }

    /**
     * Sets the time the user entered the event.
     * @param ticketTime The time the user entered the event
     */
    public void setTicketTime(long ticketTime) {
        this.ticketTime = ticketTime;
    }

    /**
     * Gets the location the user entered the event.
     * @return The location the user entered the event
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
