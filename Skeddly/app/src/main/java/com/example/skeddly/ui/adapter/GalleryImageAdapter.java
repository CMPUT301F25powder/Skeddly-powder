package com.example.skeddly.ui.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.skeddly.R;
import com.example.skeddly.ui.utils.GalleryImage;

import java.util.ArrayList;
import java.util.Base64;

public class GalleryImageAdapter extends ArrayAdapter<GalleryImage> {
    private Context context;
    private ArrayList<GalleryImage> images;
    private boolean selectionMode;
    public GalleryImageAdapter(Context context, ArrayList<GalleryImage> images) {
        super(context, 0, images);
        this.context = context;
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
        Glide.with(convertView).load(Base64.getDecoder().decode(image.getBase64String())).into(uploadedImageComponent);

        ImageView checkButton = convertView.findViewById(R.id.checkBtn);

        if (isSelectionMode()) {
            Drawable foregroundDrawable = ResourcesCompat.getDrawable(
                    context.getResources(),
                    R.drawable.gradient_image_select_overlay,
                    context.getTheme()
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

    private void showUnselectedCheck(ImageView checkButton) {
        checkButton.setImageResource(R.drawable.ic_check);
        checkButton.setClickable(true);
        checkButton.setVisibility(ViewGroup.VISIBLE);
    }

    private void showSelectedCheck(ImageView checkButton) {
        checkButton.setImageResource(R.drawable.ic_check_filled);
        checkButton.setClickable(true);
        checkButton.setVisibility(ViewGroup.VISIBLE);
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
    }
}
