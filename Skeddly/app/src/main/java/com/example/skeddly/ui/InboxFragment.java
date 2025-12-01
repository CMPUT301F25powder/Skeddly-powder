package com.example.skeddly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.example.skeddly.MainActivity;
import com.example.skeddly.R;
import com.example.skeddly.business.TicketStatus;
import com.example.skeddly.business.database.repository.GenericRepository;
import com.example.skeddly.business.database.repository.adapter.RepositoryToArrayAdapter;
import com.example.skeddly.business.database.repository.TicketRepository;
import com.example.skeddly.business.notification.Notification;
import com.example.skeddly.business.notification.NotificationType;
import com.example.skeddly.business.user.User;
import com.example.skeddly.databinding.FragmentInboxBinding;
import com.example.skeddly.business.database.repository.NotificationRepository;
import com.example.skeddly.ui.adapter.InboxAdapter;
import com.example.skeddly.ui.popup.StandardPopupDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fragment for the inbox screen
 */
public class InboxFragment extends Fragment implements View.OnClickListener {
    private FragmentInboxBinding binding;
    private ArrayList<Button> filterButtons;

    private InboxAdapter inboxAdapter;
    private NotificationRepository notificationRepositoryAll;
    private NotificationRepository notificationRepositoryMessages;
    private NotificationRepository notificationRepositoryRegistration;
    private NotificationRepository notificationRepositorySystem;

    private RepositoryToArrayAdapter<Notification> multiRepoArrayAdapter;

    private TicketRepository ticketRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInboxBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ticketRepository = new TicketRepository(FirebaseFirestore.getInstance(), null);

        // Get the user
        MainActivity activity = (MainActivity) requireActivity();
        User user = activity.getUser();

        // Setup notif list
        notificationRepositoryAll = new NotificationRepository(FirebaseFirestore.getInstance(), user.getId());
        notificationRepositoryMessages = new NotificationRepository(FirebaseFirestore.getInstance(), user.getId(), NotificationType.MESSAGES);
        notificationRepositoryRegistration = new NotificationRepository(FirebaseFirestore.getInstance(), user.getId(), NotificationType.REGISTRATION);
        notificationRepositorySystem = new NotificationRepository(FirebaseFirestore.getInstance(), user.getId(), NotificationType.SYSTEM);

        List<GenericRepository<Notification>> repositories = Arrays.asList(notificationRepositoryAll, notificationRepositoryMessages, notificationRepositoryRegistration, notificationRepositorySystem);
        inboxAdapter = new InboxAdapter(getContext(), new ArrayList<>());
        multiRepoArrayAdapter = new RepositoryToArrayAdapter<>(repositories, inboxAdapter, true);

        // Set event adapter to list view
        ListView inboxList = binding.listNotifications;
        inboxList.setAdapter(inboxAdapter);

        // Setup filter buttons
        Button buttonAll = binding.headerInbox.btnAll;
        Button buttonMessages = binding.headerInbox.btnMessages;
        Button buttonRegistration = binding.headerInbox.btnRegistration;
        Button buttonSystem = binding.headerInbox.btnSystem;;

        filterButtons = new ArrayList<>();
        filterButtons.add(buttonAll);
        filterButtons.add(buttonMessages);
        filterButtons.add(buttonRegistration);
        filterButtons.add(buttonSystem);

        // Set the OnClickListener for each button
        for (Button button : filterButtons) {
            button.setOnClickListener(this);
        }

        // Set the initial state (select "All" by default)
        updateButtonSelection(buttonAll);

        inboxList.setOnItemLongClickListener((parent, view, position, id) -> {
            Notification notification = (Notification) parent.getItemAtPosition(position);
            notificationRepositoryAll.delete(notification.getId());
            return true;
        });

        inboxList.setOnItemClickListener((adapterView, view, i, l) -> {
            Notification notification = (Notification) adapterView.getItemAtPosition(i);
            if (notification.getType() == NotificationType.REGISTRATION) {
                StandardPopupDialogFragment spdf = StandardPopupDialogFragment.newInstance("Accept Invitation",
                        "Would you like to join " + notification.getTitle(), notification.getTicketId());
                setupPopupListener(notification);
                spdf.show(getChildFragmentManager(), null);
            }
        });

        return root;
    }

    @Override
    public void onClick(View v) {
        // When any button is clicked, update the selection state
        updateButtonSelection(v);

        // Filter based on which button was clicked
        int viewId = v.getId();
        if (viewId == R.id.btn_all) {
            multiRepoArrayAdapter.switchDataset(notificationRepositoryAll);
        } else if (viewId == R.id.btn_messages) {
            multiRepoArrayAdapter.switchDataset(notificationRepositoryMessages);
        } else if (viewId == R.id.btn_registration) {
            multiRepoArrayAdapter.switchDataset(notificationRepositoryRegistration);
        } else if (viewId == R.id.btn_system) {
            multiRepoArrayAdapter.switchDataset(notificationRepositorySystem);
        }
    }

    /**
     * Updates the selected button appearance based on the one that was pressed.
     * @param selectedButton The button that was pressed.
     */
    private void updateButtonSelection(View selectedButton) {
        // Loop through all buttons in our list
        for (Button button : filterButtons) {
            if (button == selectedButton) {
                // This is the selected button
                // Set the navy blue gradient background and white text
                button.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.gradient_navy_blue));
                button.setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral_lighter_off_white));
            } else {
                // This is an unselected button
                // Set the default background and blue text
                button.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.btn_unselect));
                button.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_light_blue));
            }
        }
    }

    /**
     * Sets up the listener for the popup fragment.
     * @param notification The notification to set it up for
     */
    private void setupPopupListener(Notification notification) {
        getChildFragmentManager().setFragmentResultListener(notification.getTicketId(), this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                boolean choice = result.getBoolean("buttonChoice");

                if (choice) {
                    ticketRepository.updateStatus(requestKey, TicketStatus.ACCEPTED);
                } else {
                    ticketRepository.updateStatus(requestKey, TicketStatus.CANCELLED);
                }
                notificationRepositoryAll.delete(notification.getId());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
