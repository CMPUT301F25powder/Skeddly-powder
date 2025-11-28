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
import com.example.skeddly.business.user.NotificationSettings;
import com.example.skeddly.databinding.FragmentNotificationSettingsBinding;

/**
 * Fragment for a users notification settings
 */
public class NotificationSettingsFragment extends Fragment {

    private NotificationSettings userNotifSettings;
    private FragmentNotificationSettingsBinding binding;

    private Switch lotteryStatusNotificationsSwitch;
    private Switch eventUpdateNotificationsSwitch;
    private Switch administrativeNotificationsSwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNotificationSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // are user notification settings stored in firebase, if so im not sure how to retrieve that but like imagine i did that here
        userNotifSettings = new NotificationSettings();

        // binding
        lotteryStatusNotificationsSwitch = binding.switchLotteryStatus;
        eventUpdateNotificationsSwitch = binding.switchEventUpdate;
        administrativeNotificationsSwitch = binding.switchAdministrative;

        // based on booleans from notification settinsg
        lotteryStatusNotificationsSwitch.setChecked(userNotifSettings.getLotteryStatus());
        eventUpdateNotificationsSwitch.setChecked(userNotifSettings.getEventUpdate());
        administrativeNotificationsSwitch.setChecked(userNotifSettings.getAdministrative());

        lotteryStatusNotificationsSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        userNotifSettings.setLotteryStatus(isChecked);
                    }
                });

        eventUpdateNotificationsSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        userNotifSettings.setEventUpdate(isChecked);
                    }
                });

        administrativeNotificationsSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        userNotifSettings.setAdministrative(isChecked);
                    }
                });

        return root;
    }
}
