package com.example.skeddly.ui.popup;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDateTime;

/**
 * Dialog fragment for the time picker
 */
public class TimePickerDialogFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private String requestKey = "timePicker";

    /**
     * Instantiate the popup with the provided requestKey.
     * @param requestKey The requestKey that should be used when returning the result
     * @return A new TimePickerDialogFragment with the argument passed to it to display.
     */
    public static TimePickerDialogFragment newInstance(String requestKey) {
        Bundle arg = new Bundle();
        arg.putString("requestKey", requestKey);

        TimePickerDialogFragment popup = new TimePickerDialogFragment();
        popup.setArguments(arg);

        return popup;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // ---
        Bundle arg = getArguments();

        if (arg != null) {
            requestKey = arg.getString("requestKey");
        }

        // Use the current time as the default values for the picker.
        LocalDateTime localDateTime = LocalDateTime.now();

        // Create a new instance of TimePickerDialog and return it.
        return new TimePickerDialog(getActivity(), this, localDateTime.getHour(),
                localDateTime.getMinute(), DateFormat.is24HourFormat(getActivity()));
    }

    /**
     * Called when the user has selected a time.
     * @param view the view associated with this listener
     * @param hourOfDay the hour that was set
     * @param minute the minute that was set
     */
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Bundle bundle = new Bundle();
        bundle.putInt("hourOfDay", hourOfDay);
        bundle.putInt("minute", minute);

        getParentFragmentManager().setFragmentResult(requestKey, bundle);
    }
}
