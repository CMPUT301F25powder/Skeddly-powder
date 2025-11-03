package com.example.skeddly.ui.adapter;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.skeddly.R;
import com.example.skeddly.business.Event;
import com.example.skeddly.business.Ticket;
import com.example.skeddly.business.WaitingList;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.location.CustomLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class EventAdapter extends ArrayAdapter<Event> {

    public EventAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_event_item, parent, false);
        }

        // Get the event at the current position
        Event event = getItem(position);

        // Find the views in the layout
        TextView textEventName = convertView.findViewById(R.id.text_event_name);
        Button buttonViewInfo = convertView.findViewById(R.id.button_view_info);
        Button buttonJoin = convertView.findViewById(R.id.button_join);
        Button buttonEdit = convertView.findViewById(R.id.button_edit);

        // Populate data
        if (event != null) {
            textEventName.setText(event.getName());
            String current_user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            DatabaseHandler dbHandler = new DatabaseHandler(getContext());

            // Handle privilege assignment for editing
            if (current_user_id.equals(event.getOrganizer())) {
                buttonEdit.setVisibility(View.VISIBLE);
            } else {
                buttonEdit.setVisibility(View.GONE);
            }

            // Set button to join or leave depending on if user is on wait list of an event
            isJoinedEvent(event, current_user_id, dbHandler, this::onResult, buttonJoin);

            // Handle button clicks
            buttonViewInfo.setOnClickListener(v -> {
                // Handle view info button click
                Bundle bundle = new Bundle();
                bundle.putString("eventId", event.getId());

                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_navigation_home_to_event_view_info, bundle);

                Toast.makeText(getContext(), "View info for " + event.getName(), Toast.LENGTH_SHORT).show();
            });

            buttonJoin.setOnClickListener(v -> {
                // Handle join button click
                if (buttonJoin.getText().equals("Join")) {
                    joinEvent(event, current_user_id, null, dbHandler);
                    Toast.makeText(getContext(), "Joining " + event.getName(), Toast.LENGTH_SHORT).show();
                }
                else {
                    isJoinedEvent(event, current_user_id, dbHandler, this::leaveEvent, buttonJoin);
                    Toast.makeText(getContext(), "Leaving " + event.getName(), Toast.LENGTH_SHORT).show();
                }
            });

            buttonEdit.setOnClickListener(v -> {
                // Handle edit button click
                // TODO: Implement navigation to event edit screen
                Toast.makeText(getContext(), "Editing " + event.getName(), Toast.LENGTH_SHORT).show();
            });
        }

        return convertView;

    }

    interface Callback {
        void onResult(boolean result, Button buttonJoin, String ticketId, Event event, DatabaseHandler dbHandler);
    }
    private void onResult(boolean result, Button buttonJoin, String ticketId, Event event, DatabaseHandler dbHandler) {
        if (result) {
            buttonJoin.setText("Leave");
        }
        else {
            buttonJoin.setText("Join");
        }
    }

    private void leaveEvent(boolean result, Button buttonJoin, String ticketId, Event event, DatabaseHandler dbHandler) {
        if (result) {
            event.getApplicants().remove(ticketId);
            dbHandler.getEventsPath().child(event.getId()).child("applicants").setValue(event.getApplicants());
            dbHandler.getTicketsPath().child(ticketId).removeValue();
            buttonJoin.setText("Join");
        }
    }

    private void joinEvent(Event event, String userId, CustomLocation location, DatabaseHandler dbHandler) {
        // Ensure applicants object and list exist to prevent NullPointerException
        if (event.getApplicants() == null) {
            event.setApplications(new WaitingList());
        }
        if (event.getApplicants().getTicketIds() == null) {
            event.getApplicants().setTicketIds(new ArrayList<>());
        }

        Ticket ticket = new Ticket(userId, location);
        // Add the ticket to the event's applicants
        event.getApplicants().addTicket(ticket.getId());
        dbHandler.getEventsPath().child(event.getId()).child("applicants").setValue(event.getApplicants());

        // Save ticket to DB
        dbHandler.getTicketsPath().child(ticket.getId()).setValue(ticket);
    }

    private void isJoinedEvent(Event event, String userId, DatabaseHandler dbHandler, Callback callback, Button buttonJoin) {
        ArrayList<String> ticketIds = event.getApplicants().getTicketIds();
        for (String ticketId : ticketIds) {
            dbHandler.getTicketsPath().child(ticketId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Ticket ticket = snapshot.getValue(Ticket.class);
                    if (ticket != null && ticket.getUser().equals(userId)) {
                        callback.onResult(true, buttonJoin, ticketId, event, dbHandler);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    throw error.toException();
                }
            });
        }
        callback.onResult(false, buttonJoin, null, null, null);
    }
}