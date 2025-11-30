package com.example.skeddly.business.database.repository;


import androidx.annotation.NonNull;

import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.search.EventFilter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.List;

/**
 * A class to handle retrieving events from Firestore.
 */
public class EventRepository extends GenericRepository<Event> {
    private final FirebaseFirestore firestore;
    public static final String COLLECTION_PATH = "events";

    /**
     * Creates a new event repository.
     * @param firestore The FirebaseFirestore instance to use.
     */
    public EventRepository(FirebaseFirestore firestore) {
        super(Event.class);
        this.firestore = firestore;
    }

    /**
     * Gets all events that are owned by the specified organizer.
     * @param organizerId The ID of the organizer
     * @return A task that returns all the events associated with the organizer in a list.
     */
    public Task<List<Event>> getAllByOrganizer(String organizerId) {
        return getQuery().whereEqualTo("organizer", organizerId).get().continueWith(new Continuation<QuerySnapshot, List<Event>>() {
            @Override
            public List<Event> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                return task.getResult().toObjects(clazz);
            }
        });
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
}
