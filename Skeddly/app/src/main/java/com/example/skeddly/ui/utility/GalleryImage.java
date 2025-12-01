package com.example.skeddly.ui.utility;

import com.example.skeddly.business.event.Event;

public class GalleryImage {
    private final Event event;
    private boolean selected;

    public GalleryImage(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
