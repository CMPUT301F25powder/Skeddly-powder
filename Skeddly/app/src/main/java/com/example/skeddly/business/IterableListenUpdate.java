package com.example.skeddly.business;

import java.util.ArrayList;

public interface IterableListenUpdate<T> {
    void onUpdate(ArrayList<T> newValues);
}
