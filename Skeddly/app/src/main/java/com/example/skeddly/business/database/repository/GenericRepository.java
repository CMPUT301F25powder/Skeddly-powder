package com.example.skeddly.business.database.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.skeddly.business.database.DatabaseObject;
import com.example.skeddly.business.database.SingleListenUpdate;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

/**
 * Represents a generic repository that can be extended to retrieve POJOs from Firestore.
 * This class should NOT be used on its own. Derived classes should be used.
 * @param <T> The class of objects that this repository shall handle.
 */
abstract class GenericRepository<T extends DatabaseObject> {
    protected final Class<T> clazz;

    /**
     * Create a new GenericRepository with the given class as the type of POJO that it shall handle.
     * @param clazz The class corresponding to the POJO we're dealing with.
     */
    protected GenericRepository(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * This method retrieves the reference to the collection of documents it handles
     * @return A CollectionReference to the collection of documents it handles.
     */
    protected abstract CollectionReference getCollectionPath();

    /**
     * This method retrieves a query for getting all the documents handled by this repository.
     * This is commonly overridden in repositories that filter out documents by fields.
     * @return A query that can retrieve multiple documents.
     */
    protected Query getQuery() {
        return getCollectionPath();
    }

    /**
     * Retrieves a document by its ID. A task is returned that retrieves the object as converted to
     * the POJO. Retrieving a document that does not exist will result in the task failing.
     * <p>
     * Common usage includes adding an onCompleteListener. The status of the task should be checked
     * with a call to .isSuccessful() before attempting to retrieve the object.
     *
     * @param id The ID of the document we want to retrieve.
     * @return A task that returns the requested object.
     */
    public Task<T> get(String id) {
        return getCollectionPath().document(id).get().continueWith(new Continuation<DocumentSnapshot, T>() {
            @Override
            public T then(@NonNull Task<DocumentSnapshot> task) throws RuntimeExecutionException {
                DocumentSnapshot documentSnapshot = task.getResult();

                if (!documentSnapshot.exists()) {
                    throw new RuntimeExecutionException(new Throwable("Not found"));
                }

                return task.getResult().toObject(clazz);
            }
        });
    }

    /**
     * Retrieves all documents present in the collection managed by this repository. The documents
     * are converted to their underlying POJOs and then returned as a list. The list shall be empty
     * if the collection does not exist or has nothing in it.
     * @return A task that returns all the objects in a list.
     */
    public Task<List<T>> getAll() {
        return getAllByQuery(getQuery());
    }

    protected Task<List<T>> getAllByQuery(Query query) {
        return query.get().continueWith(new Continuation<QuerySnapshot, List<T>>() {
            @Override
            public List<T> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                return task.getResult().toObjects(clazz);
            }
        });
    }

    /**
     * Given an object, either update or add it to the repository. The ID of the object will be
     * queried and used as the Document ID in the database.
     * @param object The object to store in the repository.
     * @return A task that will complete once the object has been saved.
     */
    public Task<Void> set(T object) {
        return getCollectionPath().document(object.getId()).set(object);
    }

    /**
     * Listens to the document given by the ID. The caller is called back with the initial state
     * of the document as converted to a POJO. Any new changes afterwards will notify
     * the provided callback function.
     * <p>
     * If the document does not exist when initially calling this, the returned object is NULL.
     * The listener remains active and will notify if the document is created later on.
     * <p>
     * If the document is deleted, the returned object is NULL. The listener still remains active
     * for if a document with the same ID is created.
     * <p>
     * Warning: The caller is responsible for removing the ListenerRegistration once they no longer
     * require updates. Only use this method if real time updates are necessary.
     * @param id The ID of the document we want to listen to.
     * @param callback Who we should call back upon document changes.
     * @return A ListenerRegistration object that can be used to cancel the listener at any time.
     */
    public ListenerRegistration listen(String id, SingleListenUpdate<T> callback) {
        return getCollectionPath().document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    Log.v("GenericRepository", String.format("Retrieved update for '%s/%s'", getCollectionPath().getId(), id));
                    callback.onUpdate(value.toObject(clazz));
                }
            }
        });
    }

    /**
     * Listens to all documents in this repository. The caller is called back with the initial state
     * of all documents as converted to POJOs. Any new changes afterwards will notify the provided
     * callback function. Note that each call provides a list of every object, even those that did
     * not change.
     * <p>
     * If the collection does not exist when initially calling this, the list is empty.
     * The listener remains active and will notify if the collection is added to later on.
     * <p>
     * Warning: The caller is responsible for removing the ListenerRegistration once they no longer
     * require updates. Only use this method if real time updates are necessary.
     *
     * @param callback Who we should call back upon document changes.
     * @return A ListenerRegistration object that can be used to cancel the listener at any time.
     */
    public ListenerRegistration listenAll(SingleListenUpdate<List<T>> callback) {
        return getQuery().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    callback.onUpdate(value.toObjects(clazz));
                }
            }
        });
    }

    /**
     * Deletes a document by its ID. A task is returned that can be listened to so that the caller
     * knows when the deletion has finished being processed by the DB.
     * <p>
     * If the ID does not exist in the database, the task is still successful and nothing changes.
     * @param id The ID of the document that we want to delete.
     * @return A listenable Task that represents the deletion on the DB side.
     */
    public Task<Void> delete(String id) {
        return getCollectionPath().document(id).delete();
    }

    /**
     * Counts the number of documents that reside within the collection we are tracking.
     * If the collection does not exist, a count of 0 is returned.
     * @return A task that retrieves the count as a long.
     */
    public Task<Long> count() {
        return getQuery().count().get(AggregateSource.SERVER).continueWith(new Continuation<AggregateQuerySnapshot, Long>() {
            @Override
            public Long then(@NonNull Task<AggregateQuerySnapshot> task) throws Exception {
                return task.getResult().getCount();
            }
        });
    }
}
