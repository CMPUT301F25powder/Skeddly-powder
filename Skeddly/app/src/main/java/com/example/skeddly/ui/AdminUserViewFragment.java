package com.example.skeddly.ui;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

import com.example.skeddly.business.database.repository.GenericRepository;
import com.example.skeddly.business.database.repository.NotificationRepository;
import com.example.skeddly.business.database.repository.UserRepository;
import com.example.skeddly.business.database.repository.adapter.RepositoryToArrayAdapter;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.business.user.User;
import com.example.skeddly.business.user.UserLevel;
import com.example.skeddly.databinding.FragmentAdminInboxBinding;
import com.example.skeddly.databinding.FragmentAdminUserViewBinding;
import com.example.skeddly.ui.adapter.UserAdapter;
import com.example.skeddly.ui.popup.StandardPopupDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminUserViewFragment extends Fragment {
    private FragmentAdminUserViewBinding binding;
    private UserAdapter adapter;
    private UserRepository userRepositoryAll;
    private UserRepository userRepositoryOrg;
    private ArrayList<GenericRepository<User>> repoList;
    private RepositoryToArrayAdapter<User> repositoryToArrayAdapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminUserViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userRepositoryAll = new UserRepository(FirebaseFirestore.getInstance());
        userRepositoryOrg = new UserRepository(FirebaseFirestore.getInstance(), UserLevel.ORGANIZER);

        adapter = new UserAdapter(getContext(), new ArrayList<>());
        repoList = new ArrayList<>();
        repoList.add(userRepositoryAll);
        repoList.add(userRepositoryOrg);

        repositoryToArrayAdapter = new RepositoryToArrayAdapter<>(repoList, adapter, true);
        binding.listViewUsers.setAdapter(adapter);

        binding.listViewUsers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                User user = adapter.getItem(position);
                showDeletePopup(user);
                return true;
            }
        });

        // toggle between all users and only organizers
        binding.switchOrganizersOnly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            repositoryToArrayAdapter.switchDataset(isChecked ? userRepositoryOrg : userRepositoryAll);
        });

        // remove orphan users
        removeOrphanUsers();
        return root;
    }

    /**
     * Shows a confirmation popup before deleting a user.
     * @param user The user to be deleted.
     */
    private void showDeletePopup(User user) {
        if (getContext() == null) return;

        FragmentManager fm = getParentFragmentManager();
        // Create a unique request key using the user's ID
        String requestKey = "deleteUserConfirm-" + user.getId();

        // Listen for the result from the popup
        fm.setFragmentResultListener(requestKey, this, (reqKey, bundle) -> {
            boolean result = bundle.getBoolean("buttonChoice");
            if (result) {
                // User confirmed the deletion
                userRepositoryAll.delete(user.getId());
            }
        });

        // Create and show the confirmation dialog with user-specific text
        StandardPopupDialogFragment.newInstance(
                "Delete User?",
                "Are you sure you want to permanently delete user '" + user.getPersonalInformation().getName() + "'?",
                requestKey
        ).show(fm, "dialog_delete_user_confirm");
    }

    /**
     * Removes users with no name.
     */
    private void removeOrphanUsers() {
        userRepositoryAll.getAll().addOnSuccessListener(users -> {
            for (User user : users) {
                if (user.getPersonalInformation().getName() == null || user.getPersonalInformation().getName().isEmpty()) {
                    userRepositoryAll.delete(user.getId());
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        repositoryToArrayAdapter.removeListener();
    }


}
