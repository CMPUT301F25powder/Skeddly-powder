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

    public Task<List<Ticket>> getAllByStatus(TicketStatus status) {
        return getAllByQuery(getQuery().whereEqualTo("status", status.toString()));
    }

    public Task<List<Ticket>> getAllByStatuses(List<TicketStatus> statuses) {
        Query query = getQuery();

        for (TicketStatus status : statuses) {
            query.whereEqualTo("status", status.toString());
        }

        return getAllByQuery(query);
    }

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
