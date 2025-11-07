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
import com.example.skeddly.business.Notification;
import com.example.skeddly.business.database.DatabaseObjects;
import com.example.skeddly.business.user.User;
import com.example.skeddly.databinding.InboxFragmentBinding;
import com.example.skeddly.ui.adapter.InboxAdapter;

import java.util.ArrayList;


public class InboxFragment extends Fragment implements View.OnClickListener {
    private InboxFragmentBinding binding;
    private DatabaseObjects<Notification> inbox;
    private InboxAdapter inboxAdapter;
    private ArrayList<Button> filterButtons;

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
        inboxAdapter = new InboxAdapter(getContext(), inbox);

        ListView inboxList = binding.listNotifications;
        // Set event adapter to list view
        inboxList.setAdapter(inboxAdapter);

        Button buttonAll = binding.inboxHeader.buttonAll;
        Button buttonMessages = binding.inboxHeader.buttonMessages;
        Button buttonRegistration = binding.inboxHeader.buttonRegistration;
        Button buttonSystem = binding.inboxHeader.buttonSystem;;

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

        // 6. Trigger the filter based on which button was clicked
        // IMPORTANT: Replace with your actual enum types
        int viewId = v.getId();
        if (viewId == R.id.button_all) {
            inboxAdapter.getFilter().filter("3"); // "3" for all, as in your adapter
        } else if (viewId == R.id.button_messages) {
            // Use the ordinal value of your Notification enum
            inboxAdapter.getFilter().filter(String.valueOf(Notification.notification_type.MESSAGES.ordinal()));
        } else if (viewId == R.id.button_registration) {
            inboxAdapter.getFilter().filter(String.valueOf(Notification.notification_type.REGISTRATION.ordinal()));
        } else if (viewId == R.id.button_system) {
            inboxAdapter.getFilter().filter(String.valueOf(Notification.notification_type.SYSTEM.ordinal()));
        }
    }

    private void updateButtonSelection(View selectedButton) {
        // Loop through all buttons in our list
        for (Button button : filterButtons) {
            if (button == selectedButton) {
                // This is the selected button
                // Set the navy blue gradient background and white text
                button.setBackground(ContextCompat.getDrawable(this.getContext(), R.drawable.navy_blue_gradient));
                button.setTextColor(ContextCompat.getColor(this.getContext(), R.color.neutral_lighter_off_white));
            } else {
                // This is an unselected button
                // Set the default background and blue text
                button.setBackground(ContextCompat.getDrawable(this.getContext(), R.drawable.button_unselect));
                button.setTextColor(ContextCompat.getColor(this.getContext(), R.color.primary_light_blue));
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
