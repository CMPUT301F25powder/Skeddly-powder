package com.example.skeddly.business.database;

import java.util.ArrayList;

public interface IterableListenUpdate<T> {
    void onUpdate(DatabaseObjects newValues);
}
