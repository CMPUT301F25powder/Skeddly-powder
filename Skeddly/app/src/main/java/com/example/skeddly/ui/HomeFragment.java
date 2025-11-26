package com.example.skeddly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skeddly.MainActivity;
import com.example.skeddly.R;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.database.SingleListenUpdate;
import com.example.skeddly.business.location.CustomLocation;
import com.example.skeddly.databinding.FragmentHomeBinding;
import com.example.skeddly.business.search.EventSearch;
import com.example.skeddly.business.search.SearchFinishedListener;
import com.example.skeddly.ui.adapter.EventAdapter;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.database.DatabaseObjects;
import com.example.skeddly.ui.adapter.RetrieveLocation;
import com.example.skeddly.ui.utility.LocationFetcherFragment;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.UUID;

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

    private ListenerRegistration fetchEventsRegistration = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize DatabaseHandler and list of events
        databaseHandler = new DatabaseHandler();

        eventSearch = new EventSearch(getContext(), binding.searchEvents, eventList);

        // Initialize event adapter
        MainActivity activity = (MainActivity) requireActivity();
        eventAdapter = new EventAdapter(
                getContext(),
                eventList,
                activity.getUser(),
                null,
                R.id.action_navigation_home_to_event_view_info, // View Info Action for Home
                R.id.action_navigation_home_to_edit_event      // Edit Action for Home
        );

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
        if (fetchEventsRegistration != null) {
            fetchEventsRegistration.remove();
        }

        // Fetch events from firebase
        fetchEventsRegistration = databaseHandler.iterableListen(databaseHandler.getEventsPath(),
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

        if (fetchEventsRegistration != null) {
            fetchEventsRegistration.remove();
            fetchEventsRegistration = null;
        }
    }

    /**
     * Gets the location from the device and return it in the provided callback.
     * @param callback Who to callback when we got the location.
     */
    @Override
    public void getLocation(SingleListenUpdate<CustomLocation> callback) {
        String generatedRequestKey = String.valueOf(UUID.randomUUID());
        LocationFetcherFragment locationFetcherFragment = LocationFetcherFragment.newInstance(generatedRequestKey);
        getChildFragmentManager().beginTransaction().add(locationFetcherFragment, null).commit();
        getChildFragmentManager().setFragmentResultListener(generatedRequestKey, this, (requestKey, result) -> {
            callback.onUpdate(result.getParcelable("location"));
            getChildFragmentManager().beginTransaction().remove(locationFetcherFragment).commit();
        });
    }
}
