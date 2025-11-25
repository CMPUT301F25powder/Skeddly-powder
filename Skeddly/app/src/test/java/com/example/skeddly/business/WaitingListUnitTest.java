package com.example.skeddly.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.UUID;

public class WaitingListUnitTest {

    private WaitingList waitingList;
    private WaitingList boundedWaitingList;
    private static final int WAITING_BOUND = 3;

    @Before
    public void setup() {
        waitingList = new WaitingList();
        boundedWaitingList = new WaitingList(WAITING_BOUND);
    }

    @Test
    public void testWaitlistIsEmpty() {
        assertTrue(waitingList.isEmpty());
        assertTrue(boundedWaitingList.isEmpty());

        waitingList.addTicket(UUID.randomUUID().toString());
        boundedWaitingList.addTicket(UUID.randomUUID().toString());

        assertFalse(waitingList.isEmpty());
        assertFalse(boundedWaitingList.isEmpty());
    }

    @Test
    public void testWaitlistDrawEmpty() {
        assertThrows(IllegalStateException.class, () -> waitingList.draw());
    }

    @Test
    public void testWaitlistInsertFull() {
        for (int i = 0; i < WAITING_BOUND; ++i) {
            boundedWaitingList.addTicket(UUID.randomUUID().toString());
        }

        assertThrows(IllegalArgumentException.class, () -> boundedWaitingList.addTicket(UUID.randomUUID().toString()));
    }

    @Test
    public void testWaitlistIsFull() {
        assertFalse(boundedWaitingList.isFull());

        for (int i = 0; i < WAITING_BOUND; ++i) {
            boundedWaitingList.addTicket(UUID.randomUUID().toString());
        }

        assertTrue(boundedWaitingList.isFull());
    }

    @Test
    public void testWaitlistLimitWorks() {
        assertEquals(Integer.MAX_VALUE, waitingList.getMax());
        assertEquals(WAITING_BOUND, boundedWaitingList.getMax());
    }

    @Test
    public void testWaitlistAddTicket() {
        Ticket myTicket = new Ticket();

        waitingList.addTicket(myTicket.getId());
        assertEquals(1, waitingList.size());
    }

    @Test
    public void testWaitlistDraw() {
        Ticket[] myTickets = {new Ticket(), new Ticket(), new Ticket()};
        String[] ticketIds = {myTickets[0].getId(), myTickets[1].getId(), myTickets[2].getId()};
        String[] drawn = new String[ticketIds.length];

        for (String ticketId : ticketIds) {
            waitingList.addTicket(ticketId);
        }

        for (int i = 0; i < myTickets.length; ++i) {
            drawn[i] = waitingList.draw();
        }

        Arrays.sort(ticketIds);
        Arrays.sort(drawn);

        for (int i = 0; i < drawn.length; ++i) {
            assertEquals(ticketIds[i], drawn[i]);
        }
    }
}
