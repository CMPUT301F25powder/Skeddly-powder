package com.example.skeddly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.databinding.HomeFragmentBinding;
import com.example.skeddly.ui.adapter.EventAdapter;
import com.example.skeddly.business.Event;
import com.example.skeddly.business.database.DatabaseObjects;


import java.util.ArrayList;


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
        eventAdapter = new EventAdapter(getContext(), eventList);

        // Set event adapter to list view
        binding.listEvents.setAdapter(eventAdapter);

        // Fetch events from firebase
        fetchEvents();

        return root;
    }

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
