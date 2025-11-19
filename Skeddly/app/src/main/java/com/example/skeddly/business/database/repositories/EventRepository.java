package com.example.skeddly.business.database.repositories;


import com.example.skeddly.business.event.Event;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventRepository extends GenericRepository<Event> {
    public static final String COLLECTION_PATH = "events";

    public EventRepository() {
        super(FirebaseFirestore.getInstance(), COLLECTION_PATH, Event.class);
    }
}
