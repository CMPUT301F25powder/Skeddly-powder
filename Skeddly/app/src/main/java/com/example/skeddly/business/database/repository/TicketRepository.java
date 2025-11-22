package com.example.skeddly.business.database.repository;


import com.example.skeddly.business.Ticket;
import com.example.skeddly.business.TicketStatus;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

/**
 * A class to handle retrieving tickets from Firestore. This class is associated with a particular
 * event, which is relevant when performing aggregate retrievals.
 */
public class TicketRepository extends GenericRepository<Ticket> {
    private final FirebaseFirestore firestore;
    private final String eventId;
    public static final String COLLECTION_PATH = "tickets";

    public TicketRepository(FirebaseFirestore firestore, String eventId) {
        super(Ticket.class);
        this.firestore = firestore;
        this.eventId = eventId;
    }

    /**
     * Retrieves all the tickets related to this event that contain a certain status.
     * @param status The status to retrieve tickets by.
     * @return A task that when complete, contains all the requested tickets in a list.
     */
    public Task<List<Ticket>> getAllByStatus(TicketStatus status) {
        return getAllByQuery(getQuery().whereEqualTo("status", status.toString()));
    }

    /**
     * Retrieves all the tickets related to this event that contain certain statuses.
     * @param statuses The statuses to retrieve tickets by.
     * @return A task that when complete, contains all the requested tickets in a list.
     */
    public Task<List<Ticket>> getAllByStatuses(List<TicketStatus> statuses) {
        Query query = getQuery();

        for (TicketStatus status : statuses) {
            query.whereEqualTo("status", status.toString());
        }

        return getAllByQuery(query);
    }

    /**
     * Updates the status of a given ticket id.
     * @param ticketId The ticketId to update
     * @param newStatus The new status to update it to
     * @return A task that is completed when the update has finished on the DB side
     */
    public Task<Void> updateStatus(String ticketId, TicketStatus newStatus) {
        return getCollectionPath().document(ticketId).update("status", newStatus);
    }

    @Override
    protected CollectionReference getCollectionPath() {
        return firestore.collection(COLLECTION_PATH);
    }

    @Override
    protected Query getQuery() {
        return getCollectionPath().whereEqualTo("eventId", eventId);
    }
}
