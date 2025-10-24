package com.example.skeddly.business;

import java.util.ArrayList;
import java.util.Random;

public class WaitingList {
    private final ArrayList<Ticket> ticketList = new ArrayList<Ticket>();
    private int maxWait;

    public WaitingList() {
        maxWait = Integer.MAX_VALUE;
    }
    public WaitingList(int maxWait) {
        this.maxWait = maxWait;
    }
    public void addTicket(Ticket t) {
        ticketList.add(t);
    }
    public void remove(User u) {
        ticketList.removeIf(t -> t.getUser().equals(u));
    }
    public void remove(Ticket t) {
        ticketList.remove(t);
    }
    public Ticket draw() {
        Ticket t = ticketList.get(Random.nextInt(ticketList.size()));
        this.remove(t);

        return t;
    }
    public int getLimit() {
        return maxWait;
    }
    public void setLimit(int maxWait) {
        this.maxWait = maxWait;
    }
}
