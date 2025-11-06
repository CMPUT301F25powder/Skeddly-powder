package com.example.skeddly.business.database;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Objects;
import java.util.Set;


/**
 * Helper class that aids in retrieving specific DatabaseObjects from Firebase.
 * @param <T> The type of DatabaseObject that shall be retrieved.
 */
public class DatabaseFetcher<T extends DatabaseObject> {
    private final DatabaseHandler databaseHandler;
    private final String path;
    private final Class<T> fetchedClass;

    /**
     * Constructor for a DatabaseFetcher
     * @param path The DB path that contains our objects
     * @param fetchedClass The class of object that we are retrieving
     */
    public DatabaseFetcher(String path, Class<T> fetchedClass) {
        this.databaseHandler = new DatabaseHandler();
        this.path = path;
        this.fetchedClass = fetchedClass;
    }

    /**
     * Gets an item by its ID in the database. The caller is called back
     * when we finish retrieving it.
     * @param id The ID of the item to retrieve
     * @param callback The callback once we retrieve the data. The retrieved object is passed
     *                 as a parameter.
     */
    public void getById(String id, SingleListenUpdate<T> callback) {
        databaseHandler.getPath(this.path).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callback.onUpdate(null);
                } else {
                    callback.onUpdate(snapshot.getValue(fetchedClass));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });
    }

    /**
     * Add the given DatabaseObject to the database. If it already exists, update it.
     * @param object The DatabaseObject to set in the DB
     */
    public void set(T object) {
        databaseHandler.getPath(this.path).child(object.getId()).setValue(object);
    }

    /**
     * Retrieve a set containing all the IDs in the database of the particular object.
     * The caller is called back once we finish retrieving the data.
     * @param callback The callback once we retrieve the data. The set of IDs are passed
     *                 as a parameter.
     */
    public void getIds(SingleListenUpdate<Set<String>> callback) {
        databaseHandler.getPath(this.path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callback.onUpdate(null);
                } else {
                    GenericTypeIndicator<Map<String, Object>> gti = new GenericTypeIndicator<>() {};
                    Map<String, Object> vals = snapshot.getValue(gti);
                    callback.onUpdate(Objects.requireNonNull(vals).keySet());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });
    }
}
