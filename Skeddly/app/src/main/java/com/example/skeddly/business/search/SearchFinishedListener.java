package com.example.skeddly.business.search;

/**
 * An interface for when we're done searching.
 */
public interface SearchFinishedListener {
    void onSearchFinished();
    void onSearchFinished(String query);
}
