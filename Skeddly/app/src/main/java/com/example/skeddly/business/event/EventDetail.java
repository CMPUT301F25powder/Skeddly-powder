package com.example.skeddly.business.event;

import java.util.ArrayList;

public class EventDetail {
    private String name;
    private String description;
    private ArrayList<String> categories;

    public EventDetail() {

    }

    public EventDetail(String name, String description, ArrayList<String> categories) {
        this.name = name;
        this.description = description;
        this.categories = categories;
    }

    public EventDetail(String name, String description) {
        this(name, description, new ArrayList<>());
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

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }
}
