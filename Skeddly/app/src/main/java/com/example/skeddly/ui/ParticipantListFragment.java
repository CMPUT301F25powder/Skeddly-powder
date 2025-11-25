package com.example.skeddly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skeddly.R;
import com.example.skeddly.business.Ticket;
import com.example.skeddly.business.TicketStatus;
import com.example.skeddly.business.database.repository.TicketRepository;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.database.SingleListenUpdate;
import com.example.skeddly.business.location.CustomLocation;
import com.example.skeddly.business.location.MapPopupType;
import com.example.skeddly.databinding.FragmentParticipantListBinding;
import com.example.skeddly.ui.adapter.ParticipantAdapter;
import com.example.skeddly.ui.popup.MapPopupDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Fragment for the participant list screen
 */
public class ParticipantListFragment extends Fragment {

    private FragmentParticipantListBinding binding;
    private Event event;
    private DatabaseHandler dbhandler;

    private ArrayList<Ticket> waitingListTickets;
    private ArrayList<Ticket> finalListTickets;
    private ParticipantAdapter waitingParticipantAdapter;
    private ParticipantAdapter finalParticipantAdapter;
    private Boolean isWaitingList = true;

    private ListenerRegistration listener;
    private TicketRepository ticketRepository;

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
            loadEventAndSetupUI(eventId);
        }

        // Set up back button
        binding.btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // Set up map button
        binding.fabShowLocations.setOnClickListener(v -> {
            if (isWaitingList) {
                fetchAndDisplayTicketLocations(waitingListTickets);
            } else {
                fetchAndDisplayTicketLocations(finalListTickets);
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

                    // Create the adapter with an empty list
                    waitingParticipantAdapter = new ParticipantAdapter(getContext(), waitingListTickets, dbhandler, event);
                    finalParticipantAdapter = new ParticipantAdapter(getContext(), finalListTickets, dbhandler, event);
                    binding.listViewEntrants.setAdapter(waitingParticipantAdapter);

                    // Set the button listeners to clear the adapter and fetch the correct data.
                    binding.buttonFinalList.setOnClickListener(v -> {
                        binding.buttonFinalList.setBackgroundResource(R.drawable.btn_select);
                        binding.buttonWaitingList.setBackgroundResource(R.drawable.btn_unselect);

                        binding.listViewEntrants.setAdapter(finalParticipantAdapter);

                        isWaitingList = false;
                    });
                    binding.buttonWaitingList.setOnClickListener(v -> {
                        binding.buttonWaitingList.setBackgroundResource(R.drawable.btn_select);
                        binding.buttonFinalList.setBackgroundResource(R.drawable.btn_unselect);

                        binding.listViewEntrants.setAdapter(waitingParticipantAdapter);

                        isWaitingList = true;
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
        ticketRepository.getAllByStatus(TicketStatus.WAITING).addOnSuccessListener(tickets -> {
            waitingParticipantAdapter.addAll(tickets);
            waitingParticipantAdapter.notifyDataSetChanged();
        });

        TicketStatus[] nonWaiting = {TicketStatus.INVITED, TicketStatus.ACCEPTED, TicketStatus.CANCELLED};
        ticketRepository.getAllByStatuses(Arrays.asList(nonWaiting)).addOnSuccessListener(tickets -> {
            finalParticipantAdapter.addAll(tickets);
            finalParticipantAdapter.notifyDataSetChanged();
        });
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
}
