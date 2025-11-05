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
import com.example.skeddly.business.user.Authenticator;
import com.example.skeddly.business.user.User;
import com.example.skeddly.databinding.InboxFragmentBinding;
import com.example.skeddly.ui.adapter.InboxAdapter;

import java.util.ArrayList;


public class InboxFragment extends Fragment {
    private InboxFragmentBinding binding;
    private ArrayList<Notification> inbox;
    private DatabaseHandler dbHandler;
    private InboxAdapter inboxAdapter;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = InboxFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Database handler
        dbHandler = new DatabaseHandler(getContext());

        // Get the user
        MainActivity activity = (MainActivity) requireActivity();
        Authenticator authenticator = activity.getAuthenticator();
        User user = activity.getUser();

        // Notif list
        inbox = user.getInbox();

        // Inbox Adapter
        inboxAdapter = new InboxAdapter(getContext(), inbox);

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
