package com.example.skeddly.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.skeddly.R;
import com.example.skeddly.business.database.repository.EventRepository;
import com.example.skeddly.business.database.repository.UserRepository;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.user.User;
import com.example.skeddly.business.user.UserLevel;
import com.example.skeddly.databinding.ItemAdminUserBinding;
import com.example.skeddly.ui.popup.StandardPopupDialogFragment;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class UserAdapter extends ArrayAdapter<User> {
    /**
     * Constructor for the UserAdapter
     * @param context The context of the app
     * @param users The users to display
     */
    public UserAdapter(Context context, List<User> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_admin_user, parent, false);
        }

        ItemAdminUserBinding itemAdminUserBinding = ItemAdminUserBinding.bind(convertView);
        Spinner privilegeSpinner = convertView.findViewById(R.id.privilege_spinner);

        User user = getItem(position);
        if (user != null) {
            TextView textUserName = itemAdminUserBinding.textUserFullName;
            TextView textUserPhone = itemAdminUserBinding.textUserPhone;
            TextView textUserEmail = itemAdminUserBinding.textUserEmail;

            textUserName.setText(user.getPersonalInformation().getName());
            textUserPhone.setText(user.getPersonalInformation().getPhoneNumber());
            textUserEmail.setText(user.getPersonalInformation().getEmail());


            // Set up the spinner adapter
            ArrayAdapter<UserLevel> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, UserLevel.values());
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            privilegeSpinner.setAdapter(spinnerAdapter);

            // Find the correct position for the user's current privilege level.
            int currentPosition = Arrays.asList(UserLevel.values()).indexOf(user.getPrivilegeLevel());

            privilegeSpinner.setSelection(currentPosition, false);

            // Set a listener to handle selection changes from user interaction.
            privilegeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int spinnerPosition, long id) {
                    UserLevel newLevel = (UserLevel) parent.getItemAtPosition(spinnerPosition);

                    // Only update if the level has actually changed from the user's original level
                    if (user.getPrivilegeLevel() != newLevel) {
                        UserLevel oldLevel = user.getPrivilegeLevel();

                        boolean wasOrganizerOrAdmin = oldLevel == UserLevel.ORGANIZER || oldLevel == UserLevel.ADMIN;
                        if (wasOrganizerOrAdmin && newLevel == UserLevel.ENTRANT) {
                            showDemotionWarningPopup(user, newLevel, oldLevel);
                        } else {
                            // If not a demotion, update the privilege level directly.
                            updateUserPrivilege(user, newLevel);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }

        return convertView;
    }

    /**
     * Shows a confirmation popup to warn the admin about the consequences of demoting a user.
     * @param user     The user being demoted.
     * @param newLevel The new, lower privilege level.
     * @param oldLevel The user's original privilege level (to revert to if cancelled).
     */
    private void showDemotionWarningPopup(User user, UserLevel newLevel, UserLevel oldLevel) {
        if (!(getContext() instanceof FragmentActivity)) {
            return;
        }
        FragmentManager fm = ((FragmentActivity) getContext()).getSupportFragmentManager();
        String requestKey = "demoteConfirm-" + user.getId();

        // Listen for the popup result.
        fm.setFragmentResultListener(requestKey, (FragmentActivity) getContext(), (reqKey, bundle) -> {
            boolean confirmed = bundle.getBoolean("buttonChoice");
            if (confirmed) {
                // User confirmed, proceed with the update.
                updateUserPrivilege(user, newLevel);
            } else {
                // User cancelled, revert the spinner back to the original position.
                notifyDataSetChanged();
            }
        });

        // Create and show the confirmation dialog.
        StandardPopupDialogFragment.newInstance(
                getContext().getString(R.string.dialog_user_demotion_title),
                getContext().getString(R.string.dialog_user_demotion_contents),
                requestKey
        ).show(fm, "dialog_demote_confirm");
    }

    /**
     * Updates the user's privilege level in Firestore.
     * @param user     The user to update.
     * @param newLevel The new privilege level.
     */
    private void updateUserPrivilege(User user, UserLevel newLevel) {
        user.setPrivilegeLevel(newLevel);
        UserRepository userRepository = new UserRepository(FirebaseFirestore.getInstance());
        userRepository.set(user)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Updated " + user.getPersonalInformation().getName(), Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show());
    }
}
