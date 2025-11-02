package com.example.skeddly.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skeddly.MainActivity;
import com.example.skeddly.databinding.CreateEditEventViewBinding;
import com.example.skeddly.databinding.CreateFragmentBinding;

import java.util.ArrayList;
import java.util.Collections;


public class CreateFragment extends Fragment {
    private CreateEditEventViewBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CreateEditEventViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Variables
        ImageButton buttonBack = binding.buttonBack;
        Button buttonConfirm = binding.confirmButton;

        TextView textCategorySelector = binding.textCategorySelector;
        String[] catArray = {"Indoor", "Outdoor", "In-person", "Virtual", "Hybrid", "Arts & Crafts",
                "Physical activity", "???????????", "??????????", "???????????", "??????????"};
        boolean[] selectedCategory = new boolean[catArray.length];

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonBack.setVisibility(View.INVISIBLE);

        textCategorySelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                // Set title
                builder.setTitle("Select Category");

                // Set dialog non cancelable
                builder.setCancelable(false);

                builder.setMultiChoiceItems(catArray, selectedCategory, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {}
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Initialize string builder
                        ArrayList<String> categories = new ArrayList<>();

                        for (int i = 0; i < selectedCategory.length; i++) {
                            if (selectedCategory[i]) {
                                categories.add(catArray[i]);
                            }
                        }

                        String result = String.join(", ", categories);

                        textCategorySelector.setText(result);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < selectedCategory.length; i++) {
                            // Remove all selection
                            selectedCategory[i] = false;

                            textCategorySelector.setText("");
                        }
                    }
                });

                builder.show();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
