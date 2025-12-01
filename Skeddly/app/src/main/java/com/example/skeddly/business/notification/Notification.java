package com.example.skeddly.business.notification;

import com.example.skeddly.business.database.DatabaseObject;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Represents a single notification item within the application.
 * This class holds all relevant data for a notification, such as its type, content, and status.
 */
public class Notification extends DatabaseObject {
    // Fields for the notification object
    private String title;
    private String message;
    private String recipient;
    private String ticketId;
    private long timestamp;
    private NotificationType type;
    private NotificationInvitationStatus status; // Specific to invitation notifications
    private boolean read;

    /**
     * Default constructor for creating a new Notification.
     * Sets its read status to false and status to pending.
     */
    public Notification() {
        this.read = false;
        this.status = NotificationInvitationStatus.PENDING;
        this.type = NotificationType.MESSAGES;

        ZoneId zoneId = ZoneId.systemDefault();
        this.timestamp = LocalDateTime.now().atZone(zoneId).toEpochSecond();
    }

    /**
     * Construct a notification with the given title and message.
     * @param title The title to set on the notification.
     * @param message The message that it should contain.
     * @param recipient The user id of the recipient of the message.
     * @param type The type of notification it is
     */
    public Notification(String title, String message, String recipient, NotificationType type) {
        this();
        this.setTitle(title);
        this.setMessage(message);
        this.setRecipient(recipient);
        this.setType(type);
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
     * Gets the recipient of the notification.
     * @return The user id of the recipient
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Sets the recipient of the notification.
     * @param recipient The user id of the recipient
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
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
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp for the notification.
     * @param timestamp The Date to set.
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the type of the notification.
     * @return The NotificationType enum value.
     */
    public NotificationType getType() {
        return type;
    }

    /**
     * Sets the type of the notification.
     * @param type The NotificationType enum value to set.
     */
    public void setType(NotificationType type) {
        this.type = type;
    }

    /**
     * Gets the ID of the ticket associated with this notification, if any.
     * @return A string representing the ticket ID.
     */
    public String getTicketId() {
        return ticketId;
    }

    /**
     * Sets the ticket ID associated with this notification.
     * @param ticketId The string ticket ID to set.
     */
    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    /**
     * Checks if the notification has been marked as read.
     * @return A boolean, true if read, false otherwise.
     */
    public boolean isRead() {
        return read;
    }

    /**
     * Sets the read status of the notification.
     * @param read The boolean status to set.
     */
    public void setRead(boolean read) {
        this.read = read;
    }

    /**
     * Gets the status of an invitation notification.
     * @return The NotificationInvitationStatus enum value.
     */
    public NotificationInvitationStatus getStatus() {
        return status;
    }

    /**
     * Sets the status for an invitation notification.
     * @param status The NotificationInvitationStatus enum value to set.
     */
    public void setStatus(NotificationInvitationStatus status) {
        this.status = status;
    }
}
