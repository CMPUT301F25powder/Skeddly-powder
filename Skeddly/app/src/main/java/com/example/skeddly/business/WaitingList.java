package com.example.skeddly.business;

import com.example.skeddly.business.user.User;

import java.util.ArrayList;
import java.util.Random;

/**
 * Class for list of people who want to attend an event
 */
public class WaitingList {
    private final ArrayList<Ticket> ticketList = new ArrayList<Ticket>();
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
     */
    public void addTicket(Ticket t) {
        if (ticketList.size() < maxWait) {
            ticketList.add(t);
        }
    }

    /**
     * Remove a user from the waiting list
     * @param u User to remove
     */
    public void remove(User u) {
        ticketList.removeIf(t -> t.getUser().equals(u));
    }

    /**
     * Remove a ticket from the waiting list
     * @param t Ticket to remove
     */
    public void remove(Ticket t) {
        ticketList.remove(t);
    }

    /**
     * Randomly select and remove a ticket from the waiting list
     * @return The selected ticket
     */
    public Ticket draw() {
        Ticket t = ticketList.get(randomGen.nextInt(ticketList.size()));
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
}
