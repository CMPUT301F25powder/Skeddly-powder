package com.example.skeddly.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.skeddly.R;
import com.example.skeddly.business.event.Event;
import com.example.skeddly.ui.popup.ImagePopupDialogFragment;
import com.example.skeddly.ui.popup.QRPopupDialogFragment;

import java.util.ArrayList;
import java.util.Base64;

public class GalleryImageAdapter extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> base64Images;
    public GalleryImageAdapter(Context context, ArrayList<String> base64Images) {
        super(context, 0, base64Images);
        this.context = context;
        this.base64Images = base64Images;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_gallery_image, parent, false);
        }

        Context context = convertView.getContext();

        String imageB64 = getItem(position);
        ImageView uploadedImageComponent = convertView.findViewById(R.id.uploadedImage);
        Glide.with(convertView).load(Base64.getDecoder().decode(imageB64)).into(uploadedImageComponent);

        return convertView;
    }
}
