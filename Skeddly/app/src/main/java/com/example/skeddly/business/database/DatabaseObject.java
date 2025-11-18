package com.example.skeddly.business.database;

import com.google.firebase.firestore.DocumentId;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * An object that is stored as a document in the DB.
 * Any values in its fields will be turned into DB keys by the {@link DatabaseHandler}
 * @see DatabaseHandler
 */
public class DatabaseObject {
    @DocumentId
    private String id;

    /**
     * Constructor for the DatabaseObject
     */
    public DatabaseObject() {
        this.id = String.valueOf(UUID.randomUUID());
    }

    /**
     * Gets the ID of the object
     * @return A string of the id of the object
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the object
     * @param id The new ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the related methods for a database object.
     * @return An array of all the methods
     */
    public Method[] fetchDatabaseObjectRelatedMethods() {
        Stream<Method> stream = Arrays.stream(this.getClass().getDeclaredMethods()).filter(method -> method.getReturnType() == DatabaseObjects.class);

        return stream.toArray(Method[]::new);
    }
}
