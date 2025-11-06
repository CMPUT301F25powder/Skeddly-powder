package com.example.skeddly.business.location;

import androidx.annotation.NonNull;

import java.util.Locale;

public class CustomLocation {
    private double longitude;
    private double latitude;

    public CustomLocation() {

    }

    public CustomLocation(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "(%.2f, %.2f)", longitude, latitude);
    }
}
