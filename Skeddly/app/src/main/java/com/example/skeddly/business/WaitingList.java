package com.example.skeddly.business;

import com.example.skeddly.business.database.DatabaseObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Class for list of people who want to attend an event
 */
public class WaitingList extends DatabaseObject {
    private ArrayList<String> ticketIds = new ArrayList<>();
    private int maxWait;
    private Random randomGen;

    /**
     * Constructor for bounded waiting list
     * @param maxWait Maximum number of applicants
     */
    public WaitingList(int maxWait) {
        if (maxWait <= 0) {
            maxWait = Integer.MAX_VALUE;
        }
        this.maxWait = maxWait;
        randomGen = new Random();
    }

    /**
     * Constructor for unbounded waiting list
     */
    public WaitingList() {
        this(0);
    }

    /**
     * Add a ticket to the waiting list
     * @param ticketId Ticket to add
     * @throws IllegalArgumentException If the list is already full
     */
    public void addTicket(String ticketId) {
        if (ticketIds.size() < maxWait) {
            ticketIds.add(ticketId);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Remove a ticket from the waiting list
     * @param ticketId ID of the ticket to remove
     */
    public void remove(String ticketId) {
        ticketIds.remove(ticketId);
    }

    /**
     * Randomly select and remove a ticket from the waiting list
     * @return The selected ticket
     * @throws IllegalStateException if the list is empty.
     */
    public String draw() {
        if (ticketIds.isEmpty()) {
            throw new IllegalStateException();
        }

        String t = ticketIds.get(randomGen.nextInt(ticketIds.size()));
        this.remove(t);

        return t;
    }

    /**
     * Return the maximum number of people allowed in the waiting list
     * @return The maximum length of the list
     */
    public int getLimit() {
        return maxWait;
    }

    /**
     * Set the maximum number of people allowed in the waiting list
     * @param maxWait The new limit
     */
    public void setLimit(int maxWait) {
        this.maxWait = maxWait;
    }

    /**
     * Gets whether the waiting list is full or not.
     * @return True if the waiting list is full. False otherwise.
     */
    public boolean isFull() {
        return getLimit() <= ticketIds.size();
    }

    /**
     * Gets whether the waiting list is empty or not.
     * @return True if the waiting list is empty. False otherwise.
     */
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
