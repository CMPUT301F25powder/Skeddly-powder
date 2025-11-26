package com.example.skeddly.business.database.repository;


import androidx.annotation.NonNull;

import com.example.skeddly.business.event.Event;
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
     * Creates a new event repository.
     * @param firestore The FirebaseFirestore instance to use.
     */
    public EventRepository(FirebaseFirestore firestore) {
        super(Event.class);
        this.firestore = firestore;
        this.organizerId = null;
    }

    /**
     * Creates a new event repository, setting getByOrganizer to true
     * @param firestore The FirebaseFirestore instance to use.
     */
    public EventRepository(FirebaseFirestore firestore, String organizerId) {
        super(Event.class);
        this.firestore = firestore;
        this.organizerId = organizerId;
    }

    public Task<Void> updateEvent(Event event) {
        return get(event.getId()).continueWithTask(new Continuation<Event, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<Event> task) throws Exception {
                Event oldEvent = task.getResult();

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

    /**
     * Override getQuery to filter by organizer if specified.
     */
    @Override
    protected Query getQuery() {
        if (organizerId != null) {
            return getCollectionPath().whereEqualTo("organizer", organizerId);
        }
        return super.getQuery();
    }

}
