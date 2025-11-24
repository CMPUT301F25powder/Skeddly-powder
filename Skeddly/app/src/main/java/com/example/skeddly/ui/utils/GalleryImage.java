package com.example.skeddly.ui.utils;

public class GalleryImage {
    private String base64String;
    private boolean selected;

    public GalleryImage(String base64String) {
        this.base64String = base64String;
    }

    public String getBase64String() {
        return base64String;
    }

    public void setBase64String(String base64String) {
        this.base64String = base64String;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
