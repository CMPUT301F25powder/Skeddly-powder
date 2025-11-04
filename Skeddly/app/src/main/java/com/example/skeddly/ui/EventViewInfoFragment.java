package com.example.skeddly.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.skeddly.business.Event;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.databinding.EventViewFragmentBinding;
import com.example.skeddly.ui.adapter.EventAdapter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;


public class EventViewInfoFragment extends Fragment {
    private EventViewFragmentBinding binding;
    private DatabaseHandler dbHandler;
    private String eventId;
    private String userId;
    private EventAdapter eventAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = EventViewFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize database handler
        dbHandler = new DatabaseHandler(getContext());

        // Initialize eventAdapter
        eventAdapter = new EventAdapter(getContext(), new ArrayList<>());

        // Get the eventId passed from the HomeFragment
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            userId = getArguments().getString("userId");
        } else {
        }

        // Fetch data from Firebase if we have a valid ID
        if (eventId != null && !eventId.isEmpty()) {
            fetchEventDetails();
        } else {
            // Log an error if for some reason the eventId wasn't passed correctly
            Log.e("EventViewInfoFragment", "Event ID is null or empty!");
        }

        // Set up the back button to navigate up
        binding.buttonBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigateUp(); // The correct way to go back
        });

        return root;
    }

    /**
     * Fetches the details for an event from Firebase using its ID.
     */
    private void fetchEventDetails() {
        // Use addValueEventListener to continuously listen for changes to the event data.
        dbHandler.getEventsPath().child(eventId).addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);
                if (event != null) {
                    // Set the ID manually since it's the key of the snapshot
                    event.setId(snapshot.getKey());

                    // Set button state and on click listener
                    eventAdapter.updateJoinButtonState(binding.buttonJoin, event, userId, dbHandler);

                    // Once data is loaded or updated, populate the screen
                    populateUI(event);
                } else {
                    Log.e("EventViewInfoFragment", "Event data is null for ID: " + eventId);
                }
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                Log.e("EventViewInfoFragment", "Database error: " + error.getMessage());
            }
        });
    }

    /**
     * Populates the UI elements with data from the fetched Event object.
     */
    private void populateUI(Event event) {
        // Set Title and Location
        if (event.getLocation() != null) {
            binding.textEventTitleOverlay.setText(String.format("%s - %s", event.getName(), event.getLocation()));
        } else {
            binding.textEventTitleOverlay.setText(event.getName());
        }

        // Set Event Time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM d, HH:mm");
        LocalDateTime startTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(event.getStartTime()), ZoneId.systemDefault());
        LocalDateTime endTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(event.getEndTime()), ZoneId.systemDefault());
        binding.textEventTime.setText(String.format("%s - %s", startTime.format(formatter), endTime.toLocalTime().toString()));

        // Set Information Fields
        binding.valueEventTitle.setText(event.getName());
        binding.valueDescription.setText(event.getDescription());
        binding.valueCategory.setText(event.getCategory());

        // Format Geolocation Text
        if (event.getLocation() != null) {
            binding.valueGeolocation.setText(String.format(Locale.getDefault(), "Within %.1fkm of venue", event.getLocation()));
        } else {
            binding.valueGeolocation.setText("Not required");
        }

        // Calculate and display Attendee Count
        int currentAttendees = 0;
        if (event.getAttendees() != null && event.getAttendees().getUserList() != null) {
            currentAttendees = event.getAttendees().getUserList().size();
        }
        binding.valueAttendeeLimit.setText(String.format(Locale.getDefault(), "%d / %d", currentAttendees, event.getAttendeeLimit()));

        // Calculate and display Waitlist Count
        int currentWaitlist = 0;
        int maxWaitlist = 0;
        if (event.getApplicants() != null) {
            if (event.getApplicants().getTicketIds() != null) {
                currentWaitlist = event.getApplicants().getTicketIds().size();
            }
            maxWaitlist = event.getApplicants().getLimit();
        }

        if (maxWaitlist == Integer.MAX_VALUE) {
            binding.valueWaitlistLimit.setText(String.format(Locale.getDefault(), "%d / ∞", currentWaitlist));
        } else {
            binding.valueWaitlistLimit.setText(String.format(Locale.getDefault(), "%d / %d", currentWaitlist, maxWaitlist));
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
