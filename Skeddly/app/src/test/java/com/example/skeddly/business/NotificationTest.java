package com.example.skeddly.business;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.UUID;

/**
 * Unit tests for the Notification business object.
 */
public class NotificationTest {

    private Notification notification;

    @Before
    public void setUp() {
        notification = new Notification();
    }

    @Test
    public void notification_DefaultConstructor_InitializesCorrectly() {
        assertNotNull("Timestamp should not be null after creation", notification.getTimestamp());
        assertFalse("Notification should be unread by default", notification.isRead());
    }

    @Test
    public void notification_GetAndSetId() {
        String testId = UUID.randomUUID().toString();
        notification.setId(testId);
        assertEquals("Getter for ID should return the same value set by the setter", testId, notification.getId());
    }

    @Test
    public void notification_GetAndSetTitle() {
        String testTitle = "New Event Invitation";
        notification.setTitle(testTitle);
        assertEquals("Getter for Title should return the same value set by the setter", testTitle, notification.getTitle());
    }


    @Test
    public void notification_GetAndSetMessage() {
        String testMessage = "You have been invited to a new event.";
        notification.setMessage(testMessage);
        assertEquals("Getter for Message should return the same value set by the setter", testMessage, notification.getMessage());
    }

    @Test
    public void notification_GetAndSetTimestamp() {
        Date newDate = new Date(System.currentTimeMillis() - 10000); // A time in the past
        notification.setTimestamp(newDate);
        assertEquals("Getter for Timestamp should return the same value set by the setter", newDate, notification.getTimestamp());
    }


    @Test
    public void notification_GetAndSetType() {
        Notification.notification_type testType = Notification.notification_type.INVITATION;
        notification.setType(testType);
        assertEquals("Getter for Type should return the same enum value set by the setter", testType, notification.getType());
    }

    @Test
    public void notification_GetAndSetStatus() {
        Notification.invitation_status testStatus = Notification.invitation_status.PENDING;
        notification.setStatus(testStatus);
        assertEquals("Getter for Status should return the same enum value set by the setter", testStatus, notification.getStatus());
    }

    @Test
    public void notification_GetAndSetReadStatus() {
        notification.setRead(true);
        assertTrue("isRead should return true after being set to true", notification.isRead());
    }
}
