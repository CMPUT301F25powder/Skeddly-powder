package com.example.skeddly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.example.skeddly.MainActivity;
import com.example.skeddly.business.user.Authenticator;
import com.example.skeddly.business.user.PersonalInformation;
import com.example.skeddly.business.user.User;
import com.example.skeddly.databinding.FragmentProfileBinding;
import com.example.skeddly.ui.popup.StandardPopupDialogFragment;

/**
 * Fragment for the profile screen
 */
public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MainActivity activity = (MainActivity) requireActivity();
        Authenticator authenticator = activity.getAuthenticator();
        User user = activity.getUser();

        TextView profileName = binding.include.profileName;
        TextView profileEmail = binding.include.profileEmail;
        TextView profilePhone = binding.include.profilePhone;

        ConstraintLayout deleteAccountButton = binding.deleteAccountButton;

        PersonalInformation userInformation = user.getPersonalInformation();

        profileName.setText(userInformation.getName());
        profileEmail.setText(userInformation.getEmail());
        profilePhone.setText(userInformation.getPhoneNumber());

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
