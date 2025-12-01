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
import com.example.skeddly.business.database.repository.EventRepository;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.location.CustomLocation;
import com.example.skeddly.business.user.User;
import com.example.skeddly.business.user.UserLevel;
import com.example.skeddly.ui.popup.StandardPopupDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Base64;

/**
 * Adapter for the event list view
 */
public class EventAdapter extends ArrayAdapter<Event> {
    private User user;
    private final RetrieveLocation locationGetter;
    private final int viewInfoActionId;
    private final int editActionId;

    /**
     * Constructor for the EventAdapter
     * @param context The context of the app
     * @param events The events to display
     * @param user The current user
     * @param locationGetter A callback for retrieving the user's location
     * @param viewInfoActionId The navigation action ID for viewing event info
     * @param editActionId The navigation action ID for editing an event
     */
    public EventAdapter(Context context, ArrayList<Event> events, User user, RetrieveLocation locationGetter, int viewInfoActionId, int editActionId) {
        super(context, 0, events);
        this.user = user;
        this.locationGetter = locationGetter;
        this.viewInfoActionId = viewInfoActionId;
        this.editActionId = editActionId;
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
            } else if (user.getPrivilegeLevel().equals(UserLevel.ADMIN)) {
                buttonEdit.setVisibility(View.VISIBLE);
                buttonJoin.setVisibility(View.VISIBLE);
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
                Navigation.findNavController(v).navigate(viewInfoActionId, bundle);
            });

            // Handle edit button click
            buttonEdit.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("eventId", event.getId());
                Navigation.findNavController(v).navigate(editActionId, bundle);
            });

            // Handle long press
            convertView.setOnLongClickListener(v -> {
                // Check if the user is an admin
                if (user != null && user.getPrivilegeLevel().equals(UserLevel.ADMIN)) {
                    // Show a confirmation dialog before deleting
                    showDeleteConfirmationPopup(event);
                    Toast.makeText(getContext(), "Deleting " + event.getEventDetails().getName(), Toast.LENGTH_SHORT).show();
                    return true; // Consume the long click
                }
                return false; // Don't consume the click if not an admin
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
                buttonJoin.setText(getContext().getString(R.string.event_btn_leave));
                buttonJoin.setEnabled(true);
                buttonJoin.setAlpha(1);
                buttonJoin.setOnClickListener(v -> {
                    event.leave(dbHandler, ticketId);
                    Toast.makeText(getContext(), "Leaving " + event.getEventDetails().getName(), Toast.LENGTH_SHORT).show();
                });
            } else {
                // User is not on the waitlist
                buttonJoin.setText(getContext().getString(R.string.event_btn_join));
                buttonJoin.setOnClickListener(v -> {
                    // Show pop up to confirm join
                    showJoinConfirmationPopup(event, dbHandler, buttonJoin);
                });
            }

            // Enable the button
            buttonJoin.setEnabled(true);
            buttonJoin.setAlpha(1);
        });
    }

    /**
     * Shows a confirmation popup before joining an event.
     * @param event The event to join.
     * @param dbHandler The database handler.
     */
    private void showJoinConfirmationPopup(Event event, DatabaseHandler dbHandler, Button buttonJoin) {
        if (getContext() instanceof FragmentActivity) {
            FragmentManager fm = ((FragmentActivity) getContext()).getSupportFragmentManager();
            String requestKey = "joinConfirm-" + event.getId();

            fm.setFragmentResultListener(requestKey, (LifecycleOwner) getContext(), (reqKey, bundle) -> {
                boolean result = bundle.getBoolean("buttonChoice");
                if (result) {
                    Toast.makeText(getContext(), "Joining " + event.getEventDetails().getName(), Toast.LENGTH_SHORT).show();

                    if (event.getLogLocation()) {
                        // Hand holdy so the user knows we do things
                        buttonJoin.setEnabled(false);
                        buttonJoin.setAlpha(0.5f);

                        buttonJoin.setText(getContext().getString(R.string.event_btn_joining));
                        locationGetter.getLocation(new SingleListenUpdate<CustomLocation>() {
                            @Override
                            public void onUpdate(CustomLocation newValue) {
                                if (newValue == null) {
                                    Toast.makeText(getContext(), "Location lookup failed.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                event.join(dbHandler, user.getPersonalInformation(), user.getId(), newValue);
                            }
                        });
                    } else {
                        event.join(dbHandler, user.getPersonalInformation(), user.getId(), null);
                    }
                }
            });

            String logLocationMessage = getContext().getString(
                    event.getLogLocation() ?
                            R.string.dialog_event_join_location_required :
                            R.string.dialog_event_join_location_not_required);

            String dialogContents = getContext().getString(
                    R.string.dialog_event_join_confirm_contents,
                    event.getEventDetails().getEntryCriteria(),
                    logLocationMessage);

            StandardPopupDialogFragment.newInstance(
                    getContext().getString(R.string.dialog_event_join_confirm_title),
                    dialogContents, requestKey).show(fm, "dialog_join_confirm");
        }
    }

    /**
     * Shows a confirmation popup before deleting an event.
     * @param event The event to delete.
     */
    private void showDeleteConfirmationPopup(Event event) {
        if (getContext() instanceof FragmentActivity) {
            FragmentManager fm = ((FragmentActivity) getContext()).getSupportFragmentManager();
            String requestKey = "deleteConfirm-" + event.getId();

            // Listen for the result from the popup
            fm.setFragmentResultListener(requestKey, (LifecycleOwner) getContext(), (reqKey, bundle) -> {
                boolean result = bundle.getBoolean("buttonChoice");
                if (result) {
                    // User confirmed the deletion
                    deleteEvent(event);
                }
            });

            // Create and show the confirmation dialog
            StandardPopupDialogFragment.newInstance(
                    "Delete Event?",
                    "Are you sure you want to permanently delete '" + event.getEventDetails().getName() + "'?",
                    requestKey
            ).show(fm, "dialog_delete_confirm");
        }
    }

    /**
     * Deletes an event from Firestore using the repository and removes it from the adapter.
     * @param event The event to delete.
     */
    private void deleteEvent(Event event) {
        // Get an instance of the repository
        EventRepository eventRepository = new EventRepository(FirebaseFirestore.getInstance());

        // Call the delete method on the repository, passing the event's ID
        eventRepository.delete(event.getId())
                .addOnSuccessListener(aVoid -> {
                    // On success, remove the event from the local list and update the UI
                    remove(event);
                    notifyDataSetChanged();
                    Toast.makeText(getContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // On failure, notify the user
                    Toast.makeText(getContext(), "Failed to delete event", Toast.LENGTH_SHORT).show();
                });
    }
}
