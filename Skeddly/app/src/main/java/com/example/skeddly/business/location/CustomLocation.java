package com.example.skeddly.business.location;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

/**
 * A single location on Earth, represented by its longitude and latitude.
 */
public class CustomLocation implements Parcelable {
    private double latitude;
    private double longitude;

    @Nullable
    private String tag;

    /**
     * No-arg constructor for CustomLocation. Required by Firebase.
     */
    public CustomLocation() {

    }

    /**
     * Construct a new CustomLocation, given the latitude and longitude.
     * Stores a tag alongside the given coordinates.
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @param tag The tag to associate with the location.
     */
    public CustomLocation(double latitude, double longitude, @Nullable String tag) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.tag = tag;
    }

    /**
     * Construct a new CustomLocation, given the latitude and longitude.
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     */
    public CustomLocation(double latitude, double longitude) {
        this(latitude, longitude, null);
    }

    /**
     * Construct a new CustomLocation from a Parcel.
     * @param in The Parcel containing the latitude and longitude.
     */
    public CustomLocation(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.tag = in.readString();
    }

    /**
     * Retrieve the latitude of the location.
     * @return The latitude as a double.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Retrieve the longitude of the location.
     * @return The longitude as a double.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Retrieve the tag associated with the location.
     * @return The tag associated with the location.
     */
    @Nullable
    public String getTag() {
        return tag;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "(%.5f, %.5f)", latitude, longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.tag);
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
