package com.example.skeddly.business;

import java.util.ArrayList;

public interface SingleListenUpdate<T> {
    void onUpdate(T newValue);
}