package com.example.skeddly.ui.popup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.skeddly.R;
import com.google.android.material.textfield.TextInputEditText;

public class SendMessageDialogFragment extends DialogFragment {

    private static final String ARG_RECIPIENT_ID = "recipientId";

    public static SendMessageDialogFragment newInstance(String recipientId) {
        SendMessageDialogFragment fragment = new SendMessageDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RECIPIENT_ID, recipientId);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.send_message_dialog, null);

        TextInputEditText messageEditText = view.findViewById(R.id.edit_text_message);
        Button cancelButton = view.findViewById(R.id.btn_cancel);
        Button sendButton = view.findViewById(R.id.btn_send);

        cancelButton.setOnClickListener(v -> dismiss());

        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString();
            if (!message.isEmpty()) {
                String recipientId = getArguments().getString(ARG_RECIPIENT_ID);
                Bundle result = new Bundle();
                result.putString("message", message);
                result.putString("recipientId", recipientId);
                getParentFragmentManager().setFragmentResult("sendMessage", result);
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }
}
