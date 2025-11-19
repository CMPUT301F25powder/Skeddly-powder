package com.example.skeddly.business.dbnew;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DatabaseHandlerNew {
    public FirebaseFirestore db;

    public DatabaseHandlerNew() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<Void> addUser(TestUser testUser, String id) {
        return db.collection("users").document(id).set(testUser);
    }

    public Task<DocumentSnapshot> getUser(String id) {
        return db.collection("users").document(id).get();
    }
    
    public Task<DocumentReference> addEvent(TestEvent testEvent) {
        return db.collection("events").add(testEvent);
    }

    public Task<QuerySnapshot> getEventByOrganizer(String id) {
        return db.collection("events").whereEqualTo("organizer", id).get();
    }

    public Task<QuerySnapshot> getEvents() {
        return db.collection("events").get();
    }

    public getJoinedEvents(String uid) {
        return db.collectionGroup("tickets").whereEqualTo("uid", uid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {

                }
            }
        })
    }

    public void getUsersFromEventTickets(String eventId) {
        Task<QuerySnapshot> qs = db.collection("events").document(eventId).collection("tickets").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

            }
        });
    }
}
