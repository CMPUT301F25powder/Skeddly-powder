package com.example.skeddly.business;

import java.util.UUID;

public class DatabaseObject {
    private String id; // Needs to be public so it can be serialized into the db.

    public DatabaseObject() {
        this.id = String.valueOf(UUID.randomUUID());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
