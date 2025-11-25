package com.example.skeddly.ui.adapter;

import android.content.Context;
import android.os.Bundle;
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
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.skeddly.R;
import com.example.skeddly.business.database.SingleListenUpdate;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.location.CustomLocation;
import com.example.skeddly.business.user.User;
import com.example.skeddly.ui.popup.StandardPopupDialogFragment;

import java.util.ArrayList;
import java.util.Base64;

/**
 * Adapter for the event list view
 */
public class EventAdapter extends ArrayAdapter<Event> {
    private User user;
    private final RetrieveLocation locationGetter;

    /**
     * Constructor for the EventAdapter
     * @param context The context of the app
     * @param events The events to display
     * @param user The current user
     */
    public EventAdapter(Context context, ArrayList<Event> events, User user, RetrieveLocation locationGetter) {
        super(context, 0, events);
        this.user = user;
        this.locationGetter = locationGetter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_event, parent, false);
        }

        // Get the event at the current position
        Event event = getItem(position);

        // Find the views in the layout
        ImageView imageView = convertView.findViewById(R.id.img_event);
        TextView textEventName = convertView.findViewById(R.id.text_event_name);
        Button buttonViewInfo = convertView.findViewById(R.id.btn_view_info);
        Button buttonJoin = convertView.findViewById(R.id.btn_join);
        Button buttonEdit = convertView.findViewById(R.id.btn_edit);

        // Populate data
        if (event != null) {
            Glide.with(getContext()).load(Base64.getDecoder().decode(event.getImageb64())).into(imageView);
            textEventName.setText(event.getEventDetails().getName());
            DatabaseHandler dbHandler = new DatabaseHandler();

            // Handle privilege assignment for editing
            if (user.getId().equals(event.getOrganizer())) {
                buttonEdit.setVisibility(View.VISIBLE);
                buttonJoin.setVisibility(View.INVISIBLE);
            } else {
                buttonEdit.setVisibility(View.INVISIBLE);

                if (!event.isJoinable()) {
                    buttonJoin.setVisibility(View.INVISIBLE);
                } else {
                    buttonJoin.setVisibility(View.VISIBLE);
                }
            }

            // Set button state, and button's on click listener
            updateJoinButtonState(buttonJoin, event, user.getId(), dbHandler);

            // Handle view info button click
            buttonViewInfo.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("eventId", event.getId());
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
                    // Show pop up to confirm join
                    showJoinConfirmationPopup(event, dbHandler);
                });
            }
            buttonJoin.setEnabled(true); // Re-enable the button
        });
    }

    /**
     * Shows a confirmation popup before joining an event.
     * @param event The event to join.
     * @param dbHandler The database handler.
     */
    private void showJoinConfirmationPopup(Event event, DatabaseHandler dbHandler) {
        if (getContext() instanceof FragmentActivity) {
            FragmentManager fm = ((FragmentActivity) getContext()).getSupportFragmentManager();
            String requestKey = "joinConfirm-" + event.getId();

            fm.setFragmentResultListener(requestKey, (LifecycleOwner) getContext(), (reqKey, bundle) -> {
                boolean result = bundle.getBoolean("buttonChoice");
                if (result) {
                    Toast.makeText(getContext(), "Joining " + event.getEventDetails().getName(), Toast.LENGTH_SHORT).show();

                    if (event.getLogLocation()) {
                        locationGetter.getLocation(new SingleListenUpdate<CustomLocation>() {
                            @Override
                            public void onUpdate(CustomLocation newValue) {
                                if (newValue == null) {
                                    Toast.makeText(getContext(), "Location lookup failed!", Toast.LENGTH_SHORT).show();
                                }
                                event.join(dbHandler, user.getPersonalInformation(), user.getId(), newValue);
                            }
                        });
                    } else {
                        event.join(dbHandler, user.getPersonalInformation(), user.getId(), null);
                    }
                }
            });

            StandardPopupDialogFragment.newInstance("Entry Criteria", event.getEventDetails().getEntryCriteria(), requestKey).show(fm, "dialog_join_confirm");
        }
    }
}
