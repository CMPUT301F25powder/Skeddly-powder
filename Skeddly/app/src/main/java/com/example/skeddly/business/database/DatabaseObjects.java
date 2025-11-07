package com.example.skeddly.business.database;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

public class DatabaseObjects extends ArrayList<DatabaseObject> {

    /**
     * Constructor for the DatabaseObjects
     */
    public DatabaseObjects() {
    }

    /**
     * Constructor for the DatabaseObjects
     * @param initialCapacity The initial capacity of the array
     */
    public DatabaseObjects(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructor for the DatabaseObjects with a collection
     * @param c The collection to copy from
     */
    public DatabaseObjects(@NonNull Collection<? extends DatabaseObject> c) {
        super(c);
    }

    /**
     * Returns the IDs of the objects in the array
     * @see DatabaseObject
     * @return The IDs of the objects in the array
     */
    public ArrayList<String> getIds() {
        ArrayList<String> result = new ArrayList<>();

        for (DatabaseObject value : this) {
            result.add(value.getId());
        }

        return result;
    }
}
