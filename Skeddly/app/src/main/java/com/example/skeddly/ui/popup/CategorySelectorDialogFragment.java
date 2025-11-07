package com.example.skeddly.ui.popup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Dialog fragment for the category selector
 */
public class CategorySelectorDialogFragment extends DialogFragment {
    private ArrayList<String> selectedItems;
    private String requestKey;

    /**
     * Constructor for the CategorySelectorDialogFragment
     * @param title The title of the dialog
     * @param categories The categories to choose from
     * @param requestKey The key to use for the result
     * @return
     */
    public static CategorySelectorDialogFragment newInstance(String title, String[] categories, String requestKey) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("requestKey", requestKey);
        args.putStringArray("categories", categories);

        CategorySelectorDialogFragment popup = new CategorySelectorDialogFragment();
        popup.setArguments(args);

        return popup;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Get args
        Bundle args = requireArguments();

        String title = args.getString("title");
        requestKey = args.getString("requestKey");
        String[] categories = args.getStringArray("categories");

        // Initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Set title
        builder.setTitle(title);

        // Set dialog non cancelable
        builder.setCancelable(false);

        boolean[] selected = new boolean[categories.length];
        selectedItems = new ArrayList<String>();
        builder.setMultiChoiceItems(categories, selected, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {}
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < selected.length; i++) {
                    if (selected[i]) {
                        selectedItems.add(categories[i]);
                    }
                }

                setResult();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.show();
    }

    /**
     * Sets the result of the dialog
     */
    private void setResult() {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("selectedItems", selectedItems);

        getParentFragmentManager().setFragmentResult(requestKey, bundle);
    }
}
