package com.example.skeddly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skeddly.databinding.FragmentToolsBinding;
import com.example.skeddly.ui.utility.FragmentAnim;

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

        toolButtonsFragment.setImageGalleryButtonOnClickListener(v -> changeNewFragment(new AdminImageGalleryFragment(), View.VISIBLE));
        toolButtonsFragment.setNotificationLogsButtonOnClickListener(v -> changeNewFragment(new AdminInboxFragment(), View.VISIBLE));
        toolButtonsFragment.setViewUserButtonOnClickListener(v -> changeNewFragment(new AdminUserViewFragment(), View.VISIBLE));
        toolButtonsFragment.setMyEventsButtonOnClickListener(v -> changeNewFragment(MyEventsFragment.newInstance(true), View.VISIBLE));
        toolsBack.setOnClickListener(v -> changeNewFragment(toolButtonsFragment, View.GONE));

        return root;
    }

    /**
     * Changes to show a new fragment.
     * @param fragment The fragment to change to.
     * @param backVisibility Whether we should show a back button or not.
     */
    private void changeNewFragment(Fragment fragment, int backVisibility) {
        FragmentAnim.setDefaultAnimations(getChildFragmentManager().beginTransaction())
                .replace(binding.fragment.getId(), fragment).commit();

        ImageButton toolsBack = binding.headerTools.toolsBack;
        toolsBack.setVisibility(backVisibility);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
