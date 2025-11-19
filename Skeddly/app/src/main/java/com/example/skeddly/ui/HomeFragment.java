package com.example.skeddly.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.database.SingleListenUpdate;
import com.example.skeddly.business.location.CustomLocation;
import com.example.skeddly.databinding.FragmentHomeBinding;
import com.example.skeddly.business.search.EventSearch;
import com.example.skeddly.business.search.SearchFinishedListener;
import com.example.skeddly.databinding.HomeFragmentBinding;
import com.example.skeddly.ui.adapter.EventAdapter;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.database.DatabaseObjects;
import com.example.skeddly.ui.adapter.RetrieveLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;


import org.apache.commons.text.similarity.LevenshteinDetailedDistance;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * Fragment for the home screen
 */
public class HomeFragment extends Fragment implements RetrieveLocation {
    private FragmentHomeBinding binding;
    private ArrayList<Event> eventList = new ArrayList<>();
    private DatabaseHandler databaseHandler;
    private EventAdapter eventAdapter;

    // Search
    private EventSearch eventSearch;

    // Location Stuff
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private final String[] needed_permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize DatabaseHandler and list of events
        databaseHandler = new DatabaseHandler();

        eventSearch = new EventSearch(getContext(), binding.searchEvents, eventList);

        // Initialize event adapter
        eventAdapter = new EventAdapter(getContext(),
                eventList,
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(),
                this);

        // For getting our current location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // GET PERMISSION THING
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                // its mad at me
                boolean fine_granted = result.getOrDefault(needed_permissions[0], false);
                boolean coarse_granted = result.getOrDefault(needed_permissions[1], false);

                if (fine_granted && coarse_granted) {
                    Toast.makeText(getContext(), "Location granted. Please try again.", Toast.LENGTH_SHORT);
                }
            }
        });

        // Set event adapter to list view
        binding.listEvents.setAdapter(eventAdapter);

        // Fetch events from firebase
        fetchEvents();

        eventSearch.setOnSearchFinishedListener(new SearchFinishedListener() {
            @Override
            public void onSearchFinished() {
                fetchEvents();
            }

            @Override
            public void onSearchFinished(String query) {
                fetchEvents(query);
            }
        });

        return root;
    }

    /**
     * Fetches events from Firebase and updates the event adapter.
     */
    private void fetchEvents(String query) {
        // Fetch events from firebase
        databaseHandler.iterableListen(databaseHandler.getEventsPath(),
                Event.class,
                (DatabaseObjects dbObjects) -> {
                    // Clear existing list
                    eventList.clear();

                    // Add new events to list
                    for (int i = 0; i < dbObjects.size(); i++) {
                        Event event = (Event) dbObjects.get(i);
                        String eventName = event.getEventDetails().getName();

                        if (eventSearch.checkNameSuggestionMatch(eventName, query)) {
                            eventList.add(event);
                        }
                    }

                    // Notify adapter of changes
                    eventAdapter.notifyDataSetChanged();

                }
        );
    }

    /**
     * Fetches events from Firebase and updates the event adapter.
     */
    private void fetchEvents() {
        fetchEvents("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Gets the location from the device and return it in the provided callback.
     * Requests permission if needed.
     * @param callback Who to callback when we got the location.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void getLocation(SingleListenUpdate<CustomLocation> callback) {
        if (checkPermissions()) {
            CancellationTokenSource cts = new CancellationTokenSource();
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.getToken()).addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        callback.onUpdate(new CustomLocation(location.getLongitude(), location.getLatitude()));
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), "Please grant location permission.", Toast.LENGTH_SHORT);
        }
    }

    /**
     * Check whether we have the required permissions to get the device's location. Request
     * permission if needed.
     * @return True if we have permission. False otherwise.
     */
    public boolean checkPermissions() {
        // We need to get the required permissions
        ArrayList<String> needed_permissions = new ArrayList<>();
        boolean granted = false;

        for (String permission : this.needed_permissions) {
            // If we don't have it, add it to the list
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                needed_permissions.add(permission);
            }
        }

        // Request perms if needed
        if (!needed_permissions.isEmpty()) {
            String[] perms = new String[2];
            Toast.makeText(getContext(), "Please grant location permission.", Toast.LENGTH_SHORT);
            requestPermissionLauncher.launch(needed_permissions.toArray(perms));
            return false;
        }

        return true;
    }
}
