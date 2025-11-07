package com.example.skeddly.ui.adapter;


import com.example.skeddly.business.database.SingleListenUpdate;
import com.example.skeddly.business.location.CustomLocation;

public interface RetrieveLocation {
    public void getLocation(SingleListenUpdate<CustomLocation> callback);
}
