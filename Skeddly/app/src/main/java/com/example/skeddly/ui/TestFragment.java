package com.example.skeddly.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.example.skeddly.databinding.FragmentTestBinding;
import com.example.skeddly.ui.popup.DatePickerDialogFragment;
import com.example.skeddly.ui.popup.MapPopupDialogFragment;
import com.example.skeddly.ui.popup.QRPopupDialogFragment;
import com.example.skeddly.ui.popup.StandardPopupDialogFragment;
import com.example.skeddly.ui.popup.TimePickerDialogFragment;
import com.google.android.gms.maps.model.LatLng;


public class TestFragment extends Fragment {
    private FragmentTestBinding binding;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTestBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView returnText = binding.returnText;
        ImageView photoPickerImage = binding.photoPickerImage;

        // === Generic popup stuff ===
        Button testButton = binding.testButton;
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StandardPopupDialogFragment cdf = StandardPopupDialogFragment.newInstance("Title", "Contents", "testDialog");
                cdf.show(getChildFragmentManager(), null);
            }
        });

        getChildFragmentManager().setFragmentResultListener("testDialog", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                Boolean b = result.getBoolean("buttonChoice");
                returnText.setText(String.format("Popup returned %s", b));
            }
        });

        // === Time picker button stuff ===
        Button timePickerButton = binding.timePickerButton;
        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialogFragment tpf = new TimePickerDialogFragment();
                tpf.show(getChildFragmentManager(), "timePicker");
            }
        });

        getChildFragmentManager().setFragmentResultListener("timePicker", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                int hourOfDay = result.getInt("hourOfDay");
                int minute = result.getInt("minute");

                returnText.setText(String.format("%2d:%2d", hourOfDay, minute));
            }
        });

        // === Date Picker Button Stuff ===
        Button datePickerButton = binding.datePickerButton;
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialogFragment dpf = new DatePickerDialogFragment();
                dpf.show(getChildFragmentManager(), "datePicker");
            }
        });

        getChildFragmentManager().setFragmentResultListener("datePicker", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                int year = result.getInt("year");
                int month = result.getInt("month");
                int day = result.getInt("day");

                returnText.setText(String.format("%d:%d:%d", year, month, day));
            }
        });

        // === Location Picker Stuff ===
        Button locationPickerButton = binding.locationPickerButton;
        locationPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapPopupDialogFragment lpf = MapPopupDialogFragment.newInstance("locationPicker");
                lpf.show(getChildFragmentManager(), "LocationPicker");
            }
        });

        getChildFragmentManager().setFragmentResultListener("locationPicker", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                LatLng location = result.getParcelable("LatLng");

                if (location != null) {
                    returnText.setText(String.format("Latitude is %f, Longitude is %f", location.latitude, location.longitude));
                }
            }
        });


        // === Photo Picker Stuff ===
        // Registers a photo picker activity launcher in single-select mode.
        this.pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        Button photoPickerButton = binding.photoPickerButton;
        photoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        photoPickerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        // === QR Code stuff ===
        Button qrButton = binding.qrButton;
        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRPopupDialogFragment qpf = QRPopupDialogFragment.newInstance("https://github.com/CMPUT301F25powder/Skeddly-powder");
                qpf.show(getChildFragmentManager(), null);
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
