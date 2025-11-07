package com.example.skeddly.business.event;

import java.util.ArrayList;

/**
 * This class represents specific details about an event.
 */
public class EventDetail {
    private String name;
    private String description;
    private String entryCriteria;
    private ArrayList<String> categories;

    /**
     * No arg Constructor for EventDetails. Required by Firebase.
     */
    public EventDetail() {

    }

    /**
     * Constructor for the EventDetail
     * @param name The name of the event
     * @param description The description of the event
     * @param categories The categories of the event
     */
    public EventDetail(String name, String description, String entryCriteria, ArrayList<String> categories) {
        this.name = name;
        this.description = description;
        this.entryCriteria = entryCriteria;

        if (categories == null) {
            categories = new ArrayList<>();
        }
        this.categories = categories;
    }

    /**
     * Constructor for the EventDetail
     * @param name The name of the event
     * @param description The description of the event
     */
    public EventDetail(String name, String description, String entryCriteria) {
        this(name, description, entryCriteria, null);
    }

    /**
     * Gets the name of the event
     * @return The name of the event
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the event
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /** Gets the description of the event
     * @return The description of the event
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the event
     * @param description The description of the event
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the entry criteria for the event.
     * @return A string containing the entry criteria.
     */
    public String getEntryCriteria() {
        return entryCriteria;
    }

    /**
     * Sets the entry criteria for the event.
     * @param entryCriteria A string containing the entry criteria.
     */
    public void setEntryCriteria(String entryCriteria) {
        this.entryCriteria = entryCriteria;
    }

    /**
     * Gets the categories of the event
     * @return The categories of the event
     */
    public ArrayList<String> getCategories() {
        return categories;
    }

    /**
     * Sets the categories of the event
     * @param categories The categories of the event
     */
    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }
}
