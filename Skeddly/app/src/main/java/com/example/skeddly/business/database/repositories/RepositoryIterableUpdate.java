package com.example.skeddly.business.database.repositories;

import com.example.skeddly.business.database.DatabaseObject;

import java.util.List;

public interface RepositoryIterableUpdate<T extends DatabaseObject> {
    void onUpdate(List<T> objects);
}
