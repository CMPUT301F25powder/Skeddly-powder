package com.example.skeddly.business;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.skeddly.business.event.EventSchedule;

import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;

public class EventScheduleUnitTest {
    @Test
    public void testIsRegistrationOver() {
        EventSchedule eventSchedule = new EventSchedule(LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        assertTrue(eventSchedule.isRegistrationOver());
    }

    @Test
    public void testIsRegistrationNotOver() {
        EventSchedule eventSchedule = new EventSchedule(LocalDateTime.now(), LocalDateTime.now().plusDays(1), java.time.LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        assertFalse(eventSchedule.isRegistrationOver());
    }
}
