package com.example.skeddly.business.database;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Handles edits and realtime updates to the realtime DB in Firebase
 */
public class DatabaseHandler {
    final String INTERNAL = "_internal";
    private FirebaseFirestore database;

    public DatabaseHandler() {
        this.database = FirebaseFirestore.getInstance();
    }

    /**
     * Serializes the getter for a field in DB
     * @param name The name of the getter to serialize
     * @return The serialized getter string
     * @see DatabaseObject
     */
    private String serializeGetterName(String name) {
        String replaced = name.replaceFirst("get", "");
        String firstLetter = replaced.substring(0, 1).toLowerCase();
        String fullIdentifier = firstLetter.concat(replaced.substring(1));

        return fullIdentifier.concat(INTERNAL);
    }

    /**
     * Unserialize a getter for a field in DB
     * @param name The name of the getter to unserialize
     * @return The unserialized getter string
     */
    private String unserializeGetterName(String name) {
        String firstLetter = name.substring(0, 1).toUpperCase();
        String replaced = firstLetter.concat(name.substring(1));

        return String.format("set%s", replaced).replace(INTERNAL, "");
    }

    /**
     * Gets the base name of a field in the DB. Just strips off the INTERNAL string.
     * @param name The field name.
     * @return The base name.
     */
    private String getBaseName(String name) {
        return name.replace(INTERNAL, "");
    }

    /**
     * Serializes a {@link DatabaseObject} into the DB
     * @param ref The {@link DocumentReference} to serialize to
     * @param object The {@link DatabaseObject} to serialize
     * @see DocumentReference
     * @see DatabaseObject
     */
    public void customSerializer(DocumentReference ref, DatabaseObject object) {
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

                ref.update(internalName, null);

                // Handle all values in the DatabaseObjects to serialize
                for (int i = 0; i < values.size(); i++) {
                    DatabaseObject value = (DatabaseObject) values.get(i);
                    String valueId = value.getId();

                    ref.update(FieldPath.of(internalName, String.valueOf(i)), valueId);

                    // Save to the higher level line in the DB
                    this.database.collection(baseName).document(valueId).set(value);
                }

            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Unserializes a {@link DatabaseObject} from the DB
     * @param ref The {@link DocumentReference} to unserialize
     * @param object The {@link DatabaseObject} to unserialize
     * @see DocumentReference
     * @see DatabaseObject
     */
    public void customUnserializer(DocumentReference ref, DatabaseObject object) throws InvocationTargetException, IllegalAccessException {
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
                Task<ArrayList<String>> ownerIdsTask = getNodeChildren(ref, internalName);

                // All of the objects related to this DatabaseObject
                // For example, this would be all Notification objects (that the user is allowed to read)
                Task<DatabaseObjects<DatabaseObject>> allObjectsTask = getNodeChildren(this.database.collection(baseName), (Class<DatabaseObject>) parameter);

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

    /**
     * Gets the children of a node in the database.
     * @param ref The {@link DocumentReference} reference of the node
     * @param internalName The field in the document where all the IDs are stored.
     * @return An asynchronous task that gets an arraylist of strings
     * @see CollectionReference
     */
    public Task<ArrayList<String>> getNodeChildren(DocumentReference ref, String internalName) {
        return ref.get().continueWith(task -> {
            ArrayList<String> result = new ArrayList<>();

            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();

                if (documentSnapshot.exists()) {
                    Map<String, String> ids = (Map<String, String>) documentSnapshot.get(internalName);
                    result.addAll(Objects.requireNonNull(ids).values());
                }
            }

            return result;
        });
    }

    /**
     * Gets the children of a node in the database.
     * @param ref The {@link CollectionReference} of the node that contains the children.
     * @return An asynchronous task that gets an arraylist of database objects
     */
    public <T extends DatabaseObject> Task<DatabaseObjects<T>> getNodeChildren(CollectionReference ref, Class<T> classType) {
        return ref.get().continueWith(task -> {
            DatabaseObjects<T> result = new DatabaseObjects<>(classType);
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (!querySnapshot.isEmpty()) {
                    for (QueryDocumentSnapshot childSnapshot : querySnapshot) {
                        T loadedResult = childSnapshot.toObject(classType);

                        result.add(loadedResult);
                    }
                }
            }

            return result;
        });
    }

    /**
     * Listens to if a single value is updated in a specific part of the DB
     * @param ref The {@link DocumentReference} for the path to watch
     * @param classType The {@link DatabaseObject} to serialize to (generic)
     * @param callback The SingleListenUpdate to use as a callback for when data is changed
     * @param <T> The {@link DatabaseObject} to serialize to (generic)
     * @see DocumentReference
     * @see DatabaseObject
     */
    public <T extends DatabaseObject> ListenerRegistration singleListen(DocumentReference ref, Class<T> classType, SingleListenUpdate<T> callback) {
        return ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("DATABASE:SINGLE_LISTEN", error.toString());
                } else if (value != null && value.exists()) {
                    T object = value.toObject(classType);

                    if (object != null) {
                        callback.onUpdate(object);
                    }
                }
            }
        });
    }

    /**
     * Listens to if a group of values are updated in a specific part of the DB
     * @param ref The {@link DocumentReference} for the path to watch
     * @param classType The {@link DatabaseObject} to serialize to (generic)
     * @param callback The SingleListenUpdate to use as a callback for when data is changed
     * @param <T> The {@link DatabaseObject} to serialize to (generic)
     * @see DocumentReference
     * @see DatabaseObject
     */
    public <T extends DatabaseObject> ListenerRegistration iterableListen(CollectionReference ref, Class<T> classType, IterableListenUpdate callback) {
        return ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("DATABASE:ITERABLE_LISTEN", error.toString());
                } else if (value != null) {
                    DatabaseObjects<T> result = new DatabaseObjects<>((Class<T>) DatabaseObject.class);

                    for (QueryDocumentSnapshot item : value) {
                        T object = item.toObject(classType);

                        result.add(object);
                    }

                    callback.onUpdate(result);
                }
            }
        });
    }

    /**
     * Returns a {@link CollectionReference} pointing to users
     * @return A {@link CollectionReference} pointing to users
     * @see CollectionReference
     */
    public CollectionReference getUsersPath() {
        return database.collection("users");
    }

    /**
     * Returns a {@link CollectionReference} pointing to events
     * @return A {@link CollectionReference} pointing to events
     * @see CollectionReference
     */
    public CollectionReference getEventsPath() {
        return database.collection("events");
    }

    /**
     * Returns a {@link CollectionReference} pointing to tickets
     * @return A {@link CollectionReference} pointing to tickets
     * @see CollectionReference
     */
    public CollectionReference getTicketsPath() {
        return database.collection("tickets");
    }

    /**
     * Returns a {@link CollectionReference} pointing to notifications
     * @return A {@link CollectionReference} pointing to notifications
     * @see CollectionReference
     */
    public CollectionReference getNotificationsPath() {
        return database.collection("notifications");
    }

    /**
     * Returns a {@link CollectionReference} pointing to the specified path
     * @return A {@link CollectionReference} pointing to the specified path
     * @see CollectionReference
     */
    public CollectionReference getPath(String path) {
        return database.collection(path);
    }
}
