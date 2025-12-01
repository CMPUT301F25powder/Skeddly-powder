package com.example.skeddly.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.example.skeddly.MainActivity;
import com.example.skeddly.R;
import com.example.skeddly.business.Ticket;
import com.example.skeddly.business.TicketStatus;
import com.example.skeddly.business.database.repository.NotificationRepository;
import com.example.skeddly.business.database.repository.TicketRepository;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.database.SingleListenUpdate;
import com.example.skeddly.business.location.CustomLocation;
import com.example.skeddly.business.location.MapPopupType;
import com.example.skeddly.business.notification.Notification;
import com.example.skeddly.business.notification.NotificationType;
import com.example.skeddly.business.user.User;
import com.example.skeddly.databinding.FragmentParticipantListBinding;
import com.example.skeddly.ui.adapter.ParticipantAdapter;
import com.example.skeddly.ui.popup.MapPopupDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.example.skeddly.ui.popup.SendMessageDialogFragment;
import com.example.skeddly.ui.popup.StandardPopupDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.opencsv.CSVWriter;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Fragment for the participant list screen
 */
public class ParticipantListFragment extends Fragment implements ParticipantAdapter.OnMessageButtonClickListener {

    private FragmentParticipantListBinding binding;
    private Event event;
    private DatabaseHandler dbhandler;

    private ArrayList<Ticket> waitingListTickets;
    private ArrayList<Ticket> finalListTickets;
    private ParticipantAdapter waitingParticipantAdapter;
    private ParticipantAdapter finalParticipantAdapter;
    private Boolean isWaitingList = true;

    private ListenerRegistration listener;
    private ActivityResultLauncher<Intent> filePickerActivityResultLauncher;
    private TicketRepository ticketRepository;
    private NotificationRepository notificationRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentParticipantListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        dbhandler = new DatabaseHandler();
        waitingListTickets = new ArrayList<>();
        finalListTickets = new ArrayList<>();
        listener = null;

        if (getArguments() != null) {
            String eventId = getArguments().getString("eventId");
            ticketRepository = new TicketRepository(FirebaseFirestore.getInstance(), eventId);
            notificationRepository = new NotificationRepository(FirebaseFirestore.getInstance());
            loadEventAndSetupUI(eventId);
        }

        // Hide map and messaging for now
        binding.fabMessage.setVisibility(View.GONE);
        binding.fabShowLocations.setVisibility(View.GONE);

        // Set up back button
        binding.btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // Set up CSV export button
        binding.btnExportCsv.setOnClickListener(v -> {
            createFile();
        });

        // Set up map button
        binding.fabShowLocations.setOnClickListener(v -> {
            if (isWaitingList) {
                fetchAndDisplayTicketLocations(waitingListTickets);
            } else {
                fetchAndDisplayTicketLocations(finalListTickets);
            }
        });

