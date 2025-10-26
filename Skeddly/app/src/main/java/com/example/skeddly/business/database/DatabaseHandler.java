package com.example.skeddly.business.database;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.skeddly.business.user.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Handles edits and realtime updates to the realtime DB in Firebase
 */
public class DatabaseHandler {
    private User user;
    private Context context;
    private DatabaseReference database;

    public DatabaseHandler(Context context) {
        this.context = context;
        this.database = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Listens to if a single value is updated in a specific part of the DB
     * @param ref The {@link DatabaseReference} for the path to watch
     * @param classType The {@link DatabaseObject} to serialize to (generic)
     * @param callback The SingleListenUpdate to use as a callback for when data is changed
     * @param <T> The {@link DatabaseObject} to serialize to (generic)
     * @see DatabaseReference
     * @see DatabaseObject
     */
    public <T extends DatabaseObject> void singleListen(DatabaseReference ref, Class<T> classType, SingleListenUpdate callback) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                T value = snapshot.getValue(classType);

                if (value != null) {
                    callback.onUpdate(value);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error);
            }
        });
    }

    /**
     * Listens to if a group of values are updated in a specific part of the DB
     * @param ref The {@link DatabaseReference} for the path to watch
     * @param classType The {@link DatabaseObject} to serialize to (generic)
     * @param callback The SingleListenUpdate to use as a callback for when data is changed
     * @param <T> The {@link DatabaseObject} to serialize to (generic)
     * @see DatabaseReference
     * @see DatabaseObject
     */
    public <T extends DatabaseObject> void iterableListen(DatabaseReference ref, Class<T> classType, IterableListenUpdate callback) {
        ref.addValueEventListener(new ValueEventListener() {
            ArrayList<T> result = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> subSnapshot = snapshot.getChildren();

                for (DataSnapshot item : subSnapshot) {
                    T value = item.getValue(classType);

                    if (value != null) {
                        value.setId(item.getKey());

                        result.add(value);
                    }
                }

                callback.onUpdate(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error);
            }
        });
    }

    /**
     * Returns a {@link DatabaseReference} pointing to users
     * @return A {@link DatabaseReference} pointing to users
     * @see DatabaseReference
     */
    public DatabaseReference getUsersPath() {
        return database.child("users");
    }

    /**
     * Returns a {@link DatabaseReference} pointing to events
     * @return A {@link DatabaseReference} pointing to events
     * @see DatabaseReference
     */
    public DatabaseReference getEventsPath() {
        return database.child("events");
    }
}
