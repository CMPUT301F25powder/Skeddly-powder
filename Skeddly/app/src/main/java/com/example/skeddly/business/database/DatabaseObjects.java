package com.example.skeddly.business.database;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

public class DatabaseObjects extends ArrayList<DatabaseObject> {

    public DatabaseObjects() {
    }

    public DatabaseObjects(int initialCapacity) {
        super(initialCapacity);
    }

    public DatabaseObjects(@NonNull Collection<? extends DatabaseObject> c) {
        super(c);
    }

    public ArrayList<String> getIds() {
        ArrayList<String> result = new ArrayList<>();

        for (DatabaseObject value : this) {
            result.add(value.getId());
        }

        return result;
    }
}
