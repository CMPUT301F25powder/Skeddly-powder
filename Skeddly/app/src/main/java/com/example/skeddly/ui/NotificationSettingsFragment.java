package com.example.skeddly.ui;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skeddly.MainActivity;
import com.example.skeddly.business.user.NotificationSettings;
import com.example.skeddly.business.user.User;
import com.example.skeddly.databinding.FragmentNotificationSettingsBinding;
import com.google.android.material.materialswitch.MaterialSwitch;

/**
 * Fragment for a users notification settings
 */
public class NotificationSettingsFragment extends Fragment {

    private NotificationSettings userNotifSettings;
    private FragmentNotificationSettingsBinding binding;
    private MainActivity mainActivity;

    private MaterialSwitch lotteryStatusNotificationsSwitch;
    private MaterialSwitch eventUpdateNotificationsSwitch;
    private MaterialSwitch administrativeNotificationsSwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNotificationSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get the MainActivity to access the user object and save changes
        mainActivity = (MainActivity) requireActivity();
        User currentUser = mainActivity.getUser();

        // Load the user's notification settings from the User object
        userNotifSettings = currentUser.getNotificationSettings();

        // binding
        lotteryStatusNotificationsSwitch = binding.switchLotteryStatus;
        eventUpdateNotificationsSwitch = binding.switchEventUpdate;
        administrativeNotificationsSwitch = binding.switchAdministrative;

        // Set the switches based on the user's real settings
        lotteryStatusNotificationsSwitch.setChecked(userNotifSettings.getLotteryStatus());
        eventUpdateNotificationsSwitch.setChecked(userNotifSettings.getEventUpdate());
        administrativeNotificationsSwitch.setChecked(userNotifSettings.getAdministrative());

        // Add listeners that save changes to the database
        lotteryStatusNotificationsSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    userNotifSettings.setLotteryStatus(isChecked);
                    mainActivity.notifyUserChanged(); // Save changes to Firebase
                });

        eventUpdateNotificationsSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    userNotifSettings.setEventUpdate(isChecked);
                    mainActivity.notifyUserChanged(); // Save changes to Firebase
                });

        administrativeNotificationsSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    userNotifSettings.setAdministrative(isChecked);
                    mainActivity.notifyUserChanged(); // Save changes to Firebase
                });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
