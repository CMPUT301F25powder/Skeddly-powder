package com.example.skeddly.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

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
    private View.OnClickListener personalInfoOnClickListener = null;
    private View.OnClickListener eventHistoryOnClickListener = null;
    private View.OnClickListener notificationSettingsOnClickListener = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileButtonsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MainActivity activity = (MainActivity) requireActivity();
        Authenticator authenticator = activity.getAuthenticator();

        ConstraintLayout deleteAccountButton = binding.btnDeleteAccount;

        if (personalInfoOnClickListener != null) {
            binding.btnPersonalInfo.setOnClickListener(personalInfoOnClickListener);        }
        if (eventHistoryOnClickListener != null) {
            binding.btnEventHistory.setOnClickListener(eventHistoryOnClickListener);
        }
        if (notificationSettingsOnClickListener != null) {
            binding.notificationSettingsButton.setOnClickListener(notificationSettingsOnClickListener);
        }

        String deletePopupRequestKey = "deletePopup";
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StandardPopupDialogFragment spf = StandardPopupDialogFragment
                        .newInstance(getString(R.string.fragment_profile_delete_account),
                                getString(R.string.fragment_profile_popup_delete_content),
                                deletePopupRequestKey);
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

    /**
     * Sets the callback for when one presses the personal info edit button.
     * @param onClickListener The callback to add to the button.
     */
    public void setPersonalInfoBtnOnClickListener(View.OnClickListener onClickListener) {
        this.personalInfoOnClickListener = onClickListener;

        if (binding != null) {
            binding.btnPersonalInfo.setOnClickListener(personalInfoOnClickListener);
        }
    }


    /**
     * Defines the click listener for the Event History button.
     * @param listener The action to perform when the button is clicked.
     */
    public void setEventHistoryButtonOnClickListener(View.OnClickListener listener) {
        this.eventHistoryOnClickListener = listener;
        if (binding != null) {
            binding.btnEventHistory.setOnClickListener(eventHistoryOnClickListener);
        }
    }

    /**
     * Defines the click listener for the Notification Settings button.
     * @param onClickListener The action to perform when the button is clicked.
     */
    public void setNotificationSettingsBtnOnClickListener(View.OnClickListener onClickListener) {
        this.notificationSettingsOnClickListener = onClickListener;

        if (binding != null) {
            binding.notificationSettingsButton.setOnClickListener(notificationSettingsOnClickListener);
        }
    }
}
