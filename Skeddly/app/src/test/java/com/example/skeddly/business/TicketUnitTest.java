package com.example.skeddly.business;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.example.skeddly.business.location.CustomLocation;
import com.example.skeddly.business.user.User;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class TicketUnitTest {
    @Test
    public void testTicketUserLinked() {
        User user = new User();
        Ticket ticket = new Ticket(user.getId());

        assertSame(ticket.getUser(), user.getId());
    }

    @Test
    public void testTicketLocation() {
        User user = new User();
        CustomLocation location = new CustomLocation(0, 0);
        Ticket ticketLocation = new Ticket(user.getId(), location);
        Ticket ticketNoLocation = new Ticket(user.getId());

        assertNotNull(ticketLocation.getLocation());
        assertNull(ticketNoLocation.getLocation());
    }

    @Test
    public void testTicketTime() {
        User user = new User();
        Ticket ticket = new Ticket(user.getId());

        ZoneId zoneId = ZoneId.systemDefault();
        long curEpoch = LocalDateTime.now().atZone(zoneId).toEpochSecond();

        assertTrue(ticket.getTicketTime() - curEpoch <= 2);
    }
}
