package com.example.skeddly.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.skeddly.MainActivity;
import com.example.skeddly.R;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.event.EventDetail;
import com.example.skeddly.business.event.EventSchedule;
import com.example.skeddly.business.user.Authenticator;
import com.example.skeddly.business.user.User;
import com.example.skeddly.business.user.UserLevel;
import com.example.skeddly.databinding.EventViewAdminBinding;
import com.example.skeddly.databinding.EventViewFragmentBinding;
import com.example.skeddly.ui.adapter.EventAdapter;
import com.example.skeddly.ui.popup.QRPopupDialogFragment;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Locale;

/**
 * Fragment for the event view screen
 */
public class EventViewInfoFragment extends Fragment {
    private EventViewAdminBinding binding;
    private DatabaseHandler dbHandler;
    private String eventId;
    private String userId;
    private String organizerId;
    private EventAdapter eventAdapter;
    private ValueEventListener valueEventListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = EventViewAdminBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize database handler
        dbHandler = new DatabaseHandler(getContext());

        // Initialize eventAdapter
        eventAdapter = new EventAdapter(getContext(), new ArrayList<>(), userId);

        // Get the eventId passed from the HomeFragment
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            userId = getArguments().getString("userId");
            organizerId = getArguments().getString("organizerId");
        }

        // Set view based on level
        boolean isOrganizer = userId.equals(organizerId);
        User currentUser = ((MainActivity) requireActivity()).getUser();
        if (currentUser != null && (isOrganizer || currentUser.getPrivilegeLevel() == UserLevel.ADMIN)) {
            // USER IS ADMIN/ORGANIZER: Show admin buttons, hide join button.
            binding.adminButtonGroup.setVisibility(View.VISIBLE);
            binding.buttonJoin.setVisibility(View.GONE);
        } else {
            // USER IS ENTRANT: Hide admin buttons, show join button.
            binding.adminButtonGroup.setVisibility(View.GONE);
            binding.buttonJoin.setVisibility(View.VISIBLE);
        }

        // Fetch data from Firebase if we have a valid ID
        if (eventId != null && !eventId.isEmpty()) {
            fetchEventDetails();
        } else {
            // Log an error if for some reason the eventId wasn't passed correctly
            Log.e("EventViewInfoFragment", "Event ID is null or empty!");
        }

        // === QR Code shenanigans ===
        ImageButton buttonQrCode = binding.buttonQrCode;
        buttonQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRPopupDialogFragment qpf = QRPopupDialogFragment.newInstance(String.format("skeddly://event/%s", eventId));
                qpf.show(getChildFragmentManager(), null);
            }
        });

        // Set up Participant button
        binding.buttonParticipants.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("eventId", eventId);
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_event_view_info_to_participant_list, bundle);
        });

        // Set up the back button to navigate up
        binding.buttonBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigateUp();
        });

        return root;
    }

    /**
     * Fetches the details for an event from Firebase using its ID.
     */
    private void fetchEventDetails() {
        // Use addValueEventListener to continuously listen for changes to the event data.
        this.valueEventListener = new com.google.firebase.database.ValueEventListener() {
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
        };
        dbHandler.getEventsPath().child(eventId).addValueEventListener(this.valueEventListener);
    }

    /**
     * Populates the UI elements with data from the fetched Event object.
     */
    private void populateUI(Event event) {
        Glide.with(this).load(Base64.getDecoder().decode(event.getImageb64())).into(binding.eventImage);
        EventDetail eventDetails = event.getEventDetails();
        EventSchedule eventSchedule = event.getEventSchedule();

        // Set Title and Location
        if (event.getLocation() != null) {
            binding.textEventTitleOverlay.setText(String.format("%s - %s", eventDetails.getName(), event.getLocation().toString()));
        } else {
            binding.textEventTitleOverlay.setText(eventDetails.getName());
        }

        // Set Event Time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM d, HH:mm");
        LocalDateTime startTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(eventSchedule.getStartTime()), ZoneId.systemDefault());
        LocalDateTime endTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(eventSchedule.getEndTime()), ZoneId.systemDefault());
        binding.textDayOfWeek.setText(String.format("%s - %s", startTime.format(formatter), endTime.toLocalTime().toString()));

        // Set Information Fields
        binding.valueEventTitle.setText(eventDetails.getName());
        binding.valueDescription.setText(eventDetails.getDescription());

        if (eventDetails.getCategories() != null) {
            binding.valueCategory.setText(String.join(", ", eventDetails.getCategories()));
        } else {
            binding.valueCategory.setText("N/A");
        }

        // Calculate and display Attendee Count
        int currentAttendees = 0;
        if (event.getParticipantList() != null && event.getParticipantList().getTicketIds() != null) {
            currentAttendees = event.getParticipantList().getTicketIds().size();
        }
        binding.valueAttendeeLimit.setText(String.format(Locale.getDefault(), "%d / %d", currentAttendees, event.getParticipantList().getMaxAttend()));

        // Calculate and display Waitlist Count
        int currentWaitlist = 0;
        int maxWaitlist = 0;
        if (event.getWaitingList() != null) {
            if (event.getWaitingList().getTicketIds() != null) {
                currentWaitlist = event.getWaitingList().getTicketIds().size();
            }
            maxWaitlist = event.getWaitingList().getLimit();
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
        dbHandler.getEventsPath().child(eventId).removeEventListener(valueEventListener);
        binding = null;
    }
}
