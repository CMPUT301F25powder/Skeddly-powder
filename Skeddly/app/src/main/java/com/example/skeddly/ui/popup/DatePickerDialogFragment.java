package com.example.skeddly.ui.popup;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

/**
 *
 */
public class DatePickerDialogFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private String requestKey = "datePicker";

    /**
     * Instantiate the popup with the provided requestKey.
     * @param requestKey The requestKey that should be used when returning the result
     * @return A new DatePickerDialogFragment with the argument passed to it to display.
     */
    public static DatePickerDialogFragment newInstance(String requestKey) {
        Bundle arg = new Bundle();
        arg.putString("requestKey", requestKey);

        DatePickerDialogFragment popup = new DatePickerDialogFragment();
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

        // Use the current date as the default date in the picker.
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it.
        return new DatePickerDialog(requireContext(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Bundle bundle = new Bundle();
        bundle.putInt("year", year);
        bundle.putInt("month", month);
        bundle.putInt("day", day);

        getParentFragmentManager().setFragmentResult(requestKey, bundle);
    }
}
