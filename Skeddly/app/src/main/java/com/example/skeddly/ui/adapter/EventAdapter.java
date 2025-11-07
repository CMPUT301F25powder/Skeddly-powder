package com.example.skeddly.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.skeddly.R;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.Ticket;
import com.example.skeddly.business.database.DatabaseHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;

/**
 * Adapter for the event list view
 */
public class EventAdapter extends ArrayAdapter<Event> {
    private String userId;

    /**
     * Constructor for the EventAdapter
     * @param context The context of the app
     * @param events The events to display
     * @param userId The user ID of the current user
     */
    public EventAdapter(Context context, ArrayList<Event> events, String userId) {
        super(context, 0, events);
        this.userId = userId;
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
        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView textEventName = convertView.findViewById(R.id.text_event_name);
        Button buttonViewInfo = convertView.findViewById(R.id.button_view_info);
        Button buttonJoin = convertView.findViewById(R.id.button_join);
        Button buttonEdit = convertView.findViewById(R.id.button_edit);

        // Populate data
        if (event != null) {
            Glide.with(getContext()).load(Base64.getDecoder().decode(event.getImageb64())).into(imageView);
            textEventName.setText(event.getEventDetails().getName());
            DatabaseHandler dbHandler = new DatabaseHandler(getContext());

            // Handle privilege assignment for editing
            if (userId.equals(event.getOrganizer())) {
                buttonEdit.setVisibility(View.VISIBLE);
                buttonJoin.setVisibility(View.INVISIBLE);
            } else {
                buttonEdit.setVisibility(View.INVISIBLE);
            }

            // Set button state, and button's on click listener
            updateJoinButtonState(buttonJoin, event, userId, dbHandler);

            // Handle view info button click
            buttonViewInfo.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("eventId", event.getId());
                bundle.putString("userId", userId);
                bundle.putString("organizerId", event.getOrganizer());
                Navigation.findNavController(v).navigate(R.id.action_navigation_home_to_event_view_info, bundle);
                Toast.makeText(getContext(), "View info for " + event.getEventDetails().getName(), Toast.LENGTH_SHORT).show();
            });

            // Handle edit button click
            buttonEdit.setOnClickListener(v -> {
                // TODO: Implement navigation to event edit screen
                Toast.makeText(getContext(), "Editing " + event.getEventDetails().getName(), Toast.LENGTH_SHORT).show();
            });
        }

        return convertView;
    }

    /**
     * Updates the state of the join button based on the user's ticket status.
     * @param buttonJoin The button to update
     * @param event The event to check
     * @param userId The user ID
     * @param dbHandler The database handler
     */
    public void updateJoinButtonState(Button buttonJoin, Event event, String userId, DatabaseHandler dbHandler) {
        buttonJoin.setEnabled(false); // Disable button while we check
        event.findUserTicketId(userId, dbHandler, ticketId -> {
            // This code runs when the check is complete
            if (ticketId != null) {
                // User is on the waitlist
                buttonJoin.setText("Leave");
                buttonJoin.setOnClickListener(v -> {
                    event.leave(dbHandler, ticketId);
                    Toast.makeText(getContext(), "Leaving " + event.getEventDetails().getName(), Toast.LENGTH_SHORT).show();
                });
            } else {
                // User is not on the waitlist
                buttonJoin.setText("Join");
                buttonJoin.setOnClickListener(v -> {
                    event.join(dbHandler, userId);
                    Toast.makeText(getContext(), "Joining " + event.getEventDetails().getName(), Toast.LENGTH_SHORT).show();
                });
            }
            buttonJoin.setEnabled(true); // Re-enable the button
        });
    }

}
