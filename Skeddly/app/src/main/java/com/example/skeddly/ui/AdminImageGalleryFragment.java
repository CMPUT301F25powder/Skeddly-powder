package com.example.skeddly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.skeddly.R;
import com.example.skeddly.business.database.DatabaseHandler;
import com.example.skeddly.business.database.repository.EventRepository;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.databinding.FragmentAdminImageGalleryBinding;
import com.example.skeddly.ui.adapter.GalleryImageAdapter;
import com.example.skeddly.ui.popup.ImagePopupDialogFragment;
import com.example.skeddly.ui.utils.GalleryImage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminImageGalleryFragment extends Fragment {
    private FragmentAdminImageGalleryBinding binding;
    private EventRepository eventRepository;
    private DatabaseHandler dbHandler;
    private ArrayList<GalleryImage> images;
    private GalleryImageAdapter galleryImageAdapter;
    private GridView uploadedImagesView;
    private TextView selectedImagesCount;
    private ConstraintLayout imageSelectHeader;
    private ImageButton selectAll;
    private ImageButton closeSelectionMenu;
    private ImageButton deleteSelectedButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminImageGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dbHandler = new DatabaseHandler();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        eventRepository = new EventRepository(db);
        images = new ArrayList<>();

        uploadedImagesView = binding.uploadedImages;
        galleryImageAdapter = new GalleryImageAdapter(root.getContext(), images);
        selectedImagesCount = binding.selectedImagesCount;
        imageSelectHeader = binding.imageSelectHeader;
        selectAll = binding.selectAllBtn;
        closeSelectionMenu = binding.closeSelectionMenuBtn;
        deleteSelectedButton = binding.deleteSelectedBtn;

        imageSelectHeader.setVisibility(View.GONE);

        setListeners();

        uploadedImagesView.setAdapter(galleryImageAdapter);

        return root;
    }

    private void setSelectionMode(boolean selectionMode) {
        galleryImageAdapter.setSelectionMode(selectionMode);
        galleryImageAdapter.notifyDataSetChanged();

        if (selectionMode) {
            imageSelectHeader.setVisibility(View.VISIBLE);
        } else {
            imageSelectHeader.setVisibility(View.GONE);
        }
    }

    private void notifyDataSetChanged() {
        galleryImageAdapter.notifyDataSetChanged();
        selectedImagesCount.setText(getString(R.string.selected_images_count, galleryImageAdapter.getSelectedCount()));
    }

    private void deleteSelected() {
        for (int i = images.size() - 1; i >= 0; i--) {
            GalleryImage image = images.get(i);

            if (image.isSelected()) {
                Event event = image.getEvent();
                event.setImageb64("");
                event.clearImage(dbHandler);
                images.remove(image);
            }
        }

        notifyDataSetChanged();
    }

    private void setListeners() {
        eventRepository.getAll().addOnSuccessListener(new OnSuccessListener<List<Event>>() {
            @Override
            public void onSuccess(List<Event> events) {
                for (Event event : events) {
                    GalleryImage newGalleryImage = new GalleryImage(event);
                    String imageb64 = event.getImageb64();

                    if (!imageb64.isBlank()) {
                        images.add(newGalleryImage);
                    }
                }

                galleryImageAdapter.notifyDataSetChanged();
            }
        });

        deleteSelectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelected();
            }
        });

        closeSelectionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryImageAdapter.toggleSelectAll(false);
                setSelectionMode(false);
            }
        });

        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (galleryImageAdapter.allSelected()) {
                    galleryImageAdapter.toggleSelectAll(false);
                    selectAll.setImageResource(R.drawable.ic_check);
                } else {
                    galleryImageAdapter.toggleSelectAll(true);
                    selectAll.setImageResource(R.drawable.ic_check_filled);
                }

                notifyDataSetChanged();
            }
        });

        uploadedImagesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GalleryImage image = (GalleryImage) uploadedImagesView.getItemAtPosition(position);

                if (galleryImageAdapter.isSelectionMode()) {
                    image.setSelected(!image.isSelected());

                    if (galleryImageAdapter.getSelectedCount() == 0) {
                        setSelectionMode(false);
                    }

                    notifyDataSetChanged();
                } else {
                    String imageB64 = image.getEvent().getImageb64();

                    ImagePopupDialogFragment imagePopupDialogFragment = ImagePopupDialogFragment.newInstance(imageB64);
                    imagePopupDialogFragment.show(getChildFragmentManager(), null);
                }
            }
        });

        uploadedImagesView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!galleryImageAdapter.isSelectionMode()) {
                    GalleryImage image = (GalleryImage) uploadedImagesView.getItemAtPosition(position);

                    image.setSelected(true);

                    setSelectionMode(true);

                    notifyDataSetChanged();
                }

                return true;
            }
        });
    }
}
