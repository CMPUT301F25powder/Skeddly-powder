package com.example.skeddly.business.database;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

public class DatabaseObjects<T extends DatabaseObject> extends ArrayList<T> {
    final Class<T> parameter;

    public DatabaseObjects() {
        parameter = (Class<T>) DatabaseObject.class;
    }

    public DatabaseObjects(Class<T> parameter) {
        this.parameter = parameter;
    }

    public DatabaseObjects(Class<T> parameter, int initialCapacity) {
        super(initialCapacity);

        this.parameter = parameter;
    }

    public DatabaseObjects(Class<T> parameter, @NonNull Collection<? extends T> c) {
        super(c);

        this.parameter = parameter;
    }

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
