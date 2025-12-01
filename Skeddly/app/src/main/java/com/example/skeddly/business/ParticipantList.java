package com.example.skeddly.business;

/**
 * Class for list of participants in an event.
 */
public class ParticipantList extends TicketList {
    /**
     * Constructor for ParticipantList.
     * @param max The maximum number of tickets that can be in the list.
     */
    public ParticipantList(int max) {
        super(max);
    }

    /**
     * Constructor for ParticipantList.
     */
    public ParticipantList() {
        super();
    }
}
