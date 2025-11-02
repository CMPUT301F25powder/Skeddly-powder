package com.example.skeddly.ui.popup;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDateTime;

public class TimePickerDialogFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker.
        LocalDateTime localDateTime = LocalDateTime.now();

        // Create a new instance of TimePickerDialog and return it.
        return new TimePickerDialog(getActivity(), this, localDateTime.getHour(),
                localDateTime.getMinute(), DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Bundle bundle = new Bundle();
        bundle.putInt("hourOfDay", hourOfDay);
        bundle.putInt("minute", minute);

        getParentFragmentManager().setFragmentResult("timePicker", bundle);
    }
}
