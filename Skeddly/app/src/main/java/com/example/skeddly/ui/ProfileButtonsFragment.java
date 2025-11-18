package com.example.skeddly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.skeddly.MainActivity;
import com.example.skeddly.R;
import com.example.skeddly.business.user.Authenticator;
import com.example.skeddly.databinding.FragmentProfileButtonsBinding;
import com.example.skeddly.ui.popup.StandardPopupDialogFragment;

/**
 * Fragment for the profile screen buttons
 */
public class ProfileButtonsFragment extends Fragment {
    private FragmentProfileButtonsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileButtonsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MainActivity activity = (MainActivity) requireActivity();
        Authenticator authenticator = activity.getAuthenticator();

        ConstraintLayout deleteAccountButton = binding.btnDeleteAccount;
        ConstraintLayout profileInfoButton = binding.btnPersonalInfo;

        profileInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_profile_to_personal_info_edit);
            }
        });

        String deletePopupTitle = "Delete Account";
        String deletePopupContent = "Are you sure you want to delete your account?";
        String deletePopupRequestKey = "confirmationDialog";

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StandardPopupDialogFragment spf = StandardPopupDialogFragment
                        .newInstance(deletePopupTitle, deletePopupContent, deletePopupRequestKey);
                spf.show(getChildFragmentManager(), deletePopupRequestKey);
            }
        });

        getChildFragmentManager().setFragmentResultListener(deletePopupRequestKey,
                this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        boolean confirmation = result.getBoolean("buttonChoice");

                        if (confirmation) {
                            authenticator.deleteUser();
                            MainActivity mainActivity = (MainActivity) requireActivity();
                            mainActivity.switchToSignup();
                        }
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
