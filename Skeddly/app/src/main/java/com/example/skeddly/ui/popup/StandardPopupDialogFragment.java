package com.example.skeddly.ui.popup;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.skeddly.R;
import com.example.skeddly.databinding.PopupGenericBinding;


/**
 * A generic popup that displays the provided title and content strings.
 * The result of this popup is a bundle that contains a single boolean,
 * indicating a positive or negative response from the user.
 */
public class StandardPopupDialogFragment extends DialogFragment {

    /**
     * Instantiate the popup with the provided title and content fields.
     * @param title The title that the popup should have
     * @param content The text content that the popup should show
     * @return A new StandardPopupDialogFragment with the arguments passed to it to display.
     */
    public static StandardPopupDialogFragment newInstance(String title, String content) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("content", content);

        StandardPopupDialogFragment popup = new StandardPopupDialogFragment();
        popup.setArguments(args);

        return popup;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as a dialog or embedded fragment.
        View view = inflater.inflate(R.layout.popup_generic, container, false);
        PopupGenericBinding binding = PopupGenericBinding.bind(view);

        TextView textTitle = binding.textPopupTitle;
        TextView textContent = binding.textPopupContents;

        Bundle args = getArguments();

        if (args != null) {
            textTitle.setText(args.getString("title"));
            textContent.setText(args.getString("content"));
        }

        Button buttonCancel = binding.buttonCancel;
        Button buttonConfirm = binding.buttonConfirm;

        // Default return is false
        setPopupResult(false);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPopupResult(true);
                dismiss();
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


    /**
     * Sets the result of the fragment to the supplied boolean.
     * @param result The result that the fragment should return.
     */
    private void setPopupResult(boolean result) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("buttonChoice", result);
        getParentFragmentManager().setFragmentResult("requestKey", bundle);
    }
}
