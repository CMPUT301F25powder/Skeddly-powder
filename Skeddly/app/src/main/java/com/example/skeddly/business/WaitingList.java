package com.example.skeddly.business;

import com.example.skeddly.business.database.DatabaseObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Class for list of people who want to attend an event
 */
public class WaitingList extends TicketList {
    private final Random randomGen = new Random();

    /**
     * Constructor for WaitingList.
     * @param max The maximum number of tickets that can be in the list.
     */
    public WaitingList(int max) {
        super(max);
    }

    /**
     * Constructor for WaitingList.
     */
    public WaitingList() {
        super();
    }

    /**
     * Randomly select and remove a ticket from the waiting list
     * @return The selected ticket
     * @throws IllegalStateException if the list is empty.
     */
    public String draw() {
        if (isEmpty()) {
            throw new IllegalStateException();
        }

        String t = getTicketIds().get(randomGen.nextInt(size()));
        remove(t);

        return t;
    }
}
