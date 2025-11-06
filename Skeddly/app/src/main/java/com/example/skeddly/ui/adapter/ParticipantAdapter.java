package com.example.skeddly.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.skeddly.R;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.Ticket;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.database.SingleListenUpdate;
import com.example.skeddly.business.user.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ParticipantAdapter extends ArrayAdapter<Ticket> {
    private boolean isWaitingList;
    private DatabaseHandler dbHandler;
    private String fullname;
    private String joinDate;


    public ParticipantAdapter(Context context, ArrayList<Ticket> tickets, boolean isWaitingList, DatabaseHandler dbHandler) {
        super(context, 0, tickets);
        this.isWaitingList = isWaitingList;
        this.dbHandler = dbHandler;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_entrant_item, parent, false);
        }

        // Get components
        TextView nameText = convertView.findViewById(R.id.text_view_full_name);
        TextView dateText = convertView.findViewById(R.id.text_view_join_date);
        TextView statusText = convertView.findViewById(R.id.text_view_status);

        Ticket ticket = getItem(position);
        if (ticket != null) {
            // Set user name
            getUserFromId(ticket.getUser(), user -> {
                fullname = user.getPersonalInformation().getName();
                nameText.setText(fullname);
            });

            // Set date
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime joinDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(ticket.getTicketTime()), ZoneId.systemDefault());
            this.joinDate = "Joined on" + dateTimeFormatter.format(joinDate);
            dateText.setText(this.joinDate);

            // Set status
            boolean cancelled = ticket.getCancelled();
            if (cancelled) {
                statusText.setText("Cancelled");
                statusText.setBackgroundResource(R.drawable.status_chip_cancelled);
            }

            // remove status for the finalized list
            if (!isWaitingList) {
                statusText.setVisibility(View.INVISIBLE);
            }

        }
        return convertView;
    }


    private void getUserFromId(String userId, SingleListenUpdate<User> callback) {
        dbHandler.getUsersPath().child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callback.onUpdate(null);
                } else {
                    callback.onUpdate(snapshot.getValue(User.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });
    }



}
