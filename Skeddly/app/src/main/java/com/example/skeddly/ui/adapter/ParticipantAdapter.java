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
import com.example.skeddly.business.TicketStatus;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.Ticket;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.database.SingleListenUpdate;
import com.example.skeddly.business.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Adapter for the participant list view
 */
public class ParticipantAdapter extends ArrayAdapter<Ticket> {
    private boolean isWaitingList;
    private DatabaseHandler dbHandler;
    private String fullname;
    private String joinDate;
    private Event event;

    /**
     * Constructor for the ParticipantAdapter
     * @param context The context of the app
     * @param tickets The tickets to display
     * @param isWaitingList Whether the tickets are for the waiting list
     * @param dbHandler The database handler
     * @param event The event to get the users from
     */
    public ParticipantAdapter(Context context, ArrayList<Ticket> tickets, boolean isWaitingList, DatabaseHandler dbHandler, Event event) {
        super(context, 0, tickets);
        this.isWaitingList = isWaitingList;
        this.dbHandler = dbHandler;
        this.event = event;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_entrant, parent, false);
        }

        // Get components
        TextView nameText = convertView.findViewById(R.id.text_view_full_name);
        TextView dateText = convertView.findViewById(R.id.text_view_join_date);
        TextView statusTextView = convertView.findViewById(R.id.text_view_status);
        Ticket ticket = getItem(position);
        if (ticket != null) {
            // Set user name
            getUserFromId(ticket.getUserId(), user -> {
                fullname = user.getPersonalInformation().getName();
                nameText.setText(fullname);
            });

            // Set date
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime joinDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(ticket.getTicketTime()), ZoneId.systemDefault());
            this.joinDate = "Joined on " + dateTimeFormatter.format(joinDate);
            dateText.setText(this.joinDate);

            // Set status
            if (ticket.getStatus() == TicketStatus.CANCELLED) {
                statusTextView.setBackgroundResource(R.drawable.chip_status_cancelled);
            }
            else if (ticket.getStatus() == TicketStatus.INVITED){
                statusTextView.setBackgroundResource(R.drawable.chip_status_invited);
            }
            else {
                statusTextView.setBackgroundResource(R.drawable.chip_status_accepted);
            }


            // remove status for the finalized list and long press to delete
            if (!isWaitingList) {
                statusTextView.setVisibility(View.VISIBLE);
            }
            else {
                statusTextView.setVisibility(View.INVISIBLE);
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

    /**
     * Gets the user from the database based on their ID
     * @param userId The ID of the user to get
     * @param callback The callback to run when the user is retrieved
     */
    private void getUserFromId(String userId, SingleListenUpdate<User> callback) {
        dbHandler.getUsersPath().document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    callback.onUpdate(task.getResult().toObject(User.class));
                } else {
                    callback.onUpdate(null);
                }
            }
        });
    }

    /**
     * Sets whether we are showing the waiting list or not.
     * @param isWaitingList True if we're showing the waiting list. False otherwise
     */
    public void setWaitingList(boolean isWaitingList) {
        this.isWaitingList = isWaitingList;
    }

    /**
     * Gets whether we are showing the waiting list or not.
     * @return True if we're showing the waiting list. False otherwise
     */
    public boolean getWaitingList() {
        return isWaitingList;
    }



}
