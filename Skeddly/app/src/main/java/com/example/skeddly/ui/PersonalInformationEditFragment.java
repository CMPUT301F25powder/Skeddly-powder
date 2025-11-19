package com.example.skeddly.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skeddly.MainActivity;
import com.example.skeddly.business.user.PersonalInformation;
import com.example.skeddly.databinding.FragmentPersonalInfoEditBinding;

/**
 * Fragment for the personal information edit screen
 */
public class PersonalInformationEditFragment extends Fragment {
    private FragmentPersonalInfoEditBinding binding;
    private View.OnClickListener onCompleteListener = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPersonalInfoEditBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                toggleConfirmButton();
            }
        };

        toggleConfirmButton();

        binding.editFullName.addTextChangedListener(textWatcher);
        binding.editEmail.addTextChangedListener(textWatcher);

        binding.include.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName = binding.editFullName.getText().toString();
                String email = binding.editEmail.getText().toString();
                String phoneNumber = binding.editPhoneNumber.getText().toString();

                PersonalInformation newInfo = new PersonalInformation(fullName, email, phoneNumber);
                MainActivity activity = (MainActivity) requireActivity();
                activity.getUser().setPersonalInformation(newInfo);
                activity.notifyUserChanged();

                if (onCompleteListener != null) {
                    onCompleteListener.onClick(view);
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
     * Toggles whether the confirm button should be enabled based on if the fields are filled in
     */
    private void toggleConfirmButton() {
        boolean fullNameFilled = binding.editFullName.length() > 0;
        boolean emailFilled = binding.editEmail.length() > 0;

        binding.include.btnConfirm.setEnabled(fullNameFilled && emailFilled);

        if (fullNameFilled && emailFilled) {
            binding.include.btnConfirm.setAlpha(1f);
        } else {
            binding.include.btnConfirm.setAlpha(.5f);
        }
    }

    /**
     * Sets the listener for what we should do when the confirm button is pressed.
     * Note: User details are already saved into the object automatically by this fragment.
     * @param callback The OnClickListener that we should callback.
     */
    public void setOnCompleteListener(View.OnClickListener callback) {
        this.onCompleteListener = callback;
    }
}
