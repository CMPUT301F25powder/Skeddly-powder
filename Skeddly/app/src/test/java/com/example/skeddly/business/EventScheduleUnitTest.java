package com.example.skeddly.business;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.skeddly.business.event.EventSchedule;
import com.google.firebase.database.DatabaseReference;

import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Tests for {@link EventSchedule}.
 */
public class EventScheduleUnitTest {
    /**
     * Tests if detection for if registration is over is working
     * Explicitly tests a scenario where registration should be over
     * <p>
     * - {@link EventSchedule#isRegistrationOver()} must be true
     * @see EventSchedule
     */
    @Test
    public void testIsRegistrationOver() {
        EventSchedule eventSchedule = new EventSchedule(LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        assertTrue(eventSchedule.isRegistrationOver());
    }

    /**
     * Tests if detection for if registration is over is working
     * Explicitly tests a scenario where registration should NOT be over yet
     * <p>
     * - {@link EventSchedule#isRegistrationOver()} must be false
     * @see EventSchedule
     */
    @Test
    public void testIsRegistrationNotOver() {
        EventSchedule eventSchedule = new EventSchedule(LocalDateTime.now(), LocalDateTime.now().plusDays(1), java.time.LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        assertFalse(eventSchedule.isRegistrationOver());
    }
}
