package com.example.skeddly.business.database;

import com.google.firebase.database.Exclude;

import java.util.UUID;

/**
 * An object that can be serialized into the DB
 * Any values in its fields will be turned into DB keys by the {@link DatabaseHandler}
 * @see DatabaseHandler
 */
public class DatabaseObject {
    private String id;

    public DatabaseObject() {
        this.id = String.valueOf(UUID.randomUUID());
    }
    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
