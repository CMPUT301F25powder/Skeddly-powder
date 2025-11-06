package com.example.skeddly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skeddly.MainActivity;
import com.example.skeddly.business.Notification;
import com.example.skeddly.business.database.DatabaseHandler;
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
        inbox = user.customGetNotifications();

        Notification testNotif = new Notification();
        testNotif.setTitle("Test!");
        testNotif.setMessage("This is a fake notification.");
        inbox.add(testNotif);

        user.customSetNotifications(inbox);

        activity.notifyUserChanged();

        // Inbox Adapter
        inboxAdapter = new InboxAdapter(getContext(), user);

        // Set event adapter to list view
        binding.listNotifications.setAdapter(inboxAdapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
