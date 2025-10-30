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

import com.example.skeddly.MainActivity;
import com.example.skeddly.R;
import com.example.skeddly.business.user.Authenticator;
import com.example.skeddly.business.user.ExtraInformation;
import com.example.skeddly.business.user.User;
import com.example.skeddly.databinding.ProfileFragmentBinding;


public class ProfileFragment extends Fragment {
    private ProfileFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ProfileFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MainActivity activity = (MainActivity)getActivity();
        Authenticator authenticator = activity.getAuthenticator();
        User user = activity.getUser();

        TextView profileName = binding.include.profileName;
        TextView profileEmail = binding.include.profileEmail;
        TextView profilePhone = binding.include.profilePhone;

        ConstraintLayout deleteAccountButton = root.findViewById(R.id.delete_account_button);

        ExtraInformation userInformation = user.getExtraInformation();

        profileName.setText(userInformation.getName());
        profileEmail.setText(userInformation.getEmail());
        profilePhone.setText(userInformation.getPhoneNumber());

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticator.deleteUser();
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
