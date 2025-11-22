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
    private String eventId;
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
    public Ticket(@NonNull String userId, @NonNull String eventId, @Nullable CustomLocation location) {
        this.userId = userId;
        this.eventId = eventId;
        this.location = location;
        this.status = TicketStatus.WAITING;

        ZoneId zoneId = ZoneId.systemDefault();
        this.ticketTime = LocalDateTime.now().atZone(zoneId).toEpochSecond();
    }

    /**
     * Constructor for a Ticket without location.
     * @param userId The id of the user that this ticket is associated with
     * @param eventId The id of the event that this ticket is associated with
     */
    public Ticket(@NonNull String userId, @NonNull String eventId) {
        this(userId, eventId, null);
    }

    /**
     * Gets the user id who entered the event.
     * @return The user id which entered the event
     */
    @NonNull
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user id who entered the event.
     * @param userId The new id of the user that this ticket is associated with
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the event id that this ticket is associated with.
     * @return The event id that this ticket is associated with.
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the event id that this ticket is associated with.
     * @param eventId The new event id that this ticket is associated with.
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
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
