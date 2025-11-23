package com.example.skeddly.ui.utility;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.skeddly.business.location.CustomLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Map;

/**
 * This fragment retrieves the user's location and requests the required permissions if needed.
 * The location is returned as a fragment result. If the location could not be retrieved, the
 * result is NULL.
 */
public class LocationFetcherFragment extends Fragment {
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private FusedLocationProviderClient fusedLocationClient;
    private String requestKey = "requestKey";

    /**
     * Create a new instance of the LocationFetcherFragment.
     * @param requestKey The requestKey to use for the result
     * @return The newly constructed LocationFetcherFragment
     */
    public static LocationFetcherFragment newInstance(String requestKey) {
        Bundle args = new Bundle();
        args.putString("requestKey", requestKey);

        LocationFetcherFragment fragment = new LocationFetcherFragment();
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupArgs();

        // For getting our current location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // GET PERMISSION THING
        ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                boolean fine_granted = Boolean.TRUE.equals(result.getOrDefault(REQUIRED_PERMISSIONS[0], false));
                boolean coarse_granted = Boolean.TRUE.equals(result.getOrDefault(REQUIRED_PERMISSIONS[1], false));

                if (fine_granted && coarse_granted) {
                    getLocation();
                } else {
                    sendResult(null);
                }
            }
        });

        // We need to get the required permissions
        ArrayList<String> needed_permissions = new ArrayList<>();

        for (String permission : REQUIRED_PERMISSIONS) {
            // If we don't have it, add it to the list
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                needed_permissions.add(permission);
            }
        }

        // Request perms if needed
        if (!needed_permissions.isEmpty()) {
            String[] perms = new String[2];
            requestPermissionLauncher.launch(needed_permissions.toArray(perms));
        } else {
            // We have all the permissions
            getLocation();
        }
    }

    /**
     * Gets the location from the device. Requires the necessary permissions.
     */
    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    private void getLocation() {
        CancellationTokenSource cts = new CancellationTokenSource();
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.getToken()).addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    sendResult(new CustomLocation(location.getLongitude(), location.getLatitude()));
                } else {
                    sendResult(null);
                }
            }
        });
    }

    /**
     * Unpacks the arguments that are given to the fragment.
     */
    private void setupArgs() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            requestKey = bundle.getString("requestKey");
        }
    }

    /**
     * Sends the given location as a Fragment result back to the parent.
     * @param location The location to send as a result. Can be NULL.
     */
    private void sendResult(CustomLocation location) {
        Bundle args = new Bundle();
        args.putSerializable("location", location);
        getParentFragmentManager().setFragmentResult(requestKey, args);
    }
}
