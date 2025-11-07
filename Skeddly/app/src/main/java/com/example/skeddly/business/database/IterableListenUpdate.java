package com.example.skeddly.business.database;

/**
 * Interface for an IterableListenUpdate
 */
public interface IterableListenUpdate {
    /**
     * Called when the values are updated
     * @param newValues The new values
     */
    void onUpdate(DatabaseObjects newValues);
}
