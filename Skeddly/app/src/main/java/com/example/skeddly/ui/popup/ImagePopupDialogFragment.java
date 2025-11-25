package com.example.skeddly.ui.popup;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private boolean result;
    private String requestKey = "requestKey";

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

        // Default return is false
        result = false;

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        // When dismissed, return the result
        Bundle bundle = new Bundle();
        bundle.putBoolean("buttonChoice", result);
        getParentFragmentManager().setFragmentResult(requestKey, bundle);
    }
}
