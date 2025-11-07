package com.example.skeddly.ui;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skeddly.MainActivity;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.user.User;
import com.example.skeddly.databinding.HomeFragmentBinding;
import com.example.skeddly.ui.adapter.EventAdapter;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.database.DatabaseObjects;
import com.google.firebase.auth.FirebaseAuth;


import org.apache.commons.text.similarity.LevenshteinDetailedDistance;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment {
    private HomeFragmentBinding binding;
    private ArrayList<Event> eventList = new ArrayList<>();
    private DatabaseHandler databaseHandler;
    private EventAdapter eventAdapter;
    private SimpleCursorAdapter simpleCursorAdapter;
    private final String EVENT_NAME_SUGGESTION_ID = "eventName";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize DatabaseHandler and list of events
        databaseHandler = new DatabaseHandler();

        // Initialize event adapter
        MainActivity mainActivity = (MainActivity) requireActivity();
        eventAdapter = new EventAdapter(getContext(), eventList, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        // Set event adapter to list view
        binding.listEvents.setAdapter(eventAdapter);

        // Fetch events from firebase
        fetchEvents();

        initializeSearchBar(root.getContext());

        return root;
    }

    private void initializeSearchBar(Context context) {
        final String[] from = new String[] {EVENT_NAME_SUGGESTION_ID};
        final int[] to = new int[] {android.R.id.text1};

        SearchView searchBar = binding.searchEvents;
        simpleCursorAdapter = new SimpleCursorAdapter(context, android.R.layout.simple_list_item_1, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        searchBar.setSuggestionsAdapter(simpleCursorAdapter);

        searchBar.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) simpleCursorAdapter.getItem(position);
                String txt = cursor.getString(cursor.getColumnIndexOrThrow("eventName"));
                searchBar.setQuery(txt, true);
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }
        });

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isBlank()) {
                    fetchEvents();
                } else {
                    populateAdapter(newText);
                }

                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchEvents(query);
                populateAdapter(query);
                searchBar.clearFocus();

                return true;
            }
        });
    }

    private boolean checkNameSuggestionMatch(String name, String query) {
        if (query.isBlank()) {
            return true;
        }

        LevenshteinDistance levenshteinDistance = LevenshteinDistance.getDefaultInstance();

        int distance = levenshteinDistance.apply(name.toLowerCase(), query.toLowerCase());

        return distance <= 2;
    }

    private void populateAdapter(String query) {
        final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, EVENT_NAME_SUGGESTION_ID });
        for (int i = 0; i < eventList.size(); i++) {
            Event event = eventList.get(i);
            String eventName = event.getEventDetails().getName();

            int clampedEndNameIndex = Math.min(query.length(), eventName.length());
            String queryLengthName = eventName.substring(0, clampedEndNameIndex);

            if (checkNameSuggestionMatch(queryLengthName, query))
                c.addRow(new Object[] {i, eventName});
        }

        simpleCursorAdapter.changeCursor(c);
    }

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

                        if (checkNameSuggestionMatch(eventName, query)) {
                            eventList.add(event);
                        }
                    }

                    // Notify adapter of changes
                    eventAdapter.notifyDataSetChanged();

                }
        );
    }

    private void fetchEvents() {
        fetchEvents("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
