package com.example.skeddly.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.skeddly.R;
import com.example.skeddly.business.Ticket;
import com.example.skeddly.business.event.Event;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

public class EventHistoryAdapter extends ArrayAdapter<Event> {

    private final Map<String, Ticket> ticketMap; // Map EventID -> Ticket

    /**
     * Constructor for the EventHistoryAdapter
     * @param context Context of the app
     * @param events List of events to display
     * @param ticketMap Map of ticket IDs to tickets
     */
    public EventHistoryAdapter(Context context, ArrayList<Event> events, Map<String, Ticket> ticketMap) {
        super(context, 0, events);
        this.ticketMap = ticketMap;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_event_history, parent, false);
        }

        Event event = getItem(position);

        ImageView imageView = convertView.findViewById(R.id.img_event_history);
        TextView textEventName = convertView.findViewById(R.id.text_event_name_history);
        TextView textDateJoined = convertView.findViewById(R.id.text_date_joined_history);
        TextView textEventDescription = convertView.findViewById(R.id.text_event_description_history);

        if (event != null) {
            // Set event name and image
            textEventName.setText(event.getEventDetails().getName());
            textEventDescription.setText(event.getEventDetails().getDescription());
            if (event.getImageb64() != null && !event.getImageb64().isEmpty()) {
                Glide.with(getContext()).load(Base64.getDecoder().decode(event.getImageb64())).into(imageView);
            }

            // Find the corresponding ticket to get the join date
            Ticket ticket = ticketMap.get(event.getId());
            if (ticket != null) {
                ZonedDateTime time = Instant.ofEpochSecond(ticket.getTicketTime()).atZone(ZoneId.systemDefault());
                textDateJoined.setText(getContext().getString(
                        R.string.fragment_profile_event_history_joined_date,
                        time.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
            } else {
                textDateJoined.setText(""); // Hide if no ticket found
            }
        }

        return convertView;
    }
}
