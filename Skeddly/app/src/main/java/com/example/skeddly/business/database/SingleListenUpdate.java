package com.example.skeddly.business.database;

/**
 * Interface for a SingleListenUpdate
 * @param <T> The type of the value
 */
public interface SingleListenUpdate<T> {
    /**
     * Called when the value is updated
     * @param newValue The new value
     */
    void onUpdate(T newValue);

}