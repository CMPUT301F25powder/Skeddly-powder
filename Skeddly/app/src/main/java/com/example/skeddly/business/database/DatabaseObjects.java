package com.example.skeddly.business.database;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * DatabaseObjects are an ArrayList of DatabaseObjects
 */
public class DatabaseObjects<T extends DatabaseObject> extends ArrayList<T> {
    final Class<T> parameter;

    /**
     * No-Arg Constructor for DatabaseObjects
     */
    public DatabaseObjects() {
        parameter = (Class<T>) DatabaseObject.class;
    }

    /**
     * Constructor for DatabaseObjects
     * @param parameter The class stored in the list
     */
    public DatabaseObjects(Class<T> parameter) {
        this.parameter = parameter;
    }

    /**
     * Constructor for the DatabaseObjects
     * @param parameter The class stored in the list
     * @param initialCapacity The initial capacity of the array
     */
    public DatabaseObjects(Class<T> parameter, int initialCapacity) {
        super(initialCapacity);

        this.parameter = parameter;
    }

    /**
     * Constructor for the DatabaseObjects with a collection
     * @param parameter The class stored in the list
     * @param c The collection to copy from
     */
    public DatabaseObjects(Class<T> parameter, @NonNull Collection<? extends T> c) {
        super(c);

        this.parameter = parameter;
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

    public Class<T> getParameter() {
        return parameter;
    }
}
