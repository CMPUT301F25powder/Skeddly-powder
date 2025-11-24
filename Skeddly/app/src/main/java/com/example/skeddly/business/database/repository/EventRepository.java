package com.example.skeddly.business.database.repository;


import androidx.annotation.NonNull;

import com.example.skeddly.business.event.Event;
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
    public Task<List<Event>> getAllByOwner(String organizerId) {
        return getQuery().whereEqualTo("organizer", organizerId).get().continueWith(new Continuation<QuerySnapshot, List<Event>>() {
            @Override
            public List<Event> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                return task.getResult().toObjects(clazz);
            }
        });
    }

    /**
     * Gets all events
     * @return A task that returns all events in the db.
     */
    public Task<List<Event>> getAll() {
        return getQuery().get().continueWith(task -> task.getResult().toObjects(clazz));
    }

    @Override
    protected CollectionReference getCollectionPath() {
        return firestore.collection(COLLECTION_PATH);
    }
}
