package com.example.skeddly.business.event;

import java.util.ArrayList;

public class EventDetail {
    private String name;
    private String description;
    private String entryCriteria;
    private ArrayList<String> categories;

    public EventDetail() {

    }

    public EventDetail(String name, String description, String entryCriteria, ArrayList<String> categories) {
        this.name = name;
        this.description = description;
        this.entryCriteria = entryCriteria;

        if (categories == null) {
            categories = new ArrayList<>();
        }
        this.categories = categories;
    }

    public EventDetail(String name, String description, String entryCriteria) {
        this(name, description, entryCriteria, null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEntryCriteria() {
        return entryCriteria;
    }

    public void setEntryCriteria(String entryCriteria) {
        this.entryCriteria = entryCriteria;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }
}
