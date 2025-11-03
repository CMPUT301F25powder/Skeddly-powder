package com.example.skeddly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.example.skeddly.databinding.FragmentTestBinding;
import com.example.skeddly.ui.popup.MapPopupDialogFragment;
import com.example.skeddly.ui.popup.StandardPopupDialogFragment;
import com.example.skeddly.ui.popup.TimePickerDialogFragment;
import com.google.android.gms.maps.model.LatLng;


public class TestFragment extends Fragment {
    private FragmentTestBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTestBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView returnText = binding.returnText;

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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
