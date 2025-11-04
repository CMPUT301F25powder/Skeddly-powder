package com.example.skeddly.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.skeddly.business.database.DatabaseHandler;
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

            // Set button state, and button's on click listener
            updateJoinButtonState(buttonJoin, event, current_user_id, dbHandler);

            // Handle view info button click
            buttonViewInfo.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("eventId", event.getId());
                bundle.putString("userId", current_user_id);
                Navigation.findNavController(v).navigate(R.id.action_navigation_home_to_event_view_info, bundle);
                Toast.makeText(getContext(), "View info for " + event.getName(), Toast.LENGTH_SHORT).show();
            });

            // Handle edit button click
            buttonEdit.setOnClickListener(v -> {
                // TODO: Implement navigation to event edit screen
                Toast.makeText(getContext(), "Editing " + event.getName(), Toast.LENGTH_SHORT).show();
            });
        }

        return convertView;
    }

    // Encapsulates the logic for setting the join/leave button
    public void updateJoinButtonState(Button buttonJoin, Event event, String userId, DatabaseHandler dbHandler) {
        buttonJoin.setEnabled(false); // Disable button while we check
        event.findUserTicketId(userId, dbHandler, ticketId -> {
            // This code runs when the check is complete
            if (ticketId != null) {
                // User is on the waitlist
                buttonJoin.setText("Leave");
                buttonJoin.setOnClickListener(v -> {
                    event.leave(dbHandler, ticketId);
                    Toast.makeText(getContext(), "Leaving " + event.getName(), Toast.LENGTH_SHORT).show();
                });
            } else {
                // User is not on the waitlist
                buttonJoin.setText("Join");
                buttonJoin.setOnClickListener(v -> {
                    event.join(dbHandler, userId);
                    Toast.makeText(getContext(), "Joining " + event.getName(), Toast.LENGTH_SHORT).show();
                });
            }
            buttonJoin.setEnabled(true); // Re-enable the button
        });
    }

    // Callback interface, needed to pass callback function to findUserTicketId
    interface FindTicketCallback {
        void onResult(String ticketId);
    }

}
