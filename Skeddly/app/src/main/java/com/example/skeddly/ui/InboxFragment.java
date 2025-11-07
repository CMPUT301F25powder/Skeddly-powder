package com.example.skeddly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skeddly.MainActivity;
import com.example.skeddly.business.Notification;
import com.example.skeddly.business.database.DatabaseObjects;
import com.example.skeddly.business.user.User;
import com.example.skeddly.databinding.InboxFragmentBinding;
import com.example.skeddly.ui.adapter.InboxAdapter;


public class InboxFragment extends Fragment {
    private InboxFragmentBinding binding;
    private DatabaseObjects<Notification> inbox;
    private InboxAdapter inboxAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = InboxFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get the user
        MainActivity activity = (MainActivity) requireActivity();
        User user = activity.getUser();

        // Notif list
        inbox = user.getNotifications();

        Notification testNotif1 = new Notification();
        testNotif1.setTitle("Test1!");
        testNotif1.setMessage("This is a fake notification.");
        testNotif1.setType(Notification.notification_type.MESSAGES);
        inbox.add(testNotif1);
        Notification testNotif2 = new Notification();
        testNotif2.setTitle("Test2!");
        testNotif2.setMessage("This is a fake notification.");
        testNotif2.setType(Notification.notification_type.REGISTRATION);
        inbox.add(testNotif2);
        Notification testNotif3 = new Notification();
        testNotif3.setTitle("Test3!");
        testNotif3.setMessage("This is a fake notification.");
        testNotif3.setType(Notification.notification_type.SYSTEM);
        inbox.add(testNotif3);

        user.setNotifications(inbox);

        activity.notifyUserChanged();

        // Inbox Adapter
        inboxAdapter = new InboxAdapter(getContext(), user);

        // Set event adapter to list view
        binding.listNotifications.setAdapter(inboxAdapter);

        Button showAllButton = binding.inboxHeader.buttonAll;
        showAllButton.setOnClickListener((v) -> {
            inboxAdapter.setDisplayMode(3);
        });

        Button showMessagesButton = binding.inboxHeader.buttonMessages;
        showMessagesButton.setOnClickListener((v) -> {
            inboxAdapter.setDisplayMode(Notification.notification_type.MESSAGES);
        });

        Button showRegistrationButton = binding.inboxHeader.buttonRegistration;
        showRegistrationButton.setOnClickListener((v) -> {
            inboxAdapter.setDisplayMode(Notification.notification_type.REGISTRATION);
        });

        Button showSystemButton = binding.inboxHeader.buttonSystem;;
        showSystemButton.setOnClickListener((v) -> {
            inboxAdapter.setDisplayMode(Notification.notification_type.SYSTEM);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
