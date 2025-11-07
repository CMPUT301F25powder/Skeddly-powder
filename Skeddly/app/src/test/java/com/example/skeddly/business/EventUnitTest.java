package com.example.skeddly.business;

import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.event.EventDetail;
import com.example.skeddly.business.event.EventSchedule;import com.example.skeddly.business.location.CustomLocation;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Unit tests for the Event, EventDetail, and EventSchedule classes.
 */
public class EventUnitTest {

    private Event event;
    private EventDetail eventDetail;
    private EventSchedule eventSchedule;
    private CustomLocation location;
    private String organizerId = "organizer123";
    private byte[] imageBytes = new byte[]{1, 2, 3};

    @Before
    public void setUp() {
        // Setup EventDetail
        ArrayList<String> categories = new ArrayList<>(Arrays.asList("Social", "Community"));
        eventDetail = new EventDetail("Community Meetup", "A casual get-together.", "None", categories);

        // Setup EventSchedule
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusHours(1);
        LocalDateTime end = now.plusHours(3);
        LocalDateTime regStart = now.minusDays(7);
        LocalDateTime regEnd = now.plusHours(1);
        eventSchedule = new EventSchedule(start, end, regStart, regEnd);

        location = new CustomLocation(45.0, -90.0);

        event = new Event(eventDetail, eventSchedule, new LatLng(location.getLatitude(), location.getLongitude()), organizerId, 100, 25, true, imageBytes);
    }

    @Test
    public void testEventSchedule_RegistrationOver() {
        LocalDateTime past = LocalDateTime.now().minusDays(1);
        EventSchedule pastSchedule = new EventSchedule(past, past, past.minusDays(2), past.minusDays(1));
        assertTrue(pastSchedule.isRegistrationOver());

        LocalDateTime future = LocalDateTime.now().plusDays(1);
        EventSchedule futureSchedule = new EventSchedule(future, future, future.minusDays(1), future);
        assertFalse(futureSchedule.isRegistrationOver());
    }

    @Test
    public void testEvent_isJoinable() {
        // Test when joinable
        assertTrue(event.isJoinable());

        // Test when waiting list is full
        WaitingList fullWaitingList = new WaitingList(1);
        fullWaitingList.addTicket("ticket1");
        event.setWaitingList(fullWaitingList);
        assertFalse(event.isJoinable());

        // Reset waiting list
        event.setWaitingList(new WaitingList(100));

        // Test when registration is over
        LocalDateTime past = LocalDateTime.now().minusDays(1);
        EventSchedule pastSchedule = new EventSchedule(past, past, past.minusDays(2), past.minusDays(1));
        event.setEventSchedule(pastSchedule);
        assertFalse(event.isJoinable());
    }
}
