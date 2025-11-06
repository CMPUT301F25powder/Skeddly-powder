package com.example.skeddly.business.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.skeddly.business.user.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Handles edits and realtime updates to the realtime DB in Firebase
 */
public class DatabaseHandler {
    private DatabaseReference database;

    public DatabaseHandler() {
        this.database = FirebaseDatabase.getInstance().getReference();
    }

    private String serializeGetterName(String name) {
        String replaced = name.replaceFirst("get", "");
        String firstLetter = replaced.substring(0, 1).toLowerCase();

        return firstLetter.concat(replaced.substring(1));
    }
    public void customSerializer(DatabaseReference ref, Object object) {
        Method [] methods =  object.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (method.getReturnType() == DatabaseObjects.class) {
                String formattedMethodName = this.serializeGetterName(method.getName());
                try {
                    DatabaseObjects values = (DatabaseObjects) method.invoke(object);

                    for (int i = 0; i < values.size(); i++) {
                        DatabaseObject value = values.get(i);
                        String valueId = value.getId();

                        ref.child(formattedMethodName).child(String.valueOf(i)).setValue(valueId);
                    }

                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
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
                Log.e("DATABASE:SINGLE_LISTEN", error.toString());
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

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DatabaseObjects result = new DatabaseObjects();
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
                Log.e("DATABASE:ITERABLE_LISTEN", error.toString());
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

    /**
     * Returns a {@link DatabaseReference} pointing to tickets
     * @return A {@link DatabaseReference} pointing to tickets
     * @see DatabaseReference
     */
    public DatabaseReference getTicketsPath() {
        return database.child("tickets");
    }

    public DatabaseReference getNotificationsPath() {
        return database.child("notifications");
    }

    /**
     * Returns a {@link DatabaseReference} pointing to the specified path
     * @return A {@link DatabaseReference} pointing to the specified path
     * @see DatabaseReference
     */
    public DatabaseReference getPath(String path) {
        return database.child(path);
    }
}
