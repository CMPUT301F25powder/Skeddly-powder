package com.example.skeddly.ui.utility;

import com.example.skeddly.business.event.Event;

public class GalleryImage {
    private final Event event;
    private boolean selected;

    /**
     * Constructor for GalleryImage.
     * @param event The event associated with this image.
     */
    public GalleryImage(Event event) {
        this.event = event;
    }

    /**
     * Get the event associated with this image.
     * @return The event associated with this image.
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Check if this image is selected.
     * @return True if selected, false otherwise.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Set the selection status of this image.
     * @param selected True to select, false to deselect.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
