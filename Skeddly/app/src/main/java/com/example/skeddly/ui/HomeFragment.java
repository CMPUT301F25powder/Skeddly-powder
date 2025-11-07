package com.example.skeddly.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


import java.util.ArrayList;
import java.util.Objects;

/**
 * Fragment for the home screen
 */
public class HomeFragment extends Fragment {
    private HomeFragmentBinding binding;
    private ArrayList<Event> eventList;
    private DatabaseHandler databaseHandler;
    private EventAdapter eventAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize DatabaseHandler and list of events
        databaseHandler = new DatabaseHandler(getContext());
        eventList = new ArrayList<>();

        // Initialize event adapter
        MainActivity mainActivity = (MainActivity) requireActivity();
        eventAdapter = new EventAdapter(getContext(), eventList, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        // Set event adapter to list view
        binding.listEvents.setAdapter(eventAdapter);

        // Fetch events from firebase
        fetchEvents();

        return root;
    }

    /**
     * Fetches events from Firebase and updates the event adapter.
     */
    private void fetchEvents() {
        // Fetch events from firebase
        databaseHandler.iterableListen(databaseHandler.getEventsPath(),
                Event.class,
                (DatabaseObjects dbObjects) -> {
                    // Clear existing list
                    eventList.clear();

                    // Add new events to list
                    for (int i = 0; i < dbObjects.size(); i++) {
                        eventList.add((Event) dbObjects.get(i));
                    }

                    // Notify adapter of changes
                    eventAdapter.notifyDataSetChanged();

                }
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
