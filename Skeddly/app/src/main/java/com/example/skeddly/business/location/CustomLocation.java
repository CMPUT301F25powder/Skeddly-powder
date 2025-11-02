package com.example.skeddly.business.location;

import com.example.skeddly.business.database.DatabaseObject;

public class CustomLocation extends DatabaseObject {
    private long longitude;
    private long latitude;

    public CustomLocation(long longitude, long latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }
}