        // Set up message all button
        binding.fabMessage.setOnClickListener(v -> {
            String[] statusArr = null;

            if (!isWaitingList) {
                // Get the valid ticket statuses that we can message
                ArrayList<String> statuses = new ArrayList<>();
                ArrayList<Ticket> tickets = isWaitingList ? waitingListTickets : finalListTickets;

                for (Ticket ticket : tickets) {
                    if (!statuses.contains(ticket.getStatus().toString())) {
                        statuses.add(ticket.getStatus().toString());
                    }
                }

                statusArr = new String[statuses.size()];
                statuses.toArray(statusArr);
            }

            String listString = isWaitingList ? getString(R.string.dialog_msg_send_all_waiting_list) : getString(R.string.dialog_msg_send_all_final_list);
            StandardPopupDialogFragment spdf = StandardPopupDialogFragment.newInstance(
                    getString(R.string.dialog_msg_send_all_title),
                    getString(R.string.dialog_msg_send_all_contents, listString),
                    "messageAll", true, statusArr);
            spdf.show(getChildFragmentManager(), null);
        });

        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        filePickerActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            if (result.getData() != null) {
                                Uri uri = result.getData().getData();
                                List<String[]> entrantData = getEntrantData();
                                alterDocument(uri, entrantData);
                                Toast.makeText(requireContext(), getString(R.string.toast_file_csv_exported), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        getChildFragmentManager().setFragmentResultListener("sendMessage", this, (requestKey, bundle) -> {
            String message = bundle.getString("message");
            String recipientId = bundle.getString("recipientId");
            Notification notification = new Notification(event.getEventDetails().getName(), message, recipientId, NotificationType.MESSAGES);
            notification.setType(NotificationType.MESSAGES);
            notificationRepository.set(notification);
        });

        getChildFragmentManager().setFragmentResultListener("messageAll", this, (requestKey, result) -> {
            boolean buttonChoice = result.getBoolean("buttonChoice");
            String typedText = result.getString("typedText");
            String statusSelection = result.getString("spinnerSelection");

            if (buttonChoice) {
                ArrayList<Ticket> tickets = isWaitingList ? waitingListTickets : finalListTickets;

                for (Ticket ticket : tickets) {
                    // If they selected a specific ticket type, don't send to other ones
                    if (statusSelection != null && !ticket.getStatus().toString().equals(statusSelection)) {
                        continue;
                    }

                    Notification notification = new Notification(event.getEventDetails().getName(), typedText, ticket.getUserId(), NotificationType.MESSAGES);
                    notificationRepository.set(notification);
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        if (listener != null) {
            listener.remove();
            listener = null;
        }

    }

    /**
     * Loads the core event, sets up the UI, and populates the default list.
     * @param eventId The ID of the event to load.
     */
    private void loadEventAndSetupUI(String eventId) {
        listener = dbhandler.singleListen(dbhandler.getEventsPath().document(eventId),
                Event.class,
                (SingleListenUpdate<Event>) receivedEvent -> {
                    if (receivedEvent == null) {
                        return;
                    }
                    // Set event
                    this.event = receivedEvent;
                    User currentUser = ((MainActivity) requireActivity()).getUser();

                    // Create the adapter with an empty list
                    waitingParticipantAdapter = new ParticipantAdapter(getContext(), waitingListTickets, dbhandler, event, currentUser, this);
                    finalParticipantAdapter = new ParticipantAdapter(getContext(), finalListTickets, dbhandler, event, currentUser, this);
                    binding.listViewEntrants.setAdapter(waitingParticipantAdapter);

                    // Set the button listeners to clear the adapter and fetch the correct data.
                    binding.buttonFinalList.setOnClickListener(v -> {
                        binding.buttonFinalList.setBackgroundResource(R.drawable.btn_select);
                        binding.buttonWaitingList.setBackgroundResource(R.drawable.btn_unselect);

                        binding.listViewEntrants.setAdapter(finalParticipantAdapter);

                        isWaitingList = false;
                        updateFabVisiblity();
                    });
                    binding.buttonWaitingList.setOnClickListener(v -> {
                        binding.buttonWaitingList.setBackgroundResource(R.drawable.btn_select);
                        binding.buttonFinalList.setBackgroundResource(R.drawable.btn_unselect);

                        binding.listViewEntrants.setAdapter(waitingParticipantAdapter);

                        isWaitingList = true;
                        updateFabVisiblity();
                    });

                    // Load all the tickets
                    fetchAndDisplayTickets();
                }
        );
    }

    /**
     * Clears the adapter and then fetches and displays all tickets for the given list of IDs.
     */
    private void fetchAndDisplayTickets() {
        ticketRepository.getAllByStatus(TicketStatus.WAITING).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Ticket> tickets = task.getResult();
                waitingParticipantAdapter.addAll(tickets);
                waitingParticipantAdapter.notifyDataSetChanged();
                updateFabVisiblity();
            } else {
                if (task.getException() != null) {
                    Log.e("ParticipantListFragment", task.getException().toString());
                }
            }
        });

        TicketStatus[] nonWaiting = {TicketStatus.INVITED, TicketStatus.ACCEPTED, TicketStatus.CANCELLED};
        ticketRepository.getAllByStatuses(Arrays.asList(nonWaiting)).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Ticket> tickets = task.getResult();
                finalParticipantAdapter.addAll(tickets);
                finalParticipantAdapter.notifyDataSetChanged();
                updateFabVisiblity();
            } else {
                if (task.getException() != null) {
                    Log.e("ParticipantListFragment", task.getException().toString());
                }
            }
        });
    }

    /**
     * Updates whether the FABs are visible on screen.
     */
    private void updateFabVisiblity() {
        List<Ticket> tickets = isWaitingList ? waitingListTickets : finalListTickets;

        if (tickets.isEmpty()) {
            binding.fabMessage.setVisibility(View.GONE);
            binding.fabShowLocations.setVisibility(View.GONE);
            return;
        }

        // Not empty
        binding.fabMessage.setVisibility(View.VISIBLE);
        binding.fabShowLocations.setVisibility(View.GONE);

        for (Ticket ticket : tickets) {
            if (ticket.getLocation() != null) {
                binding.fabShowLocations.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    /**
     * Fetches and displays all ticket locations based on which arraylist of tickets is provided.
     * @param tickets The arraylist of tickets.
     */
    private void fetchAndDisplayTicketLocations(ArrayList<Ticket> tickets) {
        ArrayList<CustomLocation> entrantLocations = new ArrayList<>();

        for (Ticket entrantTicket : tickets) {
            CustomLocation ticketLocation = entrantTicket.getLocation();
            if (ticketLocation != null) {
                entrantLocations.add(new CustomLocation(ticketLocation.getLatitude(), ticketLocation.getLongitude(), entrantTicket.getUserPersonalInfo().getName()));
            }
        }

        MapPopupDialogFragment lpf = MapPopupDialogFragment.newInstance("locationPicker", MapPopupType.VIEW, entrantLocations);
        lpf.show(getChildFragmentManager(), "LocationPicker");
    }

    /**
     * Let the user pick a location to create the new csv file at
     */
    private void createFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, "entrant_data.csv");

        filePickerActivityResultLauncher.launch(intent);
    }

    /**
     * Retrieves and prepares the entrant (ticket) data for CSV export. Combines both waitingListTickets and finalListTickets into one CSV.
     * @return A List<String[]> containing the header row and ticket data rows.
     */
    private List<String[]> getEntrantData() {
        List<String[]> data = new ArrayList<>();

        // Header
        data.add(new String[]{"Name", "Email", "Phone", "UserID", "EventID", "Time",
                "Latitude", "Longitude", "Status"});

        // The actual data (rows)
        List<Ticket> combinedTicketList = Stream
                .concat(waitingListTickets.stream(), finalListTickets.stream())
                .collect(Collectors.toList());

        for (Ticket ticket : combinedTicketList) {
            String name = ticket.getUserPersonalInfo().getName();
            String email = ticket.getUserPersonalInfo().getEmail();
            String phone = ticket.getUserPersonalInfo().getPhoneNumber();
            String userID = ticket.getUserId();
            String eventID = ticket.getEventId();

            // Ticket time
            long rawTicketTime = ticket.getTicketTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
            String ticketTime = Instant.ofEpochSecond(rawTicketTime)
                    .atZone(ZoneId.systemDefault())
                    .format(formatter);

            // Location
            String latitude = "";
            String longitude = "";
            if (ticket.getLocation() != null) {
                latitude = String.valueOf(ticket.getLocation().getLatitude());
                longitude = String.valueOf(ticket.getLocation().getLongitude());
            }

            String status = ticket.getStatus().toString();

            data.add(new String[]{name, email, phone, userID, eventID, ticketTime, latitude,
                    longitude, status});
        }

        return data;
    }

    /**
     * Export a list of rows containing data to the given URI as a csv file.
     * @param uri The uri of the document to alter
     * @param data The list of string arrays to write to it
     */
    private void alterDocument(Uri uri, List<String[]> data) {
        try {
            ParcelFileDescriptor pfd = requireActivity().getContentResolver().openFileDescriptor(uri, "w");
            FileWriter fileWriter = new FileWriter(pfd.getFileDescriptor());
            CSVWriter csvWriter = new CSVWriter(fileWriter);
            csvWriter.writeAll(data);

            // Let the document provider know you're done by closing the stream.
            csvWriter.close();
            pfd.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageButtonClick(String recipientId) {
        SendMessageDialogFragment.newInstance(recipientId).show(getChildFragmentManager(), "SendMessage");
    }
}
