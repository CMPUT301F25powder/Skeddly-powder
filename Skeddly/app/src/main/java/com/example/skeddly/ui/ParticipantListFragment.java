package com.example.skeddly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skeddly.R;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.Ticket;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.database.SingleListenUpdate;
import com.example.skeddly.databinding.FragmentParticipantListBinding;
import com.example.skeddly.ui.adapter.ParticipantAdapter;

import java.util.ArrayList;

/**
 * Fragment for the participant list screen
 */
public class ParticipantListFragment extends Fragment {

    private FragmentParticipantListBinding binding;
    private Event event;
    private DatabaseHandler dbhandler;
    private ArrayList<String> finalTicketIds;
    private ArrayList<String> waitingTicketIds;
    private ParticipantAdapter participantAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentParticipantListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // Initialize the ID lists
        finalTicketIds = new ArrayList<>();
        waitingTicketIds = new ArrayList<>();
        dbhandler = new DatabaseHandler();

        if (getArguments() != null) {
            String eventId = getArguments().getString("eventId");
            loadEventAndSetupUI(eventId);
        }

        // Set up back button
        binding.btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Loads the core event, sets up the UI, and populates the default list.
     * @param eventId The ID of the event to load.
     */
    private void loadEventAndSetupUI(String eventId) {
        dbhandler.singleListen(dbhandler.getEventsPath().document(eventId),
                Event.class,
                (SingleListenUpdate<Event>) receivedEvent -> {
                    if (receivedEvent == null) {
                        return;
                    }
                    // Set event
                    this.event = receivedEvent;

                    // Create the adapter with an empty list
                    participantAdapter = new ParticipantAdapter(getContext(), new ArrayList<>(), true, dbhandler, event);
                    binding.listViewEntrants.setAdapter(participantAdapter);

                    // Extract the ticket IDs from the event object
                    if (event.getWaitingList() != null && event.getWaitingList().getTicketIds() != null) {
                        waitingTicketIds.addAll(event.getWaitingList().getTicketIds());
                    }
                    if (event.getParticipantList() != null && event.getParticipantList().getTicketIds() != null) {
                        finalTicketIds.addAll(event.getParticipantList().getTicketIds());
                    }

                    // Set the button listeners to clear the adapter and fetch the correct data.
                    binding.buttonFinalList.setOnClickListener(v -> {
                        participantAdapter.setWaitingList(false);
                        binding.buttonFinalList.setBackgroundResource(R.drawable.btn_select);
                        binding.buttonWaitingList.setBackgroundResource(R.drawable.btn_unselect);
                        fetchAndDisplayTickets(finalTicketIds);
                    });
                    binding.buttonWaitingList.setOnClickListener(v -> {
                        participantAdapter.setWaitingList(true);
                        binding.buttonWaitingList.setBackgroundResource(R.drawable.btn_select);
                        binding.buttonFinalList.setBackgroundResource(R.drawable.btn_unselect);
                        fetchAndDisplayTickets(waitingTicketIds);
                    });

                    // Load the default list (waiting list)
                    fetchAndDisplayTickets(waitingTicketIds);
                }
        );
    }

    /**
     * Clears the adapter and then fetches and displays all tickets for the given list of IDs.
     * @param ticketIds The list of ticket IDs to fetch.
     */
    private void fetchAndDisplayTickets(ArrayList<String> ticketIds) {
        if (participantAdapter == null) return;

        // Clear the adapter to show a fresh list
        participantAdapter.clear();
        participantAdapter.notifyDataSetChanged(); // Show the empty state immediately

        if (ticketIds == null) return;

        // Loop through the IDs and fetch each ticket one by one.
        for (String ticketId : ticketIds) {
            dbhandler.singleListen(dbhandler.getTicketsPath().document(ticketId),
                    Ticket.class,
                    (SingleListenUpdate<Ticket>) ticket -> {
                        if (ticket != null) {
                            participantAdapter.add(ticket);
                            participantAdapter.notifyDataSetChanged(); // Refresh after each add
                        }
                    });
        }
    }
}
