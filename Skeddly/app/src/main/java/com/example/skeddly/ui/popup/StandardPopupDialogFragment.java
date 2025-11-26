package com.example.skeddly.ui.popup;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.skeddly.R;
import com.example.skeddly.databinding.DialogGenericBinding;

import java.util.Objects;


/**
 * A generic popup that displays the provided title and content strings.
 * The result of this popup is a bundle that contains a single boolean,
 * indicating a positive or negative response from the user.
 */
public class StandardPopupDialogFragment extends DialogFragment {
    private String requestKey = "requestKey";
    private String resultText;
    private boolean result;
    private boolean showTextEntry;

    /**
     * Instantiate the popup with the provided title and content fields.
     * @param title The title that the popup should have
     * @param content The text content that the popup should show
     * @param requestKey The requestKey that should be used when returning the result
     * @param textInput Whether to take in text input from the user
     * @return A new StandardPopupDialogFragment with the arguments passed to it to display.
     */
    public static StandardPopupDialogFragment newInstance(String title, String content,
                                                          String requestKey, boolean textInput) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("content", content);
        args.putString("requestKey", requestKey);
        args.putBoolean("textInput", textInput);

        StandardPopupDialogFragment popup = new StandardPopupDialogFragment();
        popup.setArguments(args);

        return popup;
    }

    /**
     * Instantiate the popup with the provided title and content fields.
     * @param title The title that the popup should have
     * @param content The text content that the popup should show
     * @param requestKey The requestKey that should be used when returning the result
     * @return A new StandardPopupDialogFragment with the arguments passed to it to display.
     */
    public static StandardPopupDialogFragment newInstance(String title, String content,
                                                          String requestKey) {
        return newInstance(title, content, requestKey, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as a dialog or embedded fragment.
        View view = inflater.inflate(R.layout.dialog_generic, container, false);
        DialogGenericBinding binding = DialogGenericBinding.bind(view);

        TextView textTitle = binding.textDialogTitle;
        TextView textContent = binding.textPopupContents;

        Bundle args = getArguments();

        if (args != null) {
            textTitle.setText(args.getString("title"));
            textContent.setText(args.getString("content"));
            requestKey = args.getString("requestKey");
            showTextEntry = args.getBoolean("textInput");
        }

        binding.editInput.setVisibility(showTextEntry ? View.VISIBLE : View.GONE);

        Button buttonCancel = binding.btnCancel;
        Button buttonConfirm = binding.btnConfirm;

        // Default return is false
        result = false;
        resultText = null;

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result = true;
                dismiss();
            }
        });

        Objects.requireNonNull(binding.editInput.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                resultText = charSequence.toString();
            }
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

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        // When dismissed, return the result
        Bundle bundle = new Bundle();
        bundle.putBoolean("buttonChoice", result);
        bundle.putString("typedText", resultText);
        getParentFragmentManager().setFragmentResult(requestKey, bundle);
    }
}
