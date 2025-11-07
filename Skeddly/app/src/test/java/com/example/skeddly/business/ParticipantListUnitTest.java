package com.example.skeddly.business;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Unit tests for the ParticipantList class.
 */
public class ParticipantListUnitTest {

    private ParticipantList participantList;
    private final int MAX_ATTENDEES = 5;

    @Before
    public void setUp() {
        participantList = new ParticipantList(MAX_ATTENDEES);
    }

    @Test
    public void testDefaultConstructor_UnlimitedCapacity() {
        ParticipantList unlimitedList = new ParticipantList();
        assertEquals(Integer.MAX_VALUE, unlimitedList.getMaxAttend());
    }

    @Test
    public void testAddTicket_WithCapacity_ShouldAddTicket() {
        String ticketId = "ticket123";
        participantList.addTicket(ticketId);
        assertTrue(participantList.getTicketIds().contains(ticketId));
        assertEquals(1, participantList.getTicketIds().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddTicket_AtCapacity_ShouldThrowException() {
        // Fill the list to its maximum capacity
        for (int i = 0; i < MAX_ATTENDEES; i++) {
            participantList.addTicket("ticket" + i);
        }
        participantList.addTicket("extraTicket");
    }

    @Test
    public void testRemoveTicket_ExistingTicket_ShouldRemove() {
        String ticketIdToRemove = "ticket_to_remove";
        participantList.addTicket("ticket1");
        participantList.addTicket(ticketIdToRemove);
        participantList.addTicket("ticket3");

        assertEquals(3, participantList.getTicketIds().size());

        participantList.remove(ticketIdToRemove);

        assertEquals(2, participantList.getTicketIds().size());
        assertFalse(participantList.getTicketIds().contains(ticketIdToRemove));
    }

    @Test
    public void testRemoveTicket_NonExistingTicket_ShouldDoNothing() {
        participantList.addTicket("ticket1");
        participantList.addTicket("ticket2");
        int sizeBefore = participantList.getTicketIds().size();

        participantList.remove("non_existing_ticket");
        int sizeAfter = participantList.getTicketIds().size();

        assertEquals(sizeBefore, sizeAfter);
    }

    @Test
    public void testGetAndSetTicketIds() {
        ArrayList<String> newTicketIds = new ArrayList<>(Arrays.asList("new1", "new2", "new3"));
        participantList.setTicketIds(newTicketIds);

        assertEquals(newTicketIds, participantList.getTicketIds());
        assertEquals(3, participantList.getTicketIds().size());
    }


    @Test
    public void testGetAndSetMaxAttend() {
        assertEquals(MAX_ATTENDEES, participantList.getMaxAttend());
        int newMax = 20;
        participantList.setMaxAttend(newMax);
        assertEquals(newMax, participantList.getMaxAttend());
    }
}
