package com.example.skeddly.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.example.skeddly.databinding.FragmentTestBinding;
import com.example.skeddly.ui.popup.StandardPopupDialogFragment;


public class TestFragment extends Fragment {
    private FragmentTestBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTestBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button testButton = binding.testButton;
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StandardPopupDialogFragment cdf = StandardPopupDialogFragment.newInstance("Title", "Contents");
                cdf.show(getChildFragmentManager(), null);
            }
        });

        getChildFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                Boolean b = result.getBoolean("buttonChoice");
                Log.v("Popup", String.format("Popup returned %s", b));
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
