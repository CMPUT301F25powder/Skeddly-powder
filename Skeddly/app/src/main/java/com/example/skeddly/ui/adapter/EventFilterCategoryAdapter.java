package com.example.skeddly.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.skeddly.R;

public class EventFilterCategoryAdapter extends ArrayAdapter<String> {
    public EventFilterCategoryAdapter(Context context, String[] categories) {
        super(context, 0, categories);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_event_filter_category, parent, false);
        }

        CheckBox button = convertView.findViewById(R.id.btn_check);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setChecked(!button.isChecked());
            }
        });

        return convertView;
    }
}
