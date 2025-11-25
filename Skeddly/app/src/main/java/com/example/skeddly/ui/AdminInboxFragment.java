package com.example.skeddly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

import com.example.skeddly.business.database.repository.NotificationRepository;
import com.example.skeddly.business.database.repository.adapter.RepositoryToArrayAdapter;
import com.example.skeddly.business.notification.Notification;
import com.example.skeddly.databinding.FragmentAdminInboxBinding;
import com.example.skeddly.ui.adapter.InboxAdapter;
import com.example.skeddly.ui.popup.StandardPopupDialogFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminInboxFragment extends Fragment {
    private FragmentAdminInboxBinding binding;
    private InboxAdapter notifAdapter;
    private NotificationRepository notificationRepository;
    private RepositoryToArrayAdapter<Notification> repositoryToArrayAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminInboxBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        notificationRepository = new NotificationRepository(FirebaseFirestore.getInstance());
        notifAdapter = new InboxAdapter(getContext(), new ArrayList<>());
        repositoryToArrayAdapter = new RepositoryToArrayAdapter<>(notificationRepository, notifAdapter, true);
        binding.listNotifications.setAdapter(notifAdapter);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        repositoryToArrayAdapter.removeListener();
    }
}
