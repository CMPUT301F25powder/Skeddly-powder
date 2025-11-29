package com.example.skeddly.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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

        // Fill in their details
        updatePersonalInfo();

        ImageButton backButton = binding.headerProfile.btnBack;
        backButton.setVisibility(View.INVISIBLE);

        // Show all the buttons of things they can do
        ProfileButtonsFragment pbf = new ProfileButtonsFragment();
        getChildFragmentManager().beginTransaction().replace(binding.fragment.getId(), pbf).commit();

        View.OnClickListener returnToButtons = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChildFragmentManager().beginTransaction().replace(binding.fragment.getId(), pbf).commit();
                backButton.setVisibility(View.INVISIBLE);
                updatePersonalInfo();
            }
        };


        // What to do when they press to navigate to the personal info edit fragment
        pbf.setPersonalInfoBtnOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButton.setVisibility(View.VISIBLE);
                PersonalInformationEditFragment pief = new PersonalInformationEditFragment();
                getChildFragmentManager().beginTransaction().replace(binding.fragment.getId(), pief).commit();

                // If they submit, we return back to profile buttons
                pief.setOnCompleteListener(returnToButtons);
            }
        });

        // On click listener for EventHistoryFragment
        pbf.setEventHistoryButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButton.setVisibility(View.VISIBLE);
                EventHistoryFragment ehf = new EventHistoryFragment();
                getChildFragmentManager().beginTransaction().replace(binding.fragment.getId(), ehf).commit();
            }
        });

        backButton.setOnClickListener(returnToButtons);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Updates the header with the personal info of the user
     */
    private void updatePersonalInfo() {
        MainActivity activity = (MainActivity) requireActivity();
        User user = activity.getUser();

        TextView profileName = binding.headerProfile.profileName;
        TextView profileEmail = binding.headerProfile.profileEmail;
        TextView profilePhone = binding.headerProfile.profilePhone;

        PersonalInformation userInformation = user.getPersonalInformation();
        profileName.setText(userInformation.getName());
        profileEmail.setText(userInformation.getEmail());
        profilePhone.setText(userInformation.getPhoneNumber());
    }
}
