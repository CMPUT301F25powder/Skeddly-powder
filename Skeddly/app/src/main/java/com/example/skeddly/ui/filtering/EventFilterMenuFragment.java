package com.example.skeddly.ui.filtering;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skeddly.R;
import com.example.skeddly.databinding.FragmentEventFilterMenuBinding;
import com.example.skeddly.ui.adapter.EventFilterCategoryAdapter;

public class EventFilterMenuFragment extends Fragment {
    private FragmentEventFilterMenuBinding binding;
    private EventFilterCategoryAdapter eventFilterCategoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEventFilterMenuBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Resources resources = getResources();

        eventFilterCategoryAdapter = new EventFilterCategoryAdapter(root.getContext(), resources.getStringArray(R.array.cat_array));

        GridView eventFilterCategories = binding.eventFilterCategories;

        eventFilterCategories.setAdapter(eventFilterCategoryAdapter);

        return root;
    }
}
