package com.example.skeddly.business.database;

public interface SingleListenUpdate<T> {
    void onUpdate(T newValue);
}