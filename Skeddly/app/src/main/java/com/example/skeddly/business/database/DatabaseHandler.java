package com.example.skeddly.business.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Handles edits and realtime updates to the realtime DB in Firebase
 */
public class DatabaseHandler {
    private DatabaseReference database;

    public DatabaseHandler() {
        this.database = FirebaseDatabase.getInstance().getReference();
    }

    private String serializeGetterName(String name) {
        String replaced = name.replaceFirst("customGet", "");
        String firstLetter = replaced.substring(0, 1).toLowerCase();

        return firstLetter.concat(replaced.substring(1));
    }

    private String unserializeGetterName(String name) {
        String firstLetter = name.substring(0, 1).toUpperCase();
        String replaced = firstLetter.concat(name.substring(1));

        return "customSet".concat(replaced);
    }
    public void customSerializer(DatabaseReference ref, Object object) {
        Method [] methods =  object.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (method.getReturnType() == DatabaseObjects.class) {
                String rawName = this.serializeGetterName(method.getName());
                try {
                    DatabaseObjects values = (DatabaseObjects) method.invoke(object);

                    for (int i = 0; i < values.size(); i++) {
                        DatabaseObject value = (DatabaseObject) values.get(i);
                        String valueId = value.getId();

                        ref.child(rawName).child(String.valueOf(i)).setValue(valueId);
                        ref.getRoot().child(rawName).setValue(value);
                    }

                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void customUnserializer(DatabaseReference ref, Object object) {
        Method [] methods =  object.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (method.getReturnType() == DatabaseObjects.class) {
                String rawName = this.serializeGetterName(method.getName());
                String setterName = this.unserializeGetterName(rawName);

                try {
                    DatabaseObjects<?> value = (DatabaseObjects<?>) method.invoke(object);
                    if (value != null) {
                        Class<? extends DatabaseObject> parameter = value.getParameter();
                        Class<?>[] getterParams = new Class<?>[] {
                                DatabaseObjects.class
                        };
                        Method setter = object.getClass().getMethod(setterName, getterParams);

                        ArrayList<String> ownerIds = getNodeChildren(ref.child(rawName));

                        DatabaseObjects<?> allObjects = getNodeChildren(ref.getRoot().child(rawName), parameter);

                        DatabaseObjects<DatabaseObject> unserializedObjects = new DatabaseObjects(parameter);

                        for (DatabaseObject someObject : allObjects) {
                            if (ownerIds.contains(someObject.getId())) {
                                unserializedObjects.add(someObject);
                            }
                        }

                        Log.w("dbobjects", unserializedObjects.toString());

                        setter.invoke(object, unserializedObjects);
                    }


                } catch (IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public ArrayList<String> getNodeChildren(DatabaseReference ref) {
        ArrayList<String> result = new ArrayList<>();

        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        result.add(childSnapshot.getValue(String.class));
                    }
                }
            }
        });

        return result;
    }

    public <T extends DatabaseObject> DatabaseObjects<T> getNodeChildren(DatabaseReference ref, Class<T> classType) {
        DatabaseObjects<T> result = new DatabaseObjects<>(classType);

        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        result.add(childSnapshot.getValue(classType));
                    }
                }
            }
        });

        return result;
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
                DatabaseObjects<T> result = new DatabaseObjects<>((Class<T>) DatabaseObject.class);
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
