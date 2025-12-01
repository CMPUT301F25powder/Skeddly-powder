package com.example.skeddly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skeddly.MainActivity;
import com.example.skeddly.R;
import com.example.skeddly.business.database.repository.EventRepository;
import com.example.skeddly.business.database.repository.adapter.RepositoryToArrayAdapter;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.user.User;
import com.example.skeddly.databinding.FragmentMyEventsBinding;
import com.example.skeddly.ui.adapter.EventAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Fragment to display a list of events owned by the current user.
 */
public class MyEventsFragment extends Fragment {
    private FragmentMyEventsBinding binding;
    private EventAdapter eventAdapter;
    private RepositoryToArrayAdapter<Event> repositoryAdapter;
    private static final String ARG_HIDE_HEADER = "hideHeader";

    /**
     * Factory method to create a new instance of this fragment.
     * @param hideHeader True if the header should be hidden.
     * @return A new instance of fragment MyEventsFragment.
     */
    public static MyEventsFragment newInstance(boolean hideHeader) {
        MyEventsFragment fragment = new MyEventsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_HIDE_HEADER, hideHeader);
        fragment.setArguments(args);
        return fragment;
    }

    public MyEventsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Check if the hideHeader argument was passed
        if (getArguments() != null && getArguments().getBoolean(ARG_HIDE_HEADER, false)) {
            binding.constraintLayout.setVisibility(View.GONE);
        } else {
            binding.constraintLayout.setVisibility(View.VISIBLE);
        }

        setupListView();
    }

    /**
     * Sets up the list view for displaying events.
     */
    private void setupListView() {
        if (getContext() == null || getActivity() == null) return;

        User currentUser = ((MainActivity) getActivity()).getUser();
        if (currentUser == null) {
            return;
        }

        eventAdapter = new EventAdapter(
                getContext(),
                new ArrayList<>(),
                currentUser,
                null,
                R.id.action_global_navigation_event_view_info, // View Info Action
                R.id.action_global_navigation_create           // Edit Action
        );

        binding.listEvents.setAdapter(eventAdapter);

        EventRepository eventRepository = new EventRepository(FirebaseFirestore.getInstance(), currentUser.getId());

        // Use RepositoryToArrayAdapter to link the repository to the adapter
        repositoryAdapter = new RepositoryToArrayAdapter<>(
                eventRepository,
                eventAdapter,
                true
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (repositoryAdapter != null) {
            repositoryAdapter.removeListener();
        }
        binding = null;
    }
}
