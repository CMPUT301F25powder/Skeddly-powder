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
    private final String userId;
    public static final String COLLECTION_PATH = "tickets";

    /**
     * Create a new TicketRepository. The repository is associated with a particular event,
     * and user, unless either are null.
     * @param firestore The FirebaseFirestore instance to use.
     * @param eventId The event id that this repository is associated with.
     * @param userId The user id that this repository is associated with.
     */
    public TicketRepository(FirebaseFirestore firestore, String eventId, String userId) {
        super(Ticket.class);
        this.firestore = firestore;
        this.userId = userId;
        this.eventId = eventId;
    }

    /**
     * Create a new TicketRepository. The repository is associated with a particular event,
     * unless the eventId is null. If it is, the repository shall retrieve tickets for all events.
     * @param firestore The FirebaseFirestore instance to use.
     * @param eventId The event id that this repository is associated with.
     */
    public TicketRepository(FirebaseFirestore firestore, String eventId) {
        this(firestore, eventId, null);
    }

    /**
     * Retrieves all the tickets related to this event that contain a certain status.
     * @param status The status to retrieve tickets by.
     * @return A task that when complete, contains all the requested tickets in a list.
     */
    public Task<List<Ticket>> getAllByStatus(TicketStatus status) {
        return getAllByQuery(getQuery().whereEqualTo("status", status));
    }

    /**
     * Retrieves all the tickets related to this event that contain certain statuses.
     * @param statuses The statuses to retrieve tickets by.
     * @return A task that when complete, contains all the requested tickets in a list.
     */
    public Task<List<Ticket>> getAllByStatuses(List<TicketStatus> statuses) {
        return getAllByQuery(getQuery().whereIn("status", statuses));
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
        Query query = super.getQuery();

        // If an eventId is provided, query by eventId.
        if (eventId != null) {
            query = query.whereEqualTo("eventId", eventId);
        }

        // If a userId is provided, query by userId.
        if (userId != null) {
            query = query.whereEqualTo("userId", userId);
        }

        return query.orderBy("ticketTime", Query.Direction.DESCENDING);
    }
}
