package com.example.skeddly.business.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles edits and realtime updates to the realtime DB in Firebase
 */
public class DatabaseHandler {
    final String INTERNAL = "_internal";
    private DatabaseReference database;

    public DatabaseHandler() {
        this.database = FirebaseDatabase.getInstance().getReference();
    }

    private String serializeGetterName(String name) {
        String replaced = name.replaceFirst("get", "");
        String firstLetter = replaced.substring(0, 1).toLowerCase();
        String fullIdentifier = firstLetter.concat(replaced.substring(1));

        return fullIdentifier.concat(INTERNAL);
    }

    private String unserializeGetterName(String name) {
        String firstLetter = name.substring(0, 1).toUpperCase();
        String replaced = firstLetter.concat(name.substring(1));

        return String.format("set%s", replaced).replace(INTERNAL, "");
    }

    private String getBaseName(String name) {
        return name.replace(INTERNAL, "");
    }

    public void customSerializer(DatabaseReference ref, DatabaseObject object) {
        Method [] methods = object.fetchDatabaseObjectRelatedMethods();

        for (Method method : methods) {
            // Formatted strings for saving to DB
            // For example, in the user, it will be saved as field_internal so it knows where to load from next time
            // Only ids are saved to the internalName field
            String internalName = this.serializeGetterName(method.getName());

            // The higher level place in the DB where the object itself is stored
            String baseName = this.getBaseName(internalName);

            try {
                DatabaseObjects values = (DatabaseObjects) method.invoke(object);

                // Handle all values in the DatabaseObjects to serialize
                for (int i = 0; i < values.size(); i++) {
                    DatabaseObject value = (DatabaseObject) values.get(i);
                    String valueId = value.getId();

                    ref.child(internalName).child(String.valueOf(i)).setValue(valueId);

                    // Save to the higher level line in the DB
                    this.database.child(baseName).child(valueId).setValue(value);
                }

            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void customUnserializer(DatabaseReference ref, DatabaseObject object) throws InvocationTargetException, IllegalAccessException {
        Method [] methods = object.fetchDatabaseObjectRelatedMethods();

        for (Method method : methods) {
            // Formatted strings for reading from DB
            // For example, in the user, it will be saved as field_internal so it knows where to load from next time
            // Only ids are saved to the internalName field
            String internalName = this.serializeGetterName(method.getName());
            String setterName = this.unserializeGetterName(internalName);
            String baseName = this.getBaseName(internalName);

            // Invoke getter to determine its type
            DatabaseObjects<?> value = (DatabaseObjects<?>) method.invoke(object);

            if (value != null) {
                // Get type
                Class<? extends DatabaseObject> parameter = value.getParameter();

                // Prepare values to
                Class<?>[] setterParams = new Class<?>[] {
                        DatabaseObjects.class
                };

                // All of the ids that are in the _internal field
                Task<ArrayList<String>> ownerIdsTask = getNodeChildren(ref.child(internalName));

                // All of the objects related to this DatabaseObject
                // For example, this would be all Notification objects (that the user is allowed to read)
                Task<DatabaseObjects<DatabaseObject>> allObjectsTask = getNodeChildren(this.database.child(baseName), (Class<DatabaseObject>) parameter);

                Task<List<Object>> allTasks = Tasks.whenAllSuccess(ownerIdsTask, allObjectsTask);

                allTasks.addOnSuccessListener(results -> {
                    ArrayList<String> ownerIds = (ArrayList<String>) results.get(0);
                    DatabaseObjects<DatabaseObject> allObjects = (DatabaseObjects<DatabaseObject>) results.get(1);

                    // Empty to fill with values to save to the DB
                    DatabaseObjects<DatabaseObject> unserializedObjects = new DatabaseObjects(parameter);

                    for (DatabaseObject someObject : allObjects) {
                        if (ownerIds.contains(someObject.getId())) {
                            unserializedObjects.add(someObject);
                        }
                    }

                    try {
                        // Get the setter
                        Method setter = object.getClass().getMethod(setterName, setterParams);

                        setter.invoke(object, unserializedObjects);
                    } catch (IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }

                    Log.d("DbHandler", String.format("Invoked %s with %d objects", setterName, unserializedObjects.size()));
                });
            }
        }
    }

    public Task<ArrayList<String>> getNodeChildren(DatabaseReference ref) {
        return ref.get().continueWith(task -> {
            ArrayList<String> result = new ArrayList<>();

            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        result.add(childSnapshot.getValue(String.class));
                    }
                }
            }

            return result;
        });
    }

    public <T extends DatabaseObject> Task<DatabaseObjects<T>> getNodeChildren(DatabaseReference ref, Class<T> classType) {
        return ref.get().continueWith(task -> {
            DatabaseObjects<T> result = new DatabaseObjects<>(classType);
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        T loadedResult = childSnapshot.getValue(classType);
                        loadedResult.setId(childSnapshot.getKey());

                        result.add(loadedResult);
                    }
                }
            }

            return result;
        });
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
