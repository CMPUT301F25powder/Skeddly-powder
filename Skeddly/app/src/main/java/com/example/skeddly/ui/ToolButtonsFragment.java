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
    private View.OnClickListener AdminInboxOnClickListener = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentToolsButtonsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ConstraintLayout testFragmentButton = binding.btnFragmentTest;
        ConstraintLayout imageGalleryButton = binding.btnImgGallery;
        ConstraintLayout adminInboxButton = binding.btnLogNotification;


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

        if (this.AdminInboxOnClickListener != null) {
            adminInboxButton.setOnClickListener(this.AdminInboxOnClickListener);
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
        this.AdminInboxOnClickListener = onClickListener;
        if (binding != null) {
            binding.btnLogNotification.setOnClickListener(AdminInboxOnClickListener);
        }
    }
}
