package com.example.skeddly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.skeddly.R;
import com.example.skeddly.databinding.FragmentToolsBinding;

/**
 * Fragment for the tools screen
 */
public class ToolsFragment extends Fragment {
    private FragmentToolsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentToolsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ToolButtonsFragment toolButtonsFragment = new ToolButtonsFragment();
        getChildFragmentManager().beginTransaction().replace(binding.fragment.getId(), toolButtonsFragment).commit();

        ImageButton toolsBack = binding.headerTools.toolsBack;

        toolsBack.setVisibility(View.GONE);

        toolButtonsFragment.setImageGalleryButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminImageGalleryFragment fragment = new AdminImageGalleryFragment();
                getChildFragmentManager().beginTransaction().replace(binding.fragment.getId(), fragment).commit();

                toolsBack.setVisibility(View.VISIBLE);
            }
        });

        toolButtonsFragment.setAdminInboxButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminInboxFragment fragment = new AdminInboxFragment();
                getChildFragmentManager().beginTransaction().replace(binding.fragment.getId(), fragment).commit();

                toolsBack.setVisibility(View.VISIBLE);
            }
        });

        toolButtonsFragment.setMyEventsButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyEventsFragment fragment = new MyEventsFragment();
                getChildFragmentManager().beginTransaction().replace(binding.fragment.getId(), fragment).commit();
                toolsBack.setVisibility(View.VISIBLE);
            }
        });

        toolsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolsBack.setVisibility(View.GONE);

                getChildFragmentManager().beginTransaction().replace(binding.fragment.getId(), toolButtonsFragment).commit();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
