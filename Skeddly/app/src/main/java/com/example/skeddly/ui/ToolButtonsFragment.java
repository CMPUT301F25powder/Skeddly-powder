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
    private View.OnClickListener imageGalleryOnClickListener = null;
    private View.OnClickListener adminInboxOnClickListener = null;
    private View.OnClickListener adminUserViewOnClickListener = null;
    private View.OnClickListener myEventsOnClickListener = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentToolsButtonsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ConstraintLayout testFragmentButton = binding.btnFragmentTest;
        ConstraintLayout imageGalleryButton = binding.btnImgGallery;
        ConstraintLayout adminInboxButton = binding.btnLogNotification;
        ConstraintLayout adminUserViewButton = binding.btnSearchUser;
        ConstraintLayout myEventsButton = binding.btnMyEvents;

        testFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_navigation_tools_to_test);
            }
        });

        if (this.imageGalleryOnClickListener != null ) {
            imageGalleryButton.setOnClickListener(this.imageGalleryOnClickListener);
        }

        if (this.adminInboxOnClickListener != null) {
            adminInboxButton.setOnClickListener(this.adminInboxOnClickListener);
        }

        if (this.adminUserViewOnClickListener != null) {
            adminUserViewButton.setOnClickListener(this.adminUserViewOnClickListener);
        }

        if (this.myEventsOnClickListener != null) {
            myEventsButton.setOnClickListener(this.myEventsOnClickListener);
        }

        return root;
    }

    public void setImageGalleryButtonOnClickListener(View.OnClickListener onClickListener) {
        this.imageGalleryOnClickListener = onClickListener;

        if (binding != null) {
            binding.btnImgGallery.setOnClickListener(imageGalleryOnClickListener);
        }
    }

    public void setAdminInboxButtonOnClickListener(View.OnClickListener onClickListener) {
        this.adminInboxOnClickListener = onClickListener;
        if (binding != null) {
            binding.btnLogNotification.setOnClickListener(adminInboxOnClickListener);
        }
    }

    public void setAdminUserViewButtonOnClickListener(View.OnClickListener onClickListener) {
        this.adminUserViewOnClickListener = onClickListener;
        if (binding != null) {
            binding.btnSearchUser.setOnClickListener(adminUserViewOnClickListener);
        }
    }

    public void setMyEventsButtonOnClickListener(View.OnClickListener onClickListener) {
        this.myEventsOnClickListener = onClickListener;
        if (binding != null) {
            binding.btnMyEvents.setOnClickListener(myEventsOnClickListener);
        }
    }
}
