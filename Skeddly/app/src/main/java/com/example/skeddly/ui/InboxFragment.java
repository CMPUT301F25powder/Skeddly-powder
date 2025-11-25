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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.skeddly.MainActivity;
import com.example.skeddly.R;
import com.example.skeddly.business.notification.Notification;
import com.example.skeddly.business.notification.NotificationType;
import com.example.skeddly.business.user.User;
import com.example.skeddly.databinding.FragmentInboxBinding;
import com.example.skeddly.ui.adapter.InboxAdapter;
import com.example.skeddly.business.database.repository.NotificationRepository;
import com.example.skeddly.business.database.repository.RepositoryToArrayAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Fragment for the inbox screen
 */
public class InboxFragment extends Fragment implements View.OnClickListener {
    private FragmentInboxBinding binding;
    private ArrayList<Button> filterButtons;

    private ArrayList<Notification> notifications;
    private InboxAdapter inboxAdapter;
    private NotificationRepository notificationRepository;
    private RepositoryToArrayAdapter<Notification> repoToArrayAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInboxBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get the user
        MainActivity activity = (MainActivity) requireActivity();
        User user = activity.getUser();

        // Notif list
        notificationRepository = new NotificationRepository(FirebaseFirestore.getInstance(), user.getId());

        // Inbox Adapter
        notifications = new ArrayList<>();
        inboxAdapter = new InboxAdapter(getContext(), notifications);

        // Adapter adapter
        repoToArrayAdapter = new RepositoryToArrayAdapter<>(notificationRepository, inboxAdapter, true);

        ListView inboxList = binding.listNotifications;
        // Set event adapter to list view
        inboxList.setAdapter(inboxAdapter);

        Button buttonAll = binding.headerInbox.btnAll;
        Button buttonMessages = binding.headerInbox.btnMessages;
        Button buttonRegistration = binding.headerInbox.btnRegistration;
        Button buttonSystem = binding.headerInbox.btnSystem;;

        filterButtons = new ArrayList<>();
        filterButtons.add(buttonAll);
        filterButtons.add(buttonMessages);
        filterButtons.add(buttonRegistration);
        filterButtons.add(buttonSystem);

        // 4. Set the OnClickListener for each button
        for (Button button : filterButtons) {
            button.setOnClickListener(this);
        }

        // 5. Set the initial state (select "All" by default)
        updateButtonSelection(buttonAll);

        inboxList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                inboxAdapter.removeNotification(inboxAdapter.getItem(position));
                inboxAdapter.notifyDataSetChanged();
                return true;
            }
        });

        return root;
    }

    @Override
    public void onClick(View v) {
        // When any button is clicked, update the selection state
        updateButtonSelection(v);

        // Trigger the filter based on which button was clicked
        int viewId = v.getId();
        if (viewId == R.id.btn_all) {
            inboxAdapter.getFilter().filter("3"); // "3" for all, as in your adapter
        } else if (viewId == R.id.btn_messages) {
            // Use the ordinal value of your Notification enum
            inboxAdapter.getFilter().filter(String.valueOf(NotificationType.MESSAGES.ordinal()));
        } else if (viewId == R.id.btn_registration) {
            inboxAdapter.getFilter().filter(String.valueOf(NotificationType.REGISTRATION.ordinal()));
        } else if (viewId == R.id.btn_system) {
            inboxAdapter.getFilter().filter(String.valueOf(NotificationType.SYSTEM.ordinal()));
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        if (repoToArrayAdapter != null) {
            repoToArrayAdapter.removeListener();
        }
    }
}
