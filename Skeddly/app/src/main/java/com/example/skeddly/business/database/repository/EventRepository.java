package com.example.skeddly.business.database.repository;


import androidx.annotation.NonNull;

import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.search.EventFilter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.List;

/**
 * A class to handle retrieving events from Firestore.
 */
public class EventRepository extends GenericRepository<Event> {
    private final FirebaseFirestore firestore;
    public static final String COLLECTION_PATH = "events";
    public String organizerId;

    /**
     * Creates a new event repository associated with a given organizer id.
     * @param firestore The FirebaseFirestore instance to use.
     */
    public EventRepository(FirebaseFirestore firestore, String organizerId) {
        super(Event.class);
        this.firestore = firestore;
        this.organizerId = organizerId;
    }

    /**
     * Creates a new event repository that is not associated with any particular organizer.
     * @param firestore The FirebaseFirestore instance to use.
     */
    public EventRepository(FirebaseFirestore firestore) {
        this(firestore, null);
    }

    public Task<Void> updateEvent(Event event) {
        return get(event.getId()).continueWithTask(new Continuation<Event, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<Event> task) throws Exception {
                Event oldEvent = task.getResult();
                oldEvent.getWaitingList().setMax(event.getWaitingList().getMax());
                oldEvent.getParticipantList().setMax(event.getParticipantList().getMax());

                event.setWaitingList(oldEvent.getWaitingList());
                event.setParticipantList(oldEvent.getParticipantList());
                event.setOrganizer(oldEvent.getOrganizer());

                return set(event);
            }
        });
    }

    @Override
    protected CollectionReference getCollectionPath() {
        return firestore.collection(COLLECTION_PATH);
    }

    @Override
    protected Query getQuery() {
        Query query = super.getQuery();

        if (organizerId != null) {
            query = query.whereEqualTo("organizer", organizerId);
        }

        return query;
    }

}
