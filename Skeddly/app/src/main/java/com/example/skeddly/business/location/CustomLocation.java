package com.example.skeddly.business.location;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Locale;

/**
 * A single location on Earth, represented by its longitude and latitude.
 */
public class CustomLocation implements Parcelable {
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
     * Construct a new CustomLocation from a Parcel.
     * @param in The Parcel containing the latitude and longitude.
     */
    public CustomLocation(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    public static final Parcelable.Creator<CustomLocation> CREATOR = new Parcelable.Creator<>() {
        public CustomLocation createFromParcel(Parcel in) {
            return new CustomLocation(in);
        }

        public CustomLocation[] newArray(int size) {
            return new CustomLocation[size];
        }
    };
}
