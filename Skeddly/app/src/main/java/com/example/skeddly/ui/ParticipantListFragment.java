package com.example.skeddly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.Ticket;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.database.DatabaseObject;
import com.example.skeddly.business.database.DatabaseObjects;
import com.example.skeddly.business.database.SingleListenUpdate;
import com.example.skeddly.databinding.EntrantListViewBinding;
import com.example.skeddly.ui.adapter.ParticipantAdapter;

import java.util.ArrayList;

public class ParticipantListFragment extends Fragment {

    private EntrantListViewBinding binding;
    private Event event;
    private DatabaseHandler dbhandler;
    private ArrayList<Ticket> finalTicketList;
    private ArrayList<Ticket> waitingTicketList;
    private ParticipantAdapter participantAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment using View Binding
        binding = EntrantListViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        finalTicketList = new ArrayList<>();
        waitingTicketList = new ArrayList<>();

        // Get Event
        if (getArguments() != null) {
            String eventId = getArguments().getString("eventId");
            dbhandler = new DatabaseHandler(getContext());
            getEventFromId(eventId);
        }

        // Set up adapter
        participantAdapter = new ParticipantAdapter(getContext(), waitingTicketList, false, dbhandler);
        binding.listViewEntrants.setAdapter(participantAdapter);

        // Adjust adapter based on button press
        binding.buttonFinalList.setOnClickListener(v -> {
            participantAdapter = new ParticipantAdapter(getContext(), finalTicketList, true, dbhandler);
        });
        binding.buttonWaitingList.setOnClickListener(v -> {
            participantAdapter = new ParticipantAdapter(getContext(), finalTicketList, true, dbhandler);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Set the binding to null to avoid memory leaks
        binding = null;
    }

    private void getEventFromId(String eventId) {
        // Fetch events from firebase
        dbhandler.singleListen(dbhandler.getEventsPath().child(eventId),
                Event.class,
                (SingleListenUpdate<Event>) newValue -> {
                    this.event = newValue;
                    this.event.setId(eventId);

                    // Get tickets for waiting list
                    for (String ticketId : event.getWaitingList().getTicketIds()) {
                        getTicketsFromId(ticketId, waitingTicketList);
                    }

                    // Get tickets for final list
                    for (String ticketId : event.getParticipantList().getTicketIds()) {
                        getTicketsFromId(ticketId, finalTicketList);
                    }
                }
        );
    }

    private void getTicketsFromId(String ticketId, ArrayList<Ticket> ticketsList) {
        dbhandler.singleListen(dbhandler.getTicketsPath().child(ticketId),
                Ticket.class,
                (SingleListenUpdate<Ticket>) ticket -> {
                    ticketsList.add(ticket);
                    ticket.setId(ticketId);
                });
    }

}
