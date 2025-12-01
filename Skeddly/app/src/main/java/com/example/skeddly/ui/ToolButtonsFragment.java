package com.example.skeddly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.skeddly.R;
import com.example.skeddly.databinding.FragmentToolsButtonsBinding;

public class ToolButtonsFragment extends Fragment {
    private FragmentToolsButtonsBinding binding;
    private View.OnClickListener myEventsOnClickListener = null;
    private View.OnClickListener imageGalleryOnClickListener = null;
    private View.OnClickListener viewUsersOnClickListener = null;
    private View.OnClickListener notificationLogsOnClickListener = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentToolsButtonsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ConstraintLayout myEventsButton = binding.btnMyEvents;
        ConstraintLayout imageGalleryButton = binding.btnImgGallery;
        ConstraintLayout viewUserButton = binding.btnViewUser;
        ConstraintLayout adminInboxButton = binding.btnLogNotification;

        if (this.myEventsOnClickListener != null) {
            myEventsButton.setOnClickListener(this.myEventsOnClickListener);
        }

        if (this.imageGalleryOnClickListener != null ) {
            imageGalleryButton.setOnClickListener(this.imageGalleryOnClickListener);
        }

        if (this.viewUsersOnClickListener != null) {
            viewUserButton.setOnClickListener(this.viewUsersOnClickListener);
        }

        if (this.notificationLogsOnClickListener != null) {
            adminInboxButton.setOnClickListener(this.notificationLogsOnClickListener);
        }

        return root;
    }

    /**
     * Sets the callback for when one presses the personal info edit button.
     * @param onClickListener The callback to add to the button.
     */
    public void setMyEventsButtonOnClickListener(View.OnClickListener onClickListener) {
        this.myEventsOnClickListener = onClickListener;
        if (binding != null) {
            binding.btnMyEvents.setOnClickListener(myEventsOnClickListener);
        }
    }

    /**
     * Sets the callback for when one presses the personal info edit button.
     * @param onClickListener The callback to add to the button.
     */
    public void setImageGalleryButtonOnClickListener(View.OnClickListener onClickListener) {
        this.imageGalleryOnClickListener = onClickListener;

        if (binding != null) {
            binding.btnImgGallery.setOnClickListener(imageGalleryOnClickListener);
        }
    }

    /**
     * Sets the callback for when one presses the personal info edit button.
     * @param onClickListener The callback to add to the button.
     */
    public void setViewUserButtonOnClickListener(View.OnClickListener onClickListener) {
        this.viewUsersOnClickListener = onClickListener;
        if (binding != null) {
            binding.btnViewUser.setOnClickListener(viewUsersOnClickListener);
        }
    }

    /**
     * Sets the callback for when one presses the personal info edit button.
     * @param onClickListener The callback to add to the button.
     */
    public void setNotificationLogsButtonOnClickListener(View.OnClickListener onClickListener) {
        this.notificationLogsOnClickListener = onClickListener;
        if (binding != null) {
            binding.btnLogNotification.setOnClickListener(notificationLogsOnClickListener);
        }
    }
}
