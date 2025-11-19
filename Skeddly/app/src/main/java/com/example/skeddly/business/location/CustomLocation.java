package com.example.skeddly.business.location;

import androidx.annotation.NonNull;

import java.util.Locale;

/**
 * A single location on Earth, represented by its longitude and latitude.
 */
public class CustomLocation {
    private double longitude;
    private double latitude;

    /**
     * No-arg constructor for CustomLocation. Required by Firebase.
     */
    public CustomLocation() {

    }

    /**
     * Construct a new CustomLocation, given the latitude and longitude.
     * @param longitude The longitude of the location.
     * @param latitude The latitude of the location.
     */
    public CustomLocation(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Retrieve the longitude of the location.
     * @return The longitude as a double.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude of the location.
     * @param longitude The new longitude that should be set.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Retrieve the latitude of the location.
     * @return The latitude as a double.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude of the location.
     * @param latitude The new latitude that should be set.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "(%.2f, %.2f)", longitude, latitude);
    }
}
