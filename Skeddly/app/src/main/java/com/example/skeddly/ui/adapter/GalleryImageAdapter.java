package com.example.skeddly.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.example.skeddly.R;
import com.example.skeddly.ui.utility.GalleryImage;

import java.util.ArrayList;
import java.util.Base64;

/**
 * An adapter for showing images in the gallery.
 */
public class GalleryImageAdapter extends ArrayAdapter<GalleryImage> {
    private ArrayList<GalleryImage> images;
    private boolean selectionMode;

    /**
     * Constructor for the GalleryImageAdapter
     * @param context Context of the app
     * @param images List of images to display
     */
    public GalleryImageAdapter(Context context, ArrayList<GalleryImage> images) {
        super(context, 0, images);
        this.images = images;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_gallery_image, parent, false);
        }

        GalleryImage image = getItem(position);
        ImageView uploadedImageComponent = convertView.findViewById(R.id.uploadedImage);
        Glide.with(convertView).load(Base64.getDecoder().decode(image.getEvent().getImageb64())).into(uploadedImageComponent);

        ImageView checkButton = convertView.findViewById(R.id.checkBtn);

        if (isSelectionMode()) {
            Drawable foregroundDrawable = ResourcesCompat.getDrawable(
                    getContext().getResources(),
                    R.drawable.gradient_image_select_overlay,
                    getContext().getTheme()
            );
            uploadedImageComponent.setForeground(foregroundDrawable);

            showUnselectedCheck(checkButton);
        } else {
            uploadedImageComponent.setForeground(null);
            checkButton.setVisibility(ViewGroup.GONE);
        }

        if (image.isSelected()) {
            showSelectedCheck(checkButton);
        } else {
            uploadedImageComponent.setForeground(null);
        }

        return convertView;
    }

    /**
     * Gets the number of images selected
     * @return The number of images selected
     */
    public int getSelectedCount() {
        int count = 0;

        for (int i = 0; i < this.getCount(); i++) {
            GalleryImage image = this.getItem(i);

            if (image != null && image.isSelected()) {
                count++;
            }
        }

        return count;
    }

    /**
     * Shows the unselected check
     * @param checkButton The check button to show
     */
    private void showUnselectedCheck(ImageView checkButton) {
        checkButton.setImageResource(R.drawable.ic_check);
        checkButton.setVisibility(ViewGroup.VISIBLE);
    }

    /**
     * Shows the selected check
     * @param checkButton The check button to show
     */
    private void showSelectedCheck(ImageView checkButton) {
        checkButton.setImageResource(R.drawable.ic_check_filled);
        checkButton.setVisibility(ViewGroup.VISIBLE);
    }

    /**
     * Returns if in selection mode
     * @return True if in selection mode. False otherwise.
     */
    public boolean isSelectionMode() {
        return selectionMode;
    }

    /**
     * Sets if in selection mode
     * @param selectionMode True if in selection mode. False otherwise.
     */
    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
    }

    /**
     * Toggles the selection of all images
     * @param toggle True if the images should be selected. False otherwise.
     */
    public void toggleSelectAll(boolean toggle) {
        for (int i = 0; i < this.getCount(); i++) {
            GalleryImage image = this.getItem(i);

            if (image != null) {
                image.setSelected(toggle);
            }
        }
    }

    /**
     * Checks if all images are selected
     * @return True if all images are selected. False otherwise.
     */
    public boolean allSelected() {
        return getSelectedCount() == getCount();
    }
}
