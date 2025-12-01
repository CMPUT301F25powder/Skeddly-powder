package com.example.skeddly.ui.utility;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.View;

import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;

import java.time.format.DateTimeFormatter;

public class InterfaceUtilities {
    public static final CalendarConstraints calendarConstraints = new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build();
    public static final UnderlineSpan underlineSpan = new UnderlineSpan();
    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM. d, yyyy");
    private FragmentManager fragmentManager;

    public InterfaceUtilities(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    /**
     * Setup a date picker and call you back when the user has finished picking the date
     * @param view The view that should have the click associated with
     * @param callback The callback function that should be ran
     */
    public void setupDatePicker(View view, MaterialPickerOnPositiveButtonClickListener<Long> callback) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> mdp = MaterialDatePicker.Builder.datePicker()
                        .setCalendarConstraints(calendarConstraints)
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

                mdp.addOnPositiveButtonClickListener(callback);
                mdp.show(fragmentManager, null);
            }
        });
    }

    /**
     * Setup a time picker and call you back when the user has finished picking the time
     * @param view The view that should have the click associated with
     * @param callback The callback function that should be ran
     */
    public void setupTimePicker(View view, MaterialTimePickerCallback callback) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialTimePicker mtp = new MaterialTimePicker.Builder().build();
                mtp.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.onPositiveButtonClick(mtp);
                    }
                });
                mtp.show(fragmentManager, null);
            }
        });
    }

    /**
     * Underline the given string and return it as a SpannableString
     * @param string The string to underline
     * @return The given string, but underlined as a SpannableString
     */
    public static SpannableString underlineString(String string) {
        SpannableString spannedString = new SpannableString(string);
        spannedString.setSpan(underlineSpan, 0, spannedString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        return spannedString;
    }

    /**
     * Converts dp to px
     * @param context Context
     * @param dp Density pixels
     * @return float pixels
     */
    public static float convertDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
