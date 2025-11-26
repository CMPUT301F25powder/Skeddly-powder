package com.example.skeddly.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.skeddly.MainActivity;
import com.example.skeddly.R;
import com.example.skeddly.business.database.SingleListenUpdate;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.event.EventDetail;
import com.example.skeddly.business.event.EventSchedule;
import com.example.skeddly.business.location.CustomLocation;
import com.example.skeddly.business.user.User;
import com.example.skeddly.business.user.UserLevel;
import com.example.skeddly.databinding.FragmentEventInfoBinding;
import com.example.skeddly.ui.adapter.EventAdapter;
import com.example.skeddly.ui.adapter.RetrieveLocation;
import com.example.skeddly.ui.popup.DrawParticipantsDialogFragment;
import com.example.skeddly.ui.popup.MapPopupDialogFragment;
import com.example.skeddly.ui.popup.QRPopupDialogFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;

/**
 * Fragment for the event view info screen
 */
public class EventViewInfoFragment extends Fragment implements RetrieveLocation {
    private FragmentEventInfoBinding binding;
    private DatabaseHandler dbHandler;
    private String eventId;
    private String userId;
    private String organizerId;
    private EventAdapter eventAdapter;
    private ListenerRegistration eventSnapshotListenerReg;

    // Location Stuff
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private final String[] needed_permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEventInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize database handler
        dbHandler = new DatabaseHandler();
        eventSnapshotListenerReg = null;

        // Initialize eventAdapter
        MainActivity activity = (MainActivity) requireActivity();
        userId = activity.getUser().getId();
        eventAdapter = new EventAdapter(
                getContext(),
                new ArrayList<>(),
                activity.getUser(),
                this,
                R.id.action_event_view_info_to_participant_list,
                R.id.action_navigation_event_view_info_to_edit_event
        );

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


        // Get the eventId passed from the HomeFragment
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            organizerId = getArguments().getString("organizerId");
        }

        // Set view based on level
        boolean isOrganizer = userId.equals(organizerId);
        User currentUser = ((MainActivity) requireActivity()).getUser();
        if (currentUser != null && (isOrganizer || currentUser.getPrivilegeLevel() == UserLevel.ADMIN)) {
            // USER IS ADMIN/ORGANIZER: Show admin buttons, hide join button.
            binding.btnGroupAdmin.setVisibility(View.VISIBLE);
            binding.btnJoin.setVisibility(View.GONE);
        } else {
            // USER IS ENTRANT: Hide admin buttons, show join button.
            binding.btnGroupAdmin.setVisibility(View.GONE);
            binding.btnJoin.setVisibility(View.VISIBLE);
        }

        // Fetch data from Firebase if we have a valid ID
        if (eventId != null && !eventId.isEmpty()) {
            fetchEventDetails();
        } else {
            // Log an error if for some reason the eventId wasn't passed correctly
            Log.e("EventViewInfoFragment", "Event ID is null or empty!");
        }

        // === QR Code shenanigans ===
        ImageButton buttonQrCode = binding.btnQrCode;
        buttonQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRPopupDialogFragment qpf = QRPopupDialogFragment.newInstance(String.format("skeddly://event/%s", eventId));
                qpf.show(getChildFragmentManager(), null);
            }
        });

        // Set up Participant button
        binding.btnParticipants.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("eventId", eventId);
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_event_view_info_to_participant_list, bundle);
        });

        // Set up the back button to navigate up
        binding.btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigateUp();
        });

        // Set up the edit event button
        binding.btnEdit.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("eventId", eventId);
            Navigation.findNavController(v).navigate(R.id.action_navigation_event_view_info_to_edit_event, bundle);
        });

        return root;
    }

    /**
     * Fetches the details for an event from Firebase using its ID.
     */
    private void fetchEventDetails() {
        // Use addSnapshotListener to continuously listen for changes to the event data.
        eventSnapshotListenerReg = dbHandler.getEventsPath().document(eventId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null && value != null && value.exists()) {
                    Event event = value.toObject(Event.class);

                    if (event != null) {
                        // Set button state and on click listener
                        eventAdapter.updateJoinButtonState(binding.btnJoin, event, userId, dbHandler);

                        // Once data is loaded or updated, populate the screen
                        populateUI(event);
                    }
                }
            }
        });
    }

    /**
     * Populates the UI elements with data from the fetched Event object.
     */
    private void populateUI(Event event) {
        Glide.with(this).load(Base64.getDecoder().decode(event.getImageb64())).into(binding.imgEvent);
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
        binding.textDaySelect.setText(String.format("%s - %s", startTime.format(formatter), endTime.toLocalTime().toString()));

        // Set Information Fields
        binding.valueEventTitle.setText(eventDetails.getName());
        binding.valueEventDescription.setText(eventDetails.getDescription());

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
        binding.valueAttendeeLimit.setText(String.format(Locale.getDefault(), "%d / %d", currentAttendees, event.getParticipantList().getMax()));

        // Calculate and display Waitlist Count
        int currentWaitlist = 0;
        int maxWaitlist = 0;
        if (event.getWaitingList() != null) {
            if (event.getWaitingList().getTicketIds() != null) {
                currentWaitlist = event.getWaitingList().getTicketIds().size();
            }
            maxWaitlist = event.getWaitingList().getMax();
        }

        if (maxWaitlist == Integer.MAX_VALUE) {
            binding.valueWaitlistLimit.setText(String.format(Locale.getDefault(), "%d / ∞", currentWaitlist));
        } else {
            binding.valueWaitlistLimit.setText(String.format(Locale.getDefault(), "%d / %d", currentWaitlist, maxWaitlist));
        }

        if (!event.isJoinable()) {
            binding.btnJoin.setVisibility(View.GONE);
        }

        // Setup draw button
        binding.btnDraw.setOnClickListener(v -> {
            DrawParticipantsDialogFragment dpdf = DrawParticipantsDialogFragment.newInstance("drawParticipants", event.getWaitingList().size(), event.getParticipantList().size(), event.getParticipantList().getMax());
            dpdf.show(getChildFragmentManager(), "drawParticipants");
        });

        getChildFragmentManager().setFragmentResultListener("drawParticipants", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                int drawAmount = result.getInt("drawAmount");
                Log.v("EventViewInfoFragment", String.format("Drawing %d", drawAmount));

                event.draw(drawAmount);
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void getLocation(SingleListenUpdate<CustomLocation> callback) {
        if (checkPermissions()) {
            CancellationTokenSource cts = new CancellationTokenSource();
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.getToken()).addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        callback.onUpdate(new CustomLocation(location.getLatitude(), location.getLongitude()));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (eventSnapshotListenerReg != null) {
            eventSnapshotListenerReg.remove();
        }
    }
}
