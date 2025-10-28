package com.example.skeddly.business;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a single notification item within the application.
 * This class holds all relevant data for a notification, such as its type, content, and status.
 */
public class Notification {
    /**
     * Defines the different categories a notification can belong to.
     */
    public enum notification_type {
        LOTTERY_STATUS,
        EVENT_STATUS,
        ADMINSTRATIVE,
        INVITATION
    }

    /**
     * Defines the possible states of an invitation notification.
     */
    public enum invitation_status {
        ACCEPTED,
        PENDING,
        REJECTED
    }

    // Fields for the notification object
    private final String id;
    private String title;
    private String message;
    private LocalDateTime timestamp;
    private notification_type type;
    private String eventId; // Used to link to a specific event
    private boolean isRead;
    private invitation_status status; // Specific to invitation notifications

    /**
     * Default constructor for creating a new Notification.
     * Initializes the timestamp to the current time and sets its read status to false.
     */
    public Notification() {
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
        this.status = invitation_status.PENDING;
        this.id = UUID.randomUUID().toString();

    }

    /**
     * Gets the unique identifier of the notification.
     * @return A string representing the notification ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the title of the notification.
     * @return A string containing the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the notification.
     * @param title The string title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the main message content of the notification.
     * @return A string containing the message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the main message content for the notification.
     * @param message The string message to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the timestamp indicating when the notification was created.
     * @return A Date object representing the creation time.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp for the notification.
     * @param timestamp The Date to set.
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the type of the notification.
     * @return The notification_type enum value.
     */
    public notification_type getType() {
        return type;
    }

    /**
     * Sets the type of the notification.
     * @param type The notification_type enum value to set.
     */
    public void setType(notification_type type) {
        this.type = type;
    }

    /**
     * Gets the ID of the event associated with this notification, if any.
     * @return A string representing the event ID.
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the event ID associated with this notification.
     * @param eventId The string event ID to set.
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Checks if the notification has been marked as read.
     * @return A boolean, true if read, false otherwise.
     */
    public boolean isRead() {
        return isRead;
    }

    /**
     * Sets the read status of the notification.
     * @param read The boolean status to set.
     */
    public void setRead(boolean read) {
        isRead = read;
    }

    /**
     * Gets the status of an invitation notification.
     * @return The invitation_status enum value.
     */
    public invitation_status getStatus() {
        return status;
    }

    /**
     * Sets the status for an invitation notification.
     * @param status The invitation_status enum value to set.
     */
    public void setStatus(invitation_status status) {
        this.status = status;
    }
}
