package com.example.skeddly.ui.adapter;


import com.example.skeddly.business.database.SingleListenUpdate;
import com.example.skeddly.business.location.CustomLocation;

/**
 * Interface for retrieving a location. Implementors will call you back when the location has
 * been received.
 */
public interface RetrieveLocation {
    public void getLocation(SingleListenUpdate<CustomLocation> callback);
}
