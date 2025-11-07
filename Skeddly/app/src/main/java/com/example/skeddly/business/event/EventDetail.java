package com.example.skeddly.business.event;

import java.util.ArrayList;

/**
 * An event detail that can be serialized into the DB
 */
public class EventDetail {
    private String name;
    private String description;
    private ArrayList<String> categories;

    /**
     * No arg Constructor for the EventDetail
     */
    public EventDetail() {

    }

    /**
     * Constructor for the EventDetail
     * @param name The name of the event
     * @param description The description of the event
     * @param categories The categories of the event
     */
    public EventDetail(String name, String description, ArrayList<String> categories) {
        this.name = name;
        this.description = description;

        if (categories == null) {
            categories = new ArrayList<String>();
        }
        this.categories = categories;
    }

    /**
     * Constructor for the EventDetail
     * @param name The name of the event
     * @param description The description of the event
     */
    public EventDetail(String name, String description) {
        this(name, description, new ArrayList<>());
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
