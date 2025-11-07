package com.example.skeddly.business;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Unit tests for the Notification business object.
 * Verifies constructor logic and getter/setter functionality.
 */
public class NotificationUnitTest {

    private Notification notification;

    /**
     * This method is executed before each test.
     * It creates a fresh instance of the Notification class.
     */
    @Before
    public void setUp() {
        notification = new Notification();
    }

    /**
     * Tests the default constructor to ensure all fields are initialized correctly.
     * - ID should be a valid UUID.
     * - isRead should be false.
     * - Status should be PENDING.
     */
    @Test
    public void notification_DefaultConstructor_InitializesCorrectly() {
        // 1. Verify ID is a valid, non-null UUID string
        assertNotNull("ID should not be null after creation", notification.getId());
        // This will throw an IllegalArgumentException if the ID is not a valid UUID format
        try {
            UUID.fromString(notification.getId());
        } catch (IllegalArgumentException e) {
            fail("ID is not a valid UUID string.");
        }

        // 2. Verify default boolean and enum values
        assertFalse("Notification should be unread by default", notification.isRead());
        assertEquals("Default status should be PENDING",
                Notification.invitation_status.PENDING, notification.getStatus());
    }

    /**
     * Verifies that the getter and setter for the Title field work as expected.
     */
    @Test
    public void notification_GetAndSetTitle() {
        String testTitle = "Lottery Results";
        notification.setTitle(testTitle);
        assertEquals("Getter for Title should return the same value set by the setter",
                testTitle, notification.getTitle());
    }

    /**
     * Verifies that the getter and setter for the Message field work as expected.
     */
    @Test
    public void notification_GetAndSetMessage() {
        String testMessage = "You have won the grand prize!";
        notification.setMessage(testMessage);
        assertEquals("Getter for Message should return the same value set by the setter",
                testMessage, notification.getMessage());
    }

    /**
     * Verifies that the getter and setter for the notification_type enum work as expected.
     */
    @Test
    public void notification_GetAndSetType() {
        Notification.notification_type testType = Notification.notification_type.MESSAGES;
        notification.setType(testType);
        assertEquals("Getter for Type should return the same enum value set by the setter",
                testType, notification.getType());
    }

    /**
     * Verifies that the getter and setter for the Event ID field work as expected.
     */
    @Test
    public void notification_GetAndSetEventId() {
        String testEventId = "event_12345";
        notification.setEventId(testEventId);
        assertEquals("Getter for Event ID should return the same value set by the setter",
                testEventId, notification.getEventId());
    }

    /**
     * Verifies that the getter and setter for the isRead boolean work as expected.
     */
    @Test
    public void notification_GetAndSetRead() {
        notification.setRead(true);
        assertTrue("isRead should return true after being set to true", notification.isRead());

        notification.setRead(false);
        assertFalse("isRead should return false after being set to false", notification.isRead());
    }

    /**
     * Verifies that the getter and setter for the invitation_status enum work as expected.
     */
    @Test
    public void notification_GetAndSetStatus() {
        Notification.invitation_status testStatus = Notification.invitation_status.ACCEPTED;
        notification.setStatus(testStatus);
        assertEquals("Getter for Status should return the same enum value set by the setter",
                testStatus, notification.getStatus());
    }
}
