package com.example.skeddly.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.skeddly.R;
import com.example.skeddly.business.Ticket;
import com.example.skeddly.business.TicketStatus;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.user.User;
import com.example.skeddly.business.user.UserLevel;
import com.example.skeddly.databinding.ItemEntrantBinding;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Adapter for the participant list view
 */
public class ParticipantAdapter extends ArrayAdapter<Ticket> {
    private DatabaseHandler dbHandler;
    private Event event;
    private User currentUser;
    private OnMessageButtonClickListener messageButtonClickListener;

    public interface OnMessageButtonClickListener {
        void onMessageButtonClick(String recipientId);
    }

    /**
     * Constructor for the ParticipantAdapter
     * @param context The context of the app
     * @param tickets The tickets to display
     * @param dbHandler The database handler
     * @param event The event to get the users from
     */
    public ParticipantAdapter(Context context, ArrayList<Ticket> tickets, DatabaseHandler dbHandler, Event event, User currentUser, OnMessageButtonClickListener listener) {
        super(context, 0, tickets);
        this.dbHandler = dbHandler;
        this.event = event;
        this.currentUser = currentUser;
        this.messageButtonClickListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_entrant, parent, false);
        }
        ItemEntrantBinding binding = ItemEntrantBinding.bind(convertView);

        // Get components
        TextView nameText = binding.textViewFullName;
        TextView dateText = binding.textViewJoinDate;
        TextView statusTextView = binding.textViewStatus;
        Button messageButton = binding.buttonMessageUser;

        Ticket ticket = getItem(position);
        if (ticket != null) {
            // Set user name
            nameText.setText(ticket.getUserPersonalInfo().getName());

            // Set date
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime joinDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(ticket.getTicketTime()), ZoneId.systemDefault());
            dateText.setText("Joined on " + dateTimeFormatter.format(joinDate));

            // Set status
            statusTextView.setVisibility(View.VISIBLE);
            if (ticket.getStatus() == TicketStatus.INVITED) {
                statusTextView.setBackgroundResource(R.drawable.chip_status_invited);
            }
            else if (ticket.getStatus() == TicketStatus.ACCEPTED){
                statusTextView.setBackgroundResource(R.drawable.chip_status_accepted);
            }
            else if (ticket.getStatus() == TicketStatus.CANCELLED) {
                statusTextView.setBackgroundResource(R.drawable.chip_status_cancelled);
            } else {
                statusTextView.setVisibility(View.INVISIBLE);
            }

            // Show message button for admin or organizer
            boolean isOrganizer = event.getOrganizer().equals(currentUser.getId());
            if (currentUser.getPrivilegeLevel() == UserLevel.ADMIN || isOrganizer) {
                messageButton.setVisibility(View.VISIBLE);
                messageButton.setOnClickListener(v -> {
                    if (messageButtonClickListener != null) {
                        messageButtonClickListener.onMessageButtonClick(ticket.getUserId());
                    }
                });
            } else {
                messageButton.setVisibility(View.GONE);
            }

            // Delete from either list when long pressed
            convertView.setOnLongClickListener(v -> {
                if (dbHandler != null) {
                    event.leave(dbHandler, ticket.getId());
                    remove(ticket);
                    notifyDataSetChanged();
                }
                return true;
            });

        }
        return convertView;
    }
}
