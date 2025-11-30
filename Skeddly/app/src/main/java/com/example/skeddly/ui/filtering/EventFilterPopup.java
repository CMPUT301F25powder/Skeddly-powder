package com.example.skeddly.ui.filtering;

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

import com.example.skeddly.R;
import com.example.skeddly.business.search.EventFilter;
import com.example.skeddly.ui.adapter.CheckBoxCheckedListener;
import com.example.skeddly.ui.adapter.EventFilterCategoryAdapter;

import java.util.ArrayList;

public class EventFilterPopup extends PopupWindow {
    private boolean filterMenuToggle;
    private EventFilterCategoryAdapter eventFilterCategoryAdapter;
    private ImageButton dropdownButton;
    private FilterUpdatedListener filterUpdatedListener;
    private EventFilter eventFilter;
    public EventFilterPopup(Context context, View popupView, SearchView searchBar, ImageButton dropdownButton) {
        super(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        this.dropdownButton = dropdownButton;
        this.eventFilter = new EventFilter();

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
        EditText startTime = popupView.findViewById(R.id.edit_text_start);
        EditText endTime = popupView.findViewById(R.id.edit_text_end);

        saveFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventFilter == null) {
                    eventFilter = new EventFilter();
                }

                eventFilter.setWeekend(weekendCheckBox.isChecked());
                eventFilter.setWeekday(weekdayCheckBox.isChecked());

                eventFilter.setStartTime(String.valueOf(startTime.getText()));
                eventFilter.setEndTime(String.valueOf(endTime.getText()));

//                ArrayList<String> selectedEvents = new ArrayList<>();
//
//                for (int i = 0; i < eventFilterCategoryAdapter.getCount(); i++) {
//                    String category = eventFilterCategoryAdapter.getItem(i);
//                    ArrayList<String> selectedEventTypes = eventFilter.getSelectedEventTypes();
//
//                    if (selectedEventTypes.contains(category)) {
//                        selectedEvents.add(category);
//                    }
//                }
//
//                eventFilter.setSelectedEventTypes(selectedEvents);

                System.out.println(eventFilter.getSelectedEventTypes());

                if (filterUpdatedListener != null) {
                    filterUpdatedListener.onFilterUpdated();
                }
            }
        });

        clearFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventFilter = null;

                if (filterUpdatedListener != null) {
                    filterUpdatedListener.onFilterUpdated();
                }
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
