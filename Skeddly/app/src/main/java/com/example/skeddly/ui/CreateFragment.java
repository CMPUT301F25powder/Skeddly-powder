package com.example.skeddly.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.example.skeddly.databinding.CreateEditEventViewBinding;
import com.example.skeddly.ui.popup.DatePickerDialogFragment;
import com.example.skeddly.ui.popup.MapPopupDialogFragment;
import com.example.skeddly.ui.popup.TimePickerDialogFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;


public class CreateFragment extends Fragment {
    private CreateEditEventViewBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CreateEditEventViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Variables
        ImageButton buttonBack = binding.buttonBack;
        ImageButton buttonQrCode = binding.buttonQrCode;

        MaterialSwitch switchRecurrence = binding.switchRecurrence;

        TextView textDateStart = binding.textDateStart;
        TextView textDateDash = binding.textDateDash;
        TextView textDateFinish = binding.textDateFinish;

        TextView textDayOfWeek = binding.textDayOfWeek;
        String[] dayArray = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday",
                "Sunday"};
        String dayTitle = "Select Day";

        TextView textTimeStart = binding.textTimeStart;
        TextView textTimeFinish = binding.textTimeFinish;

        TextView textCategorySelector = binding.textCategorySelector;
        String[] catArray = {"Indoor", "Outdoor", "In-person", "Virtual", "Hybrid", "Arts & Crafts",
                "Physical activity", "???????????", "??????????", "???????????", "??????????"};
        String categoryTitle = "Select Category";

        Button buttonConfirm = binding.confirmButton;

        // Hide them because we don't want them here
        buttonBack.setVisibility(View.INVISIBLE);
        buttonQrCode.setVisibility(View.INVISIBLE);

        TextView textEventTitleOverlay = binding.textEventTitleOverlay;

        textEventTitleOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapPopupDialogFragment lpf = MapPopupDialogFragment.newInstance("locationPicker");
                lpf.show(getChildFragmentManager(), "LocationPicker");
            }
        });

        getChildFragmentManager().setFragmentResultListener("locationPicker", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                LatLng location = result.getParcelable("LatLng");

                if (location != null) {
                    textEventTitleOverlay.setText(String.format(Locale.getDefault(), "%.2f, %.2f", location.latitude, location.longitude));
                }
            }
        });

        textDateDash.setVisibility(View.INVISIBLE);
        textDateFinish.setVisibility(View.INVISIBLE);
        textDayOfWeek.setVisibility(View.GONE);

        switchRecurrence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textDateDash.setVisibility(View.VISIBLE);
                    textDateFinish.setVisibility(View.VISIBLE);
                    textDayOfWeek.setVisibility(View.VISIBLE);
                } else {
                    textDateDash.setVisibility(View.INVISIBLE);
                    textDateFinish.setVisibility(View.INVISIBLE);
                    textDayOfWeek.setVisibility(View.GONE);
                }
            }
        });

        textDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialogFragment dpf = DatePickerDialogFragment.newInstance("dateStart");
                dpf.show(getChildFragmentManager(), "dateStart");
            }
        });

        getChildFragmentManager().setFragmentResultListener("dateStart",
                this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                int year = result.getInt("year");

                int monthNum = result.getInt("month");
                String month = Month.of(monthNum + 1)
                        .getDisplayName(TextStyle.SHORT, Locale.getDefault());

                int day = result.getInt("day");

                textDateStart.setText(String.format("%d, %s, %d", year, month, day));
            }
        });

        textDateFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialogFragment dpf = DatePickerDialogFragment.newInstance("dateFinish");
                dpf.show(getChildFragmentManager(), "dateFinish");
            }
        });

        getChildFragmentManager().setFragmentResultListener("dateFinish",
                this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                int year = result.getInt("year");

                int monthNum = result.getInt("month");
                String month = Month.of(monthNum + 1).getDisplayName(TextStyle.SHORT, Locale.getDefault());

                int day = result.getInt("day");

                textDateFinish.setText(String.format("%d, %s, %d", year, month, day));
            }
        });

        setupSelector(textDayOfWeek, dayArray, dayTitle);

        textTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialogFragment tpf = TimePickerDialogFragment.newInstance("timeStart");
                tpf.show(getChildFragmentManager(), "timeStart");
            }
        });

        getChildFragmentManager().setFragmentResultListener("timeStart",
                this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                int hourOfDay = result.getInt("hourOfDay");
                int minute = result.getInt("minute");

                textTimeStart.setText(String.format("%d:%02d", hourOfDay, minute));
            }
        });

        textTimeFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialogFragment tpf = TimePickerDialogFragment.newInstance("timeFinish");
                tpf.show(getChildFragmentManager(), "timeFinish");
            }
        });

        getChildFragmentManager().setFragmentResultListener("timeFinish",
                this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                int hourOfDay = result.getInt("hourOfDay");
                int minute = result.getInt("minute");

                textTimeFinish.setText(String.format("%d:%02d", hourOfDay, minute));
            }
        });

        setupSelector(textCategorySelector, catArray, categoryTitle);

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Fill in functionality
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
     *
     * @param textSelector
     * @param array
     */
    private void setupSelector(TextView textSelector, String[] array, String title) {
        boolean[] selected = new boolean[array.length];

        textSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                // Set title
                builder.setTitle(title);

                // Set dialog non cancelable
                builder.setCancelable(false);

                builder.setMultiChoiceItems(array, selected, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {}
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<String> categories = new ArrayList<>();

                        for (int i = 0; i < selected.length; i++) {
                            if (selected[i]) {
                                categories.add(array[i]);
                            }
                        }

                        String result = String.join(", ", categories);

                        textSelector.setText(result);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < selected.length; i++) {
                            // Remove all selection
                            selected[i] = false;

                            textSelector.setText("");
                        }
                    }
                });

                builder.show();
            }
        });
    }
}
