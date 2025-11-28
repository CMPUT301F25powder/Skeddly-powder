package com.example.skeddly.ui.filtering;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SearchView;

import com.example.skeddly.R;
import com.example.skeddly.ui.adapter.EventFilterCategoryAdapter;

public class EventFilterPopup extends PopupWindow {
    private boolean filterMenuToggle;
    private EventFilterCategoryAdapter eventFilterCategoryAdapter;
    private ImageButton dropdownButton;
    public EventFilterPopup(Context context, View popupView, SearchView searchBar, ImageButton dropdownButton) {
        super(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                500,
                true);

        this.dropdownButton = dropdownButton;

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
    }

    private void rotateDropdownButton() {
        dropdownButton.setRotation((dropdownButton.getRotation() + 180) % 360);
    }
}
