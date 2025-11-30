package com.example.skeddly.ui.utils;

import com.google.android.material.timepicker.MaterialTimePicker; /**
 * Callback function for the MaterialTimePicker used to pick the time. Includes a reference to the
 * original picker in the callback.
 */
public interface MaterialTimePickerCallback {
    void onPositiveButtonClick(MaterialTimePicker picker);
}
