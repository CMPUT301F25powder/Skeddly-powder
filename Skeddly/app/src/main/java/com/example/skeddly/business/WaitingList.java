package com.example.skeddly.business;

import com.example.skeddly.business.database.DatabaseObject;
import com.example.skeddly.business.database.DatabaseObjects;

import java.util.ArrayList;
import java.util.Random;

/**
 * Class for list of people who want to attend an event
 */
public class WaitingList extends DatabaseObject {
    private ArrayList<DatabaseObjects> ticketList = new ArrayList<DatabaseObjects>();
    private int maxWait;
    private Random randomGen;

    /**
     * Constructor for unbounded waiting list
     */
    public WaitingList() {
        maxWait = Integer.MAX_VALUE;
        randomGen = new Random();
    }

    /**
     * Constructor for bounded waiting list
     * @param maxWait Maximum number of applicants
     */
    public WaitingList(int maxWait) {
        this.maxWait = maxWait;
        randomGen = new Random();
    }

    /**
     * Add a ticket to the waiting list
     * @param t Ticket to add
     * @throws IllegalArgumentException If the list is already full
     */
    public void addTicket(DatabaseObjects t) {
        if (ticketList.size() < maxWait) {
            ticketList.add(t);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Remove a user from the waiting list
     * @param u User to remove
     */
//    public void remove(User u) {
//        ticketList.removeIf(t -> t.getUser().equals(u));
//    }

    /**
     * Remove a ticket from the waiting list
     * @param t Ticket to remove
     */
    public void remove(DatabaseObjects t) {
        ticketList.remove(t);
    }

    /**
     * Randomly select and remove a ticket from the waiting list
     * @return The selected ticket
     */
    public DatabaseObjects draw() {
        DatabaseObjects t = ticketList.get(randomGen.nextInt(ticketList.size()));
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

    public ArrayList<DatabaseObjects> getTicketList() {
        return ticketList;
    }

    public void setTicketList(ArrayList<DatabaseObjects> ticketList) {
        this.ticketList = ticketList;
    }
}
