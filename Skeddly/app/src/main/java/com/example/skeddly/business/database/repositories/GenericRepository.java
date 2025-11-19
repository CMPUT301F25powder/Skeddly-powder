package com.example.skeddly.business.database.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.skeddly.business.database.DatabaseObject;
import com.example.skeddly.business.database.SingleListenUpdate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

public abstract class GenericRepository<T extends DatabaseObject> {
    private final FirebaseFirestore firestore;
    protected final String collectionPath;
    protected final Class<T> clazz;

    protected GenericRepository(FirebaseFirestore firestore, String collectionPath, Class<T> clazz) {
        this.firestore = firestore;
        this.collectionPath = collectionPath;
        this.clazz = clazz;
    }

    /**
     * Retrieves a document by its ID. The caller is called back with the document as converted to
     * the POJO that it represents. The returned object shall be NULL if the document does not exist.
     *
     * @param id The ID of the document we want to retrieve.
     * @param callback The callback for when we finish retrieving the document from the database.
     */
    public void getById(String id, SingleListenUpdate<T> callback) {
        firestore.collection(collectionPath).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.v("GenericRepository", String.format("Retrieved '%s/%s' successfully.", collectionPath, id));

                    DocumentSnapshot documentSnapshot = task.getResult();
                    callback.onUpdate(documentSnapshot.toObject(clazz));
                } else {
                    Log.e("GenericRepository", String.format("Retrieving '%s/%s' failed! %s", collectionPath, id, task.getException()));
                    callback.onUpdate(null);
                }
            }
        });
    }

    public void getAll(RepositoryIterableUpdate<T> callback) {
        firestore.collection(collectionPath).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.v("GenericRepository", String.format("Retrieved all documents of '%s' successfully.", collectionPath));
                    callback.onUpdate(task.getResult().toObjects(clazz));
                } else {
                    Log.e("GenericRepository", String.format("Retrieving all documents of '%s' failed! %s", collectionPath, task.getException()));
                    callback.onUpdate(null);
                }
            }
        });
    }

    public Task<Void> set(T object) {
        return firestore.collection(collectionPath).document(object.getId()).set(object);
    }

    /**
     * Listens to the document given by the ID. The caller is called back with the initial state
     * of the document as converted to a POJO. Any new changes afterwards will notify
     * the provided callback function.
     * <p>
     * If the document does not exist when initially calling this, the returned object is NULL.
     * The listener remains active and will notify if the document is created later on.
     * <p>
     * If the document is deleted, the returned object is NULL. The listener still remains active.
     * @param id The ID of the document we want to listen to.
     * @param callback Who we should call back upon document changes.
     * @return A ListenerRegistration object that can be used to cancel the listener at any time.
     */
    public ListenerRegistration listenById(String id, SingleListenUpdate<T> callback) {
        return firestore.collection(collectionPath).document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    Log.v("GenericRepository", String.format("Retrieved update for '%s/%s'", collectionPath, id));
                    callback.onUpdate(value.toObject(clazz));
                }
            }
        });
    }

    /**
     * Deletes a document by its ID. A task is returned that can be listened to so that the caller
     * knows when the deletion has finished being processed by the DB.
     * @param id The ID of the document that we want to delete.
     * @return A listenable Task that represents the deletion on the DB side.
     */
    public Task<Void> deleteById(String id) {
        return firestore.collection(collectionPath).document(id).delete();
    }

    public void listenAll() {
        firestore.collection(collectionPath).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

            }
        });
    }

    /**
     * Counts the number of documents that reside within the collection we are tracking.
     * If the collection does not exist, a count of 0 is returned.
     * The count shall be NULL if an error accessing the database occurs.
     * @param callback The callback function when we have obtained the count.
     */
    public void getCount(SingleListenUpdate<Long> callback) {
        firestore.collection(collectionPath).count().get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    Log.v("GenericRepository", String.format("Counted '%s' successfully. Got %d", collectionPath, snapshot.getCount()));
                    callback.onUpdate(snapshot.getCount());
                } else {
                    Log.v("GenericRepository", String.format("Counting '%s' failed! %s", collectionPath, task.getException()));
                    callback.onUpdate(null);
                }
            }
        });
    }
}
