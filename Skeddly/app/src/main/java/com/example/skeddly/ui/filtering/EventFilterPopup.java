package com.example.skeddly.ui.filtering;

import static com.example.skeddly.ui.utils.InterfaceUtilities.timeFormatter;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.example.skeddly.R;
import com.example.skeddly.business.search.EventFilter;
import com.example.skeddly.ui.adapter.CheckBoxCheckedListener;
import com.example.skeddly.ui.adapter.EventFilterCategoryAdapter;
import com.example.skeddly.ui.utils.InterfaceUtilities;
import com.example.skeddly.ui.utils.MaterialTimePickerCallback;
import com.google.android.material.timepicker.MaterialTimePicker;

import org.w3c.dom.Text;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class EventFilterPopup extends PopupWindow {
    private InterfaceUtilities interfaceUtilities;
    private boolean filterMenuToggle;
    private EventFilterCategoryAdapter eventFilterCategoryAdapter;
    private ImageButton dropdownButton;
    private FilterUpdatedListener filterUpdatedListener;
    private EventFilter eventFilter;
    public EventFilterPopup(Context context, FragmentManager fragmentManager, View popupView, SearchView searchBar, ImageButton dropdownButton) {
        super(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        this.dropdownButton = dropdownButton;
        this.eventFilter = new EventFilter();
        this.interfaceUtilities = new InterfaceUtilities(fragmentManager);

        dropdownButton.setOnClickListener(v -> {
            if (filterMenuToggle) {
                dismiss();
            } else {
                showAsDropDown(searchBar);
            }

            rotateDropdownButton();

            filterMenuToggle = !filterMenuToggle;
        });

        setOnDismissListener(() -> {
            rotateDropdownButton();

            filterMenuToggle = false;
        });

        Resources resources = context.getResources();

        eventFilterCategoryAdapter = new EventFilterCategoryAdapter(context, resources.getStringArray(R.array.cat_array));

        GridView eventFilterCategories = popupView.findViewById(R.id.event_filter_categories);

        eventFilterCategories.setAdapter(eventFilterCategoryAdapter);

        eventFilterCategoryAdapter.setOnCheckBoxCheckedListener(new CheckBoxCheckedListener() {
            @Override
            public void onCheckBoxChecked(String category, boolean checked) {
                ArrayList<String> newCategories;

                if (eventFilter.getSelectedEventTypes() == null) {
                    newCategories = new ArrayList<>();
                } else {
                    newCategories = (ArrayList<String>) eventFilter.getSelectedEventTypes().clone();
                }

                System.out.printf("%s - %s\n", category, checked);
                System.out.println(eventFilter.getSelectedEventTypes());

                if (checked) {
                    if (!newCategories.contains(category)) {
                        newCategories.add(category);
                    }
                } else {
                    newCategories.remove(category);
                }

                eventFilter.setSelectedEventTypes(newCategories);
            }
        });

        // Clear / save
        Button clearFiltersButton = popupView.findViewById(R.id.btn_clear_filter);
        Button saveFiltersButton = popupView.findViewById(R.id.btn_save_filter);

        // Base check boxes
        CheckBox weekendCheckBox = popupView.findViewById(R.id.chk_weekend);
        CheckBox weekdayCheckBox = popupView.findViewById(R.id.chk_weekday);

        // Availability range
        TextView startTime = popupView.findViewById(R.id.edit_text_start);
        TextView endTime = popupView.findViewById(R.id.edit_text_end);

        saveFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventFilter == null) {
                    eventFilter = new EventFilter();
                }

                eventFilter.setWeekend(weekendCheckBox.isChecked());
                eventFilter.setWeekday(weekdayCheckBox.isChecked());

                if (filterUpdatedListener != null) {
                    filterUpdatedListener.onFilterUpdated(false);
                }
            }
        });

        clearFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventFilter = null;

                if (filterUpdatedListener != null) {
                    filterUpdatedListener.onFilterUpdated(true);
                }
            }
        });

        interfaceUtilities.setupTimePicker(popupView.findViewById(R.id.edit_text_start), new MaterialTimePickerCallback() {
            @Override
            public void onPositiveButtonClick(MaterialTimePicker picker) {
                LocalTime eventStartTime = LocalTime.of(picker.getHour(), picker.getMinute());
                startTime.setText(InterfaceUtilities.underlineString(eventStartTime.format(timeFormatter)));
                eventFilter.setStartTime(eventStartTime);
            }
        });

        interfaceUtilities.setupTimePicker(popupView.findViewById(R.id.edit_text_end), new MaterialTimePickerCallback() {
            @Override
            public void onPositiveButtonClick(MaterialTimePicker picker) {
                LocalTime eventEndTime = LocalTime.of(picker.getHour(), picker.getMinute());
                endTime.setText(InterfaceUtilities.underlineString(eventEndTime.format(timeFormatter)));
                eventFilter.setEndTime(eventEndTime);
            }
        });
    }

    public void setOnFilterUpdatedListener(FilterUpdatedListener listener) {
        this.filterUpdatedListener = listener;
    }

    public EventFilter getEventFilter() {
        return this.eventFilter;
    }

    private void rotateDropdownButton() {
        dropdownButton.setRotation((dropdownButton.getRotation() + 180) % 360);
    }
}
