package com.example.skeddly;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import android.location.Location;

import com.example.skeddly.business.Ticket;
import com.example.skeddly.business.user.User;

public class TicketUnitTest {
    @Test
    public void testCreateTicketNoLocation() {
        // TODO: When User is finished, use the proper constructor
        User user = new User();
        Ticket ticket = new Ticket(user);

        assertSame(ticket.getUser(), user);
        assertNull(ticket.getLocation());
        assertNotNull(ticket.getTicketTime());
    }

    @Test
    public void testCreateTicketLocation() {
        // TODO: When User is finished, use the proper constructor
        User user = new User();
        Location location = new Location("");
        Ticket ticket = new Ticket(user, location);

        assertSame(ticket.getUser(), user);
        assertSame(ticket.getLocation(), location);
        assertNotNull(ticket.getTicketTime());
    }
}
