package com.example.skeddly.business.search;

public interface SearchFinishedListener {
    void onSearchFinished();
    void onSearchFinished(String query);
}