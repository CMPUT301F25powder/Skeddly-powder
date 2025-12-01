package com.example.skeddly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skeddly.MainActivity;
import com.example.skeddly.R;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.database.SingleListenUpdate;
import com.example.skeddly.business.database.repository.EventRepository;
import com.example.skeddly.business.location.CustomLocation;
import com.example.skeddly.business.search.EventFilter;
import com.example.skeddly.business.user.User;
import com.example.skeddly.databinding.FragmentHomeBinding;
import com.example.skeddly.business.search.EventSearch;
import com.example.skeddly.business.search.SearchFinishedListener;
import com.example.skeddly.ui.adapter.EventAdapter;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.ui.adapter.RetrieveLocation;
import com.example.skeddly.ui.filtering.EventFilterPopup;
import com.example.skeddly.ui.filtering.FilterUpdatedListener;
import com.example.skeddly.ui.utility.LocationFetcherFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Fragment for the home screen
 */
public class HomeFragment extends Fragment implements RetrieveLocation {
    private FragmentHomeBinding binding;
    private ArrayList<Event> eventList = new ArrayList<>();
    private DatabaseHandler databaseHandler;
    private EventAdapter eventAdapter;
    private ListView listEvents;
    private EventFilterPopup eventFilterPopup;
    private View popupView;
    private SearchView searchEvents;
    private ImageButton filterDropdownButton;
    private View circleBadge;
    private User user;

    // Search
    private EventSearch eventSearch;
    TextView noResultsText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize DatabaseHandler and list of events
        databaseHandler = new DatabaseHandler();
        searchEvents = binding.searchEvents;
        eventSearch = new EventSearch(getContext(), searchEvents, eventList);
        MainActivity activity = (MainActivity) requireActivity();
        user = activity.getUser();

        // Handle opening filter menu
        filterDropdownButton = binding.btnFilter;

        popupView = inflater.inflate(R.layout.fragment_event_filter_menu, null);

        resetFilterPopup();

        // Initialize event adapter
        eventAdapter = new EventAdapter(
                getContext(),
                eventList,
                activity.getUser(),
                this,
                R.id.action_navigation_home_to_event_view_info, // View Info Action for Home
                R.id.action_navigation_home_to_edit_event      // Edit Action for Home
        );

        // Set event adapter to list view
        listEvents = binding.listEvents;
        listEvents.setAdapter(eventAdapter);

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

        // Other UI elements
        noResultsText = binding.noResultsAlert;
        circleBadge = binding.circleBadge;

        // Set the circle badge (on the filter dropdown button) to invisible by default.
        circleBadge.setVisibility(View.GONE);

        return root;
    }

    /**
     * Fetches events from Firebase and updates the event adapter.
     */
    private void fetchEvents(String query) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        EventRepository eventRepository = new EventRepository(firestore);
        EventFilter eventFilter = eventFilterPopup.getEventFilter();

        eventRepository.getAll().addOnSuccessListener(new OnSuccessListener<List<Event>>() {
            @Override
            public void onSuccess(List<Event> events) {
                // Clear existing list
                eventList.clear();

                // Add new events to list
                for (Event event : events) {
                    String eventName = event.getEventDetails().getName();

                    boolean nameSuggestionMatch = eventSearch.checkNameSuggestionMatch(eventName, query);
                    boolean privilegeMatch = checkPrivilegeMatch(event);

                    if (privilegeMatch && ((eventFilterPopup.filterReady() && eventFilter.checkFilterCriteria(event) && nameSuggestionMatch) || (!eventFilterPopup.filterReady() && nameSuggestionMatch))) {
                        eventList.add(event);
                    }
                }

                toggleNoResultsTextVisibility();

                // Notify adapter of changes
                eventAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Fetches events from Firebase and updates the event adapter.
     */
    private void fetchEvents() {
        fetchEvents("");
    }

    /**
     * Toggles whether or not the "no results" text appears in the home screen.
     * Based on if eventList is empty or not.
     */
    private void toggleNoResultsTextVisibility() {
        if (eventList.isEmpty()) {
            noResultsText.setVisibility(View.VISIBLE);
            listEvents.setVisibility(View.GONE);
        } else {
            noResultsText.setVisibility(View.GONE);
            listEvents.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Resets the filter popup UI menu.
     * Used for when the filter is reset or when the UI first loads.
     */
    private void resetFilterPopup() {
        this.eventFilterPopup = new EventFilterPopup(getContext(), getChildFragmentManager(), popupView, user, searchEvents, filterDropdownButton);

        eventFilterPopup.setOnFilterUpdatedListener(new FilterUpdatedListener() {
            @Override
            public void onFilterUpdated(boolean cleared) {
                eventFilterPopup.dismiss();

                if (cleared || eventFilterPopup.getEventFilter().isBlank()) {
                    resetFilterPopup();
                    fetchEvents();
                    circleBadge.setVisibility(View.GONE);
                } else {
                    fetchEvents();
                    circleBadge.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private boolean checkPrivilegeMatch(Event event) {
        switch (user.getPrivilegeLevel()) {
            // if entrant, don't show events that are not joinable
            case ENTRANT:
                if (!event.isJoinable()) {
                    return false;
                }
                break;
            // if organizer, don't show events that are not joinable unless they are the organizer
            case ORGANIZER:
                if (!event.isJoinable() && !Objects.equals(event.getOrganizer(), user.getId())) {
                    return false;
                }
                break;
            // Admins are allowed to see everything
        }

        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
