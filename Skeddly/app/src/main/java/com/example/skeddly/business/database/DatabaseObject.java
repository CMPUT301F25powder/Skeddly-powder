package com.example.skeddly.business.database;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.UUID;

/**
 * An object that can be serialized into the DB
 * Any values in its fields will be turned into DB keys by the {@link DatabaseHandler}
 * @see DatabaseHandler
 */
public class DatabaseObject implements Serializable {
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
    @Exclude
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the object
     * @param id The new ID
     */
    @Exclude
    public void setId(String id) {
        this.id = id;
    }
}
