package com.example.skeddly.business.database;

import com.google.firebase.database.Exclude;

import java.util.UUID;

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
