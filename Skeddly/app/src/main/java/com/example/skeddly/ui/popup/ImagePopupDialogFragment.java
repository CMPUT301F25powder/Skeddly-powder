package com.example.skeddly.ui.popup;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.skeddly.R;
import com.example.skeddly.databinding.DialogImageBinding;

import java.util.Base64;

public class ImagePopupDialogFragment extends DialogFragment {

    public static ImagePopupDialogFragment newInstance(String imageB64) {
        Bundle args = new Bundle();
        args.putString("imageB64", imageB64);

        ImagePopupDialogFragment fragment = new ImagePopupDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_image, container, false);
        DialogImageBinding binding = DialogImageBinding.bind(view);

        Bundle args = getArguments();

        ImageView imageView = binding.img;

        if (args != null) {
            Glide.with(view).load(Base64.getDecoder().decode(args.getString("imageB64"))).into(imageView);
        }

        Button buttonClose = binding.btnClose;

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
}
