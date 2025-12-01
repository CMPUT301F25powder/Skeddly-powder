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
import com.example.skeddly.business.TicketStatus;
import com.example.skeddly.business.database.SingleListenUpdate;
import com.example.skeddly.business.database.repository.TicketRepository;
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
import com.example.skeddly.ui.utility.LocationFetcherFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.Format;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

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
    private int currentAttendees;

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

        // Set up QR code button
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

        // Location
        binding.textEventLocationOverlay.setText(String.format(event.getLocation().toString()));

        // Set Event Time
        LocalDateTime startTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(eventSchedule.getStartTime()), ZoneId.systemDefault());
        LocalDateTime endTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(eventSchedule.getEndTime()), ZoneId.systemDefault());

        String formattedTime;
        if (eventSchedule.isRecurring()) {
            // For recurring events
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            String timePart = String.format("%s - %s", startTime.format(timeFormatter), endTime.format(timeFormatter));

            // Build the days of the week string
            StringBuilder daysPart = getDaysPart(eventSchedule);

            // Format the date range part
            DateTimeFormatter dateFormatterRange = DateTimeFormatter.ofPattern("MMM d");
            String datePart = String.format("from %s to %s",
                    startTime.format(dateFormatterRange),
                    endTime.format(dateFormatterRange));

            // Combine all parts into the final string
            formattedTime = String.format("Recurring every %s at %s, %s", daysPart, timePart, datePart);

        } else {
            // For single events
            DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            formattedTime = String.format("%s, %s - %s",
                    startTime.format(dayFormatter),
                    startTime.format(timeFormatter),
                    endTime.format(timeFormatter));
        }
        binding.textDaySelect.setText(formattedTime);

        // Set Information Fields
        binding.valueEventTitle.setText(eventDetails.getName());
        binding.valueEventDescription.setText(eventDetails.getDescription());

        if (eventDetails.getCategories() != null) {
            binding.valueCategory.setText(String.join(", ", eventDetails.getCategories()));
        } else {
            binding.valueCategory.setText("N/A");
        }

        // Calculate and display Attendee Count

        TicketRepository ticketRepository = new TicketRepository(FirebaseFirestore.getInstance(), event.getId());
        List<TicketStatus> statuses = Arrays.asList(TicketStatus.ACCEPTED, TicketStatus.INVITED);
        ticketRepository.getAllByStatuses(statuses).addOnSuccessListener(tickets -> {
            currentAttendees = tickets.size();
            binding.valueAttendeeLimit.setText(String.format(Locale.getDefault(), "%d / %d", currentAttendees, event.getParticipantList().getMax()));
        });

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
            DrawParticipantsDialogFragment dpdf = DrawParticipantsDialogFragment.newInstance("drawParticipants", event.getWaitingList().size(), currentAttendees, event.getParticipantList().getMax());
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

        LocalDateTime regEndTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(eventSchedule.getRegEnd()), ZoneId.systemDefault());
        DateTimeFormatter regFormatter = DateTimeFormatter.ofPattern("MMM d, h:mm a", Locale.ENGLISH);
        String formattedRegTime = regEndTime.format(regFormatter);

        binding.valueRegistrationEnd.setText(String.format("%s",formattedRegTime));


    }

    @NonNull
    private static StringBuilder getDaysPart(EventSchedule eventSchedule) {
        List<Boolean> days = eventSchedule.getDaysOfWeek();
        StringBuilder daysPart = new StringBuilder();
        if (days != null && days.contains(true)) {
            String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
            for (int i = 0; i < days.size(); i++) {
                if (days.get(i)) {
                    if (daysPart.length() > 0) {
                        daysPart.append(", ");
                    }
                    daysPart.append(dayNames[i]);
                }
            }
        }
        return daysPart;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (eventSnapshotListenerReg != null) {
            eventSnapshotListenerReg.remove();
        }
    }
}
