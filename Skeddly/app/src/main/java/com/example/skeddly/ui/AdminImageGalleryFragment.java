package com.example.skeddly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.skeddly.business.database.repository.EventRepository;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.databinding.FragmentAdminImageGalleryBinding;
import com.example.skeddly.ui.adapter.GalleryImageAdapter;
import com.example.skeddly.ui.popup.ImagePopupDialogFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AdminImageGalleryFragment extends Fragment {
    private FragmentAdminImageGalleryBinding binding;
    private EventRepository eventRepository;
    private ArrayList<String> base64Images;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminImageGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        eventRepository = new EventRepository(db);
        base64Images = new ArrayList<>();

        GridView uploadedImagesView = binding.uploadedImages;
        GalleryImageAdapter galleryImageAdapter = new GalleryImageAdapter(root.getContext(), base64Images);

        eventRepository.getAll().addOnSuccessListener(new OnSuccessListener<List<Event>>() {
            @Override
            public void onSuccess(List<Event> events) {
                for (Event event : events) {
                    base64Images.add(event.getImageb64());
                }

                galleryImageAdapter.notifyDataSetChanged();
            }
        });

        uploadedImagesView.setAdapter(galleryImageAdapter);

        uploadedImagesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String imageB64 = uploadedImagesView.getItemAtPosition(position).toString();

                ImagePopupDialogFragment imagePopupDialogFragment = ImagePopupDialogFragment.newInstance(imageB64);
                imagePopupDialogFragment.show(getChildFragmentManager(), null);
            }
        });

        return root;
    }
}
