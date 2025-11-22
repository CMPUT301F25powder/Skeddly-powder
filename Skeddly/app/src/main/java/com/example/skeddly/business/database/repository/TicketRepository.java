package com.example.skeddly.business.database.repository;


import com.example.skeddly.business.Ticket;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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

    @Override
    protected CollectionReference getCollectionPath() {
        return firestore.collection(COLLECTION_PATH);
    }

    @Override
    protected Query getQuery() {
        return getCollectionPath().whereEqualTo("eventId", eventId);
    }
}
