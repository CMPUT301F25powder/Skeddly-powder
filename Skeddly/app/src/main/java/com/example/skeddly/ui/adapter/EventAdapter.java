package com.example.skeddly.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.skeddly.R;
import com.example.skeddly.business.Event;
import com.google.firebase.auth.FirebaseAuth;

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

            // Handle privilege assignment for editing
            if (Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().equals(event.getOrganizer())) {
                buttonEdit.setVisibility(View.VISIBLE);
            } else {
                buttonEdit.setVisibility(View.GONE);
            }

            // Handle button clicks
            buttonViewInfo.setOnClickListener(v -> {
                // Handle view info button click
                // TODO: Implement navigation to event details screen
                Toast.makeText(getContext(), "View info for " + event.getName(), Toast.LENGTH_SHORT).show();
            });

            buttonJoin.setOnClickListener(v -> {
                // Handle join button click
                // TODO: Implement join/leave logic using DatabaseHandler
                Toast.makeText(getContext(), "Joining " + event.getName(), Toast.LENGTH_SHORT).show();

            });

            buttonEdit.setOnClickListener(v -> {
                // Handle edit button click
                // TODO: Implement navigation to event edit screen
                Toast.makeText(getContext(), "Editing " + event.getName(), Toast.LENGTH_SHORT).show();
            });
        }

        return convertView;

    }
}
