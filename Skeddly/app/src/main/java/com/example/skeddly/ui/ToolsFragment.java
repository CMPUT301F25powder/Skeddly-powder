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

    private enum ToolsFragmentSubFragments {
        TOOLS_BUTTONS_FRAGMENT,
        ADMIN_IMAGE_GALLERY_FRAGMENT,
        ADMIN_INBOX_FRAGMENT,
        ADMIN_USER_VIEW_FRAGMENT,
        MY_EVENTS_FRAGMENT
    }

    private ToolsFragmentSubFragments curFragment;
    private ToolButtonsFragment toolsButtonFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentToolsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        toolsButtonFragment = new ToolButtonsFragment();

        ImageButton toolsBack = binding.headerTools.toolsBack;
        toolsBack.setVisibility(View.GONE);

        toolsButtonFragment.setImageGalleryButtonOnClickListener(v -> {
            curFragment = ToolsFragmentSubFragments.ADMIN_IMAGE_GALLERY_FRAGMENT;
            updateCurrentFragment();
        });

        toolsButtonFragment.setNotificationLogsButtonOnClickListener(v -> {
            curFragment = ToolsFragmentSubFragments.ADMIN_INBOX_FRAGMENT;
            updateCurrentFragment();
        });

        toolsButtonFragment.setViewUserButtonOnClickListener(v -> {
            curFragment = ToolsFragmentSubFragments.ADMIN_USER_VIEW_FRAGMENT;
            updateCurrentFragment();
        });

        toolsButtonFragment.setMyEventsButtonOnClickListener(v -> {
            curFragment = ToolsFragmentSubFragments.MY_EVENTS_FRAGMENT;
            updateCurrentFragment();
        });

        toolsBack.setOnClickListener(v -> {
            curFragment = ToolsFragmentSubFragments.TOOLS_BUTTONS_FRAGMENT;
            updateCurrentFragment();
        });

        if (curFragment == null) {
            curFragment = ToolsFragmentSubFragments.TOOLS_BUTTONS_FRAGMENT;
        }

        updateCurrentFragment();

        return root;
    }

    /**
     * Updates the currently shown fragment to be the one that it should be
     */
    private void updateCurrentFragment() {
        switch (curFragment) {
            case ADMIN_IMAGE_GALLERY_FRAGMENT:
                changeNewFragment(new AdminImageGalleryFragment(), View.VISIBLE);
                break;

            case ADMIN_INBOX_FRAGMENT:
                changeNewFragment(new AdminInboxFragment(), View.VISIBLE);
                break;

            case ADMIN_USER_VIEW_FRAGMENT:
                changeNewFragment(new AdminUserViewFragment(), View.VISIBLE);
                break;

            case MY_EVENTS_FRAGMENT:
                changeNewFragment(MyEventsFragment.newInstance(true), View.VISIBLE);
                break;

            default:
                changeNewFragment(toolsButtonFragment, View.GONE);
                break;
        }
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
