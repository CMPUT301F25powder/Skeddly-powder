package com.example.skeddly.business.database;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * An object that can be serialized into the DB
 * Any values in its fields will be turned into DB keys by the {@link DatabaseHandler}
 * @see DatabaseHandler
 */
public class DatabaseObject implements Serializable {
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

    public Method[] getDatabaseObjectRelatedMethods() {
        Stream<Method> stream = Arrays.stream(this.getClass().getDeclaredMethods()).filter(method -> method.getReturnType() == DatabaseObjects.class);

        return (Method[]) stream.toArray();
    }
}
