package com.example.skeddly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

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
        user.setNotifications(inbox);

        activity.notifyUserChanged();

        // Inbox Adapter
        inboxAdapter = new InboxAdapter(getContext(), user, inbox);

        ListView inboxList = binding.listNotifications;
        // Set event adapter to list view
        inboxList.setAdapter(inboxAdapter);

        Notification testNotif = new Notification("Hello", "This is a test.");
        inbox.add(testNotif);

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

        inboxList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                inbox.remove(position);
                inboxAdapter.notifyDataSetChanged();
                return true;
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
