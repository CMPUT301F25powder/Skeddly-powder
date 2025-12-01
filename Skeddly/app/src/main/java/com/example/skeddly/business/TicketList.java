package com.example.skeddly.business;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;

/**
 * A list of tickets. This class is extended to make up the participant and final lists.
 */
public abstract class TicketList {
    private ArrayList<String> ticketIds = new ArrayList<>();
    private int max;

    /**
     * Constructor for bounded ticket list
     * @param max Maximum number of tickets
     */
    public TicketList(int max) {
        if (max <= 0) {
            max = Integer.MAX_VALUE;
        }
        this.max = max;
    }

    /**
     * Constructor for unbounded ticket list
     */
    public TicketList() {
        this(0);
    }

    /**
     * Add a ticket to the waiting list
     * @param ticketId Ticket to add
     * @throws IllegalArgumentException If the list is already full
     */
    public void addTicket(String ticketId) {
        if (ticketIds.size() < max) {
            ticketIds.add(ticketId);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Remove a ticket from the ticket list
     * @param ticketId ID of the ticket to remove
     */
    public void remove(String ticketId) {
        ticketIds.remove(ticketId);
    }

    /**
     * Return the maximum number of people allowed in the ticket list
     * @return The maximum length of the list
     */
    public int getMax() {
        return max;
    }

    /**
     * Set the maximum number of people allowed in the ticket list
     * @param max The new limit
     */
    public void setMax(int max) {
        this.max = max;
    }

    /**
     * Gets whether the ticket list is full or not.
     * @return True if the ticket list is full. False otherwise.
     */
    @Exclude
    public boolean isFull() {
        return getMax() <= ticketIds.size();
    }

    /**
     * Gets whether the ticket list is empty or not.
     * @return True if the waiting list is empty. False otherwise.
     */
    @Exclude
    public boolean isEmpty() {
        return ticketIds.isEmpty();
    }

    /**
     * Gets how many tickets are stored in the waiting list.
     * @return The number of tickets in the waiting list.
     */
    public int size() {
        return ticketIds.size();
    }

    /**
     * Return the {@link ArrayList} of tickets
     * @return The ticketId list
     */
    public ArrayList<String> getTicketIds() {
        return ticketIds;
    }

    /**
     * Update the {@link ArrayList} of tickets
     * @param ticketIds The new ticketId list
     */
    public void setTicketIds(ArrayList<String> ticketIds) {
        this.ticketIds = ticketIds;
    }
}
