package com.example.skeddly.ui.popup;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.skeddly.R;
import com.example.skeddly.databinding.DialogDrawBinding;

import java.util.Objects;

public class DrawParticipantsDialogFragment extends DialogFragment {
    private String requestKey = "requestKey";
    private int curWait;
    private int curAttend;
    private int maxAttend;
    private int result = 0;

    /**
     * Create a new instance of the DrawParticipantsDialogFragment.
     * @param requestKey The requestKey to use for the result
     * @param curWait The current number of waiting participants
     * @param curAttend The current number of attending participants
     * @param maxAttend The maximum number of attending participants
     * @return
     */
    public static DrawParticipantsDialogFragment newInstance(String requestKey, int curWait, int curAttend, int maxAttend) {
        Bundle args = new Bundle();
        args.putString("requestKey", requestKey);
        args.putInt("curWait", curWait);
        args.putInt("curAttend", curAttend);
        args.putInt("maxAttend", maxAttend);

        DrawParticipantsDialogFragment popup = new DrawParticipantsDialogFragment();
        popup.setArguments(args);

        return popup;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_draw, container, false);
        DialogDrawBinding binding = DialogDrawBinding.bind(view);

        // Setup request key
        setupArgs();

        // Initialize text view with the info
        binding.textPopupContents.setText(getString(R.string.dialog_draw_content, curWait, curAttend, maxAttend));

        // They need to type smth in
        binding.btnConfirm.setEnabled(false);

        // Watch the textbox
        Objects.requireNonNull(binding.inputParticipantsAmount.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    try {
                        result = Integer.parseInt(s.toString());
                    } catch (NumberFormatException e) {
                        result = 0;
                    }
                } else {
                    result = 0;
                }

                binding.btnConfirm.setEnabled(shouldButtonBeEnabled());
            }
        });

        // Setup buttons
        binding.btnCancel.setOnClickListener(v -> {
            dismiss();
        });

        binding.btnConfirm.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("drawAmount", result);
            getParentFragmentManager().setFragmentResult(requestKey, bundle);

            dismiss();
        });

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using
        // onCreateView() is to modify the dialog characteristics. For example,
        // the dialog includes a title by default, but your custom layout might
        // not need it. Here, you can remove the dialog title, but you must
        // call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return dialog;
    }

    /**
     * Sets up the arguments for the dialog.
     */
    private void setupArgs() {
        Bundle args = getArguments();

        if (args != null) {
            requestKey = args.getString("requestKey");
            curWait = args.getInt("curWait");
            curAttend = args.getInt("curAttend");
            maxAttend = args.getInt("maxAttend");
        }
    }

    /**
     * Checks if the button should be enabled.
     * @return True if the button should be enabled, false otherwise.
     */
    private boolean shouldButtonBeEnabled() {
        return result > 0 && result <= curWait && curAttend + curWait <= maxAttend;
    }
}
